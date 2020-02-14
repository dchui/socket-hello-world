package com.acme.assessment.socketchallenge.bidding.wss.messages

import com.fasterxml.jackson.annotation.JsonInclude

import scala.beans.BeanProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class Response(var _type: String, var _message: String, var _payload: Any) {

  @BeanProperty
  var `type`: String = _type

  @BeanProperty
  var message: String = _message

  @BeanProperty
  var payload: Any = _payload
}
