package homeee

import akka.persistence.SnapshotMetadata
import cqrs.{Event, ProcessorState}
import messages.homeee.homessages.HomeEvents.HomeCreated
import messages.homeee.homessages.{Device, HomeSnapShot, Owner}
private[homeee] object HomeState {

  def initial(homeId: Int) = HomeState(
    homeId,
    Owner("", "", "", 0),
    Seq.empty[Device],
    "",
    0
  )
}

private[homeee] final case class HomeState(
  homeId: Int,
  owner: Owner,
  devices: Seq[Device],
  address: String,
  houseArea: Int

) extends ProcessorState[HomeState] {

  override def updated(e: Event): HomeState = {
    e match {
      case e: HomeCreated ⇒
        this.copy(e.homeId, e.owner, Seq.empty[Device], e.address, e.houseArea)

    }
  }

  override def withSnapshot(metadata: SnapshotMetadata, snapshot: Any): HomeState = snapshot match {
    case hs: HomeSnapShot ⇒
      copy(
        homeId = hs.homeId,
        owner = hs.owner,
        devices = hs.devices,
        address = hs.address,
        houseArea = hs.houseArea
      )
  }

  override lazy val snapshot = HomeSnapShot(
    homeId = homeId,
    owner = owner,
    devices = devices,
    address = address,
    houseArea = houseArea
  )
}

