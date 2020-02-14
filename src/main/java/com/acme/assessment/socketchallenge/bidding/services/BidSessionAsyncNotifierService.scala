package com.acme.assessment.socketchallenge.bidding.services

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class BidSessionAsyncNotifierService @Autowired() (val simpleMessageTemplate: SimpMessagingTemplate) {

  private def logger = LoggerFactory.getLogger(classOf[BidSessionAsyncNotifierService])

  def notifyBidSession(bidSessionId: Long, payload: Any): Unit = {
    logger.info(s"Notifying clients that bid session {} has just ended.", bidSessionId)
    simpleMessageTemplate.convertAndSend(s"/topics/bid/${bidSessionId}", payload)
  }
}
