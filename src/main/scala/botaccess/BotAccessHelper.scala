package botaccess

import com.typesafe.config.Config
import messages.botaccess._
import scala.collection.JavaConverters._

trait BotAccessHelper {

  protected def search(roleName: String, definedRoles: Set[Role]): Option[Seq[Access]] = {
    val result = definedRoles.filter(p ⇒ p.roleName == roleName)
    if (result.size == 1)
      Some(result.head.roles)
    else
      None
  }

  protected def rolesExists(roles: Seq[String], definedRoles: Set[Role]): Boolean =
    roles.map(roleExists(_, definedRoles)).foldLeft(true)(_ && _)

  protected def getUndefinedRoles(roles: Seq[String], definedRoles: Set[Role]): Seq[String] =
    roles.diff(definedRoles.map(_.roleName).toSeq)

  protected def roleExists(role: String, definedRoles: Set[Role]): Boolean = definedRoles.exists(p ⇒ p.roleName == role)

  protected def readRolesFromConfig(config: Config, isOrdinaryRole: Boolean, baseConf: String): Seq[Role] =
    config.getConfigList(baseConf + (if (isOrdinaryRole) "roles" else "premium-accesses")).asScala.map { role ⇒
      val roleName = role.getString("name")
      val accesses = role.getConfigList(roleName).asScala.flatMap { access ⇒
        val service = access.getString("service")
        val apis = access.getStringList("apis").asScala
        apis.map { api ⇒ Access(service, api) }
      }
      Role(roleName, accesses)
    }

}
