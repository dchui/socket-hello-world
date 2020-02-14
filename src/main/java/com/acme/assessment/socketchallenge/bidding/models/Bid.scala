package com.acme.assessment.socketchallenge.bidding.models

import java.math.BigDecimal
import java.util.Date

import javax.persistence._

import scala.beans.BeanProperty

@Entity
class Bid {

  @Id
  @GeneratedValue
  @BeanProperty
  var id: Long = _

  @ManyToOne
  @BeanProperty
  var session: BidSession = _

  @ManyToOne
  @BeanProperty
  var dealer: Dealer = _

  @Column(nullable = false)
  @BeanProperty
  var price: BigDecimal = _

  @Column(nullable = false)
  @BeanProperty
  var created: Date = _

  @Column(nullable = false)
  @BeanProperty
  var updated: Date = _
}
