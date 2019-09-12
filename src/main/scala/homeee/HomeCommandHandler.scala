package homeee

import java.time.Instant

import messages.homeee.homessages.HomeCommands.{AddDevice, CreateHome, DeviceStatus, RemoveDevice}
import messages.homeee.homessages.HomeEvents.{DeviceAdded, DeviceRemoved, DeviceStatusChanged, HomeCreated}
import messages.homeee.homessages.ResponseVoid


private trait HomeCommandHandler {
  this: HomeProcessor ⇒


  def createHome(ch: CreateHome): Unit = {
    val replyTo = sender()
    persist(HomeCreated(ch.homeId, Instant.now, ch.owner, ch.address, ch.houseArea)) { evt =>
      commit(evt)
      log.info("Home created, home: {}", ch)
      replyTo ! ResponseVoid
    }

  }

  def addDevice(ad: AddDevice): Unit = {
    val replyTo = sender()
    persist(DeviceAdded(ad.device, Instant.now)) {evt =>
      commit(evt)
      log.info("Device Added to home: {}, device: {}", homeUserId, ad.device)
      replyTo ! ResponseVoid
    }

  }

  def removeDevice(rd: RemoveDevice): Unit = {
    val replyTo = sender()
    persist(DeviceRemoved(rd.deviceId, Instant.now())) { evt =>
      commit(evt)
      log.info("Device removed from home: {}, device: {}", homeUserId, rd.deviceId)
      replyTo ! ResponseVoid
    }
  }


  def deviceStatus(ds: DeviceStatus): Unit = {
    val replyTo = sender()
    persist(DeviceStatusChanged(ds.deviceId, Instant.now(), ds.status, ds.optTemp)) { evt =>
      commit(evt)
      log.info("Device Status changed from home: {}, device: {}", homeUserId, ds.deviceId)
      replyTo ! ResponseVoid
    }
  }

}