package com.acme.assessment.socketchallenge.bidding.dtos

import scala.beans.BeanProperty

class VehicleDTO {

  @BeanProperty
  var id: Long = _

  @BeanProperty
  var vin: String = _
}
