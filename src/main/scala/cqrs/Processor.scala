package cqrs

import java.net.InetAddress
import java.sql.SQLException

import akka.actor.ActorLogging
import akka.persistence._

import scala.concurrent.Future
import scala.util.control.NoStackTrace
import akka.pattern.pipe

trait ProcessorState[S] {
  def updated(e: Event): S

  def withSnapshot(metadata: SnapshotMetadata, snapshot: Any): S

  def snapshot: Any = this
}

abstract class ProcessorError(msg: String) extends RuntimeException(msg) with NoStackTrace

trait PersistenceDebug extends PersistentActor with ActorLogging {

  override def preRestart(cause: Throwable, message: Option[Any]): Unit = {
    super.preRestart(cause, message)
    log.error(cause, "Failure while handling message: {}", message)
  }

  override protected def onPersistFailure(cause: Throwable, event: Any, seqNr: Long): Unit = {
    super.onPersistFailure(cause, event, seqNr)

    cause match {
      case e: SQLException ⇒ log.error(e.getNextException, "Next exception:")
      case o               ⇒ log.error(o, o.getMessage)
    }
  }
}

trait PersistConfig {
  val SnapshotCommitsThreshold = 100
}

trait IncrementalSnapshots[S <: ProcessorState[S]] extends ProcessorStateControl[S] with PersistenceDebug with PersistConfig {

  private var _commitsNum = 0L
  private var _savingSequenceNr = 0L

  override protected def afterCommit(e: Event): Unit = {
    super.afterCommit(e)
    _commitsNum += 1
    if (_commitsNum >= SnapshotCommitsThreshold && _savingSequenceNr != snapshotSequenceNr) {
      log.debug("Saving snapshot due to threshold hit")
      _commitsNum = 0
      _savingSequenceNr = snapshotSequenceNr
      saveSnapshot(state.snapshot)
    }
  }

  override protected def afterRecovery(lastSeqNumber: Long): Unit = {
    super.afterRecovery(lastSeqNumber)
    _commitsNum = lastSeqNumber % SnapshotCommitsThreshold
  }

}

trait ProcessorStateControl[S <: ProcessorState[S]] {
  private[this] var _state: S = getInitialState

  protected def getInitialState: S

  final def state: S = _state

  def setState(state: S) = this._state = state

  def commit(e: Event): S = {
    beforeCommit(e)
    setState(state.updated(e))
    afterCommit(e)
    state
  }

  def applySnapshot(metadata: SnapshotMetadata, snapshot: Any): Unit = {
    beforeSnapshotApply(metadata, snapshot)
    setState(state.withSnapshot(metadata, snapshot))
    afterSnapshotApply(metadata, snapshot)
  }

  protected def beforeCommit(e: Event) = {}

  protected def afterCommit(e: Event) = {}

  protected def afterRecovery(lastSeqNumber: Long) = {}

  protected def beforeSnapshotApply(metadata: SnapshotMetadata, snapshot: Any): Unit = {}

  protected def afterSnapshotApply(metadata: SnapshotMetadata, snapshot: Any): Unit = {}
}

object ProcessorStateProbe {
  def apply[S <: ProcessorState[S]](initial: S) = new ProcessorStateProbe[S](initial)
}

final class ProcessorStateProbe[S <: ProcessorState[S]](initial: S) extends ProcessorStateControl[S] {
  override protected def getInitialState: S = initial
}

abstract class Processor[S <: ProcessorState[S]]
  extends ProcessorStateControl[S]
  with PersistenceDebug
  with PersistConfig {

  type CommandHandler = PartialFunction[Any, Unit]
  type QueryHandler = PartialFunction[Any, Future[Any]]

  import context.dispatcher

  override def preStart(): Unit = {
    super.preStart()
    log.debug(
      "trace: " + persistenceId + " is starting in {} : {}",
      //      InetAddress.getLocalHost().getHostName,
      InetAddress.getLocalHost().getHostAddress)
  }

  override def postStop(): Unit = {
    super.postStop()
    log.debug(
      "trace: " + persistenceId + " was stopped  in {} : {}",
      //      InetAddress.getLocalHost().getHostName,
      InetAddress.getLocalHost().getHostAddress)
  }

  override def postRestart(reason: Throwable): Unit = {
    super.postRestart(reason)
    log.debug(
      "trace: " + persistenceId + " was restarted  in {} : {}, reason: {}",
      InetAddress.getLocalHost().getHostName,
      InetAddress.getLocalHost().getHostAddress,
      reason)
  }

  override def receiveCommand = handleCommand orElse (handleQuery andThen (_ pipeTo sender())).asInstanceOf[Receive] orElse {
    case SaveSnapshotSuccess(metadata: SnapshotMetadata) ⇒
      log.debug("deleting snapshots, metadata: {}", metadata)
      //Keep the last 3 snapshots
      deleteSnapshots(SnapshotSelectionCriteria(metadata.sequenceNr - 3 * SnapshotCommitsThreshold))
      deleteMessages(metadata.sequenceNr - 3 * SnapshotCommitsThreshold)
      log.debug("Snapshot was deleted successfully, metadata: {}", metadata)
    case SaveSnapshotFailure(metadata, cause) ⇒
      log.error(cause, "Failed to save snapshot, metadata: {}", metadata)
  }

  override def unhandled(message: Any): Unit = {
    log.warning(s"Unhandled message of class ${message.getClass.getName}: $message")
    super.unhandled(message)
  }

  override final def receiveRecover = {
    case e: Event ⇒
      commit(e)
    case SnapshotOffer(metadata, snapshot) ⇒
      applySnapshot(metadata, snapshot)
    case RecoveryCompleted ⇒ onRecoveryCompleted()
  }

  protected def handleCommand: Receive

  protected def handleQuery: PartialFunction[Any, Future[Any]]

  protected def onRecoveryCompleted() = {
    afterRecovery(lastSequenceNr)
  }

  protected def reply(msg: AnyRef): Unit = sender() ! msg

  protected def replyFuture(msgFuture: Future[Any]): Unit = msgFuture pipeTo sender()

  protected def saveSnapshotIfNeeded(): Unit = {}

}
