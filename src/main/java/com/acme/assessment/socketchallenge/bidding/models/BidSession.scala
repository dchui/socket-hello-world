package com.acme.assessment.socketchallenge.bidding.models

import java.math.BigDecimal
import java.util.Date

import javax.persistence._

import scala.beans.BeanProperty

@Entity
class BidSession {

  @Id
  @GeneratedValue
  @BeanProperty
  var id: Long = _

  @ManyToOne
  @BeanProperty
  var vehicle: Vehicle = _

  @Column(nullable = false)
  @BeanProperty
  var initialPrice: BigDecimal = _

  @Column(nullable = false)
  @BeanProperty
  var start: Date = _

  @Column(nullable = false)
  @BeanProperty
  var end: Date = _

  @Column(nullable = false)
  @BeanProperty
  var created: Date = _

  @Column(nullable = false)
  @BeanProperty
  var updated: Date = _
}
