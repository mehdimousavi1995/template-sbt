package serializer

import akka.actor.ExtendedActorSystem
import akka.remote.serialization.ProtobufSerializer
import im.actor.serialization.ActorSerializer

class ActorProtobufSerializer(system: ExtendedActorSystem) extends ActorSerializer {
  val protoSer = new ProtobufSerializer(system)

  override def identifier: Int = super.identifier + 1

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
    val messageId = manifest.toInt
    ActorSerializer.get(messageId) match {
      case Some(clazz) ⇒
        protoSer.fromBinary(bytes, Some(clazz))
      case None ⇒ throw new IllegalArgumentException(s"Can't find mapping for id: ${messageId}")
    }
  }

  override def toBinary(o: AnyRef): Array[Byte] = protoSer.toBinary(o)
}
