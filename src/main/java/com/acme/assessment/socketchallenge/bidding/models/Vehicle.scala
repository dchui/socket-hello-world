package com.acme.assessment.socketchallenge.bidding.models

import java.util.Date

import javax.persistence.{Column, Entity, GeneratedValue, Id}

import scala.beans.BeanProperty

@Entity
class Vehicle {

  @Id
  @GeneratedValue
  @BeanProperty
  var id: Long = _

  @Column(nullable = false)
  @BeanProperty
  var vin: String = _

  @Column(nullable = false)
  @BeanProperty
  var created: Date = _

  @Column(nullable = false)
  @BeanProperty
  var updated: Date = _
}
