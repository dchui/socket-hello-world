package com.acme.assessment.socketchallenge.bidding.dtos

import java.math.BigDecimal
import java.util.Date

import scala.beans.BeanProperty

class BidSessionDTO {

  @BeanProperty
  var id: Long = _

  @BeanProperty
  var vehicle: VehicleDTO = _

  @BeanProperty
  var initialPrice: BigDecimal = _

  @BeanProperty
  var start: Date = _

  @BeanProperty
  var end: Date = _
}
