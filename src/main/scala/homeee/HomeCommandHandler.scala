package homeee

import messages.homeee.homessages.HomeCommands.CreateHome


private trait HomeCommandHandler {
  this: HomeProcessor ⇒


  def createHome(ch: CreateHome) = {

  }

}