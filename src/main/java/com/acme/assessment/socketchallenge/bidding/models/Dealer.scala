package com.acme.assessment.socketchallenge.bidding.models

import javax.persistence.{Entity, GeneratedValue, Id}

import scala.beans.BeanProperty

@Entity
class Dealer {

  @Id
  @GeneratedValue
  @BeanProperty
  var id: Long = _
}
