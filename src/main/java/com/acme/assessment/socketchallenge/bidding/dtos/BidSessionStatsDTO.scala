package com.acme.assessment.socketchallenge.bidding.dtos

import scala.beans.BeanProperty

class BidSessionStatsDTO {

  @BeanProperty
  var bidSession: BidSessionDTO = _

  @BeanProperty
  var winningBid: BidDTO = _

  @BeanProperty
  var acceptedBidsCount : Int = _

  @BeanProperty
  var dealerWithBidsCount: Int = _

  @BeanProperty
  var bidsProcessedPerSecond: Float = _
}
