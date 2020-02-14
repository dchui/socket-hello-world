package com.acme.assessment.socketchallenge.bidding.dtos

import scala.beans.BeanProperty

class BidDTO {

  @BeanProperty
  var id: Long = _

  @BeanProperty
  var dealer: DealerDTO = _

  @BeanProperty
  var price: BigDecimal = _
}
