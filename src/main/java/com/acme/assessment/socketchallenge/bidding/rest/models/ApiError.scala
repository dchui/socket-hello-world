package com.acme.assessment.socketchallenge.bidding.rest.models

import scala.beans.BeanProperty

class ApiError(var e: String, var m: String) {

  @BeanProperty
  var error: String = e

  @BeanProperty
  var message: String = m

  def this(e: Throwable) {
    this(e.getClass.getName, e.getMessage)
  }
}
