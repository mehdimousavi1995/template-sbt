package homeee

import akka.persistence.SnapshotMetadata
import cqrs.{Event, ProcessorState}
import messages.homeee.homessages.HomeEvents.{DeviceAdded, DeviceRemoved, HomeCreated}
import messages.homeee.homessages.{AllDevices, HomeSnapShot, Owner}

private[homeee] object HomeState {

  def initial(homeId: String) = HomeState(
    homeId,
    Owner("", "", "", 0),
    Seq.empty[AllDevices],
    "",
    0
  )
}

private[homeee] final case class HomeState(
                                            homeId: String,
                                            owner: Owner,
                                            devices: Seq[AllDevices],
                                            address: String,
                                            houseArea: Int

                                          ) extends ProcessorState[HomeState] {

  override def updated(e: Event): HomeState = {
    e match {
      case e: HomeCreated ⇒
        this.copy(e.homeId, e.owner, Seq.empty[AllDevices], e.address, e.houseArea)

      case e: DeviceAdded =>
        this.copy(devices = this.devices :+ e.device)

      case e: DeviceRemoved =>
        this.copy(devices = this.devices.filterNot {
          case AllDevices(d) if d.isDefined && d.isHeatingCooler =>
            d.heatingCooler.get.deviceId == e.deviceId
          case AllDevices(d) if d.isDefined && d.isLampDevice =>
            d.lampDevice.get.deviceId == e.deviceId
          case _ => false
        })

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

