package com.acme.assessment.socketchallenge.bidding.models

import com.acme.assessment.socketchallenge.bidding.dtos.{BidDTO, BidSessionDTO, DealerDTO}

import scala.beans.BeanProperty

class BidState {

  @BeanProperty
  var bidSession: BidSessionDTO = _

  @BeanProperty
  var dealers: java.util.List[DealerDTO] = _

  @BeanProperty
  var currentPrice: BigDecimal = _

  @BeanProperty
  var highestBid: BidDTO = _
}
