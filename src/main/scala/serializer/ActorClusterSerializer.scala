package serializer

import akka.actor.ExtendedActorSystem
import akka.serialization.JavaSerializer
import im.actor.serialization.ActorSerializer

import scala.util.Try

final class ActorClusterSerializer(system: ExtendedActorSystem) extends ActorSerializer {
  private val javaSer = new JavaSerializer(system)
  private val javaSerPrefix = "j_"

  override def identifier: Int = super.identifier + 1

  //class.toString reaches 'ClassNotFoundException' while deserialization in Class.forName method!
  override def manifest(o: AnyRef): String =
    Try(super.manifest(o)).getOrElse(s"${javaSerPrefix}${o.getClass.getName}")

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef =
    if (manifest.startsWith(javaSerPrefix))
      javaSer.fromBinary(bytes, Class.forName(manifest.drop(javaSerPrefix.length)))
    else super.fromBinary(bytes, manifest)

  //for not registered classes which extend GeneratedMessage super.toBinary does not
  //  throw Exception and so we are serializing them with ActorSerializer and sending
  //  a bad manifest (class name starting with j_) with them
  override def toBinary(o: AnyRef): Array[Byte] = {
    try {
      if (manifest(o).startsWith(javaSerPrefix)) {
        javaSer.toBinary(o)
      } else
        super.toBinary(o)
    } catch {
      //TODO change it
      case e: IllegalArgumentException ⇒ throw e
    }
  }
}