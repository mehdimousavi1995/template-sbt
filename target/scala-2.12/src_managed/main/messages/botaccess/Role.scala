// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO2

package messages.botaccess
import cqrs.TypeMappers._
import scala.collection.JavaConversions._

@SerialVersionUID(0L)
final case class Role(
    roleName: _root_.scala.Predef.String,
    roles: _root_.scala.collection.Seq[messages.botaccess.Access] = _root_.scala.collection.Seq.empty
    ) extends scalapb.GeneratedMessage with scalapb.Message[Role] with scalapb.lenses.Updatable[Role] {
    @transient
    private[this] var __serializedSizeCachedValue: _root_.scala.Int = 0
    private[this] def __computeSerializedValue(): _root_.scala.Int = {
      var __size = 0
      __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(1, roleName)
      roles.foreach(roles => __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(roles.serializedSize) + roles.serializedSize)
      __size
    }
    final override def serializedSize: _root_.scala.Int = {
      var read = __serializedSizeCachedValue
      if (read == 0) {
        read = __computeSerializedValue()
        __serializedSizeCachedValue = read
      }
      read
    }
    def writeTo(`_output__`: _root_.com.google.protobuf.CodedOutputStream): Unit = {
      _output__.writeString(1, roleName)
      roles.foreach { __v =>
        _output__.writeTag(2, 2)
        _output__.writeUInt32NoTag(__v.serializedSize)
        __v.writeTo(_output__)
      };
    }
    def mergeFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): messages.botaccess.Role = {
      var __roleName = this.roleName
      val __roles = (_root_.scala.collection.immutable.Vector.newBuilder[messages.botaccess.Access] ++= this.roles)
      var __requiredFields0: _root_.scala.Long = 0x1L
      var _done__ = false
      while (!_done__) {
        val _tag__ = _input__.readTag()
        _tag__ match {
          case 0 => _done__ = true
          case 10 =>
            __roleName = _input__.readString()
            __requiredFields0 &= 0xfffffffffffffffeL
          case 18 =>
            __roles += _root_.scalapb.LiteParser.readMessage(_input__, messages.botaccess.Access.defaultInstance)
          case tag => _input__.skipField(tag)
        }
      }
      if (__requiredFields0 != 0L) { throw new _root_.com.google.protobuf.InvalidProtocolBufferException("Message missing required fields.") } 
      messages.botaccess.Role(
          roleName = __roleName,
          roles = __roles.result()
      )
    }
    def withRoleName(__v: _root_.scala.Predef.String): Role = copy(roleName = __v)
    def clearRoles = copy(roles = _root_.scala.collection.Seq.empty)
    def addRoles(__vs: messages.botaccess.Access*): Role = addAllRoles(__vs)
    def addAllRoles(__vs: TraversableOnce[messages.botaccess.Access]): Role = copy(roles = roles ++ __vs)
    def withRoles(__v: _root_.scala.collection.Seq[messages.botaccess.Access]): Role = copy(roles = __v)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => roleName
        case 2 => roles
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => _root_.scalapb.descriptors.PString(roleName)
        case 2 => _root_.scalapb.descriptors.PRepeated(roles.map(_.toPMessage)(_root_.scala.collection.breakOut))
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion = messages.botaccess.Role
}

object Role extends scalapb.GeneratedMessageCompanion[messages.botaccess.Role] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[messages.botaccess.Role] = this
  def fromFieldsMap(__fieldsMap: scala.collection.immutable.Map[_root_.com.google.protobuf.Descriptors.FieldDescriptor, scala.Any]): messages.botaccess.Role = {
    require(__fieldsMap.keys.forall(_.getContainingType() == javaDescriptor), "FieldDescriptor does not match message type.")
    val __fields = javaDescriptor.getFields
    messages.botaccess.Role(
      __fieldsMap(__fields.get(0)).asInstanceOf[_root_.scala.Predef.String],
      __fieldsMap.getOrElse(__fields.get(1), Nil).asInstanceOf[_root_.scala.collection.Seq[messages.botaccess.Access]]
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[messages.botaccess.Role] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      require(__fieldsMap.keys.forall(_.containingMessage == scalaDescriptor), "FieldDescriptor does not match message type.")
      messages.botaccess.Role(
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).get.as[_root_.scala.Predef.String],
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(2).get).map(_.as[_root_.scala.collection.Seq[messages.botaccess.Access]]).getOrElse(_root_.scala.collection.Seq.empty)
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = BotaccessProto.javaDescriptor.getMessageTypes.get(1)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = BotaccessProto.scalaDescriptor.messages(1)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = {
    var __out: _root_.scalapb.GeneratedMessageCompanion[_] = null
    (__number: @_root_.scala.unchecked) match {
      case 2 => __out = messages.botaccess.Access
    }
    __out
  }
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_]] = Seq.empty
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = throw new MatchError(__fieldNumber)
  lazy val defaultInstance = messages.botaccess.Role(
    roleName = ""
  )
  implicit class RoleLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, messages.botaccess.Role]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, messages.botaccess.Role](_l) {
    def roleName: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.roleName)((c_, f_) => c_.copy(roleName = f_))
    def roles: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.collection.Seq[messages.botaccess.Access]] = field(_.roles)((c_, f_) => c_.copy(roles = f_))
  }
  final val ROLE_NAME_FIELD_NUMBER = 1
  final val ROLES_FIELD_NUMBER = 2
}
