package com.acme.assessment.socketchallenge.bidding.scheduling

import com.acme.assessment.socketchallenge.bidding.services.{BidService, BidSessionAsyncNotifierService}
import com.acme.assessment.socketchallenge.bidding.wss.messages.Response
import javax.transaction.Transactional
import org.quartz.{JobExecutionContext, JobExecutionException}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component

@Component
class NotifyBidSessionEndedJob @Autowired() (
                                              val bidSessionAsyncNotifierService: BidSessionAsyncNotifierService,
                                              val bidService: BidService
                                            ) extends QuartzJobBean {

  private def logger = LoggerFactory.getLogger(classOf[NotifyBidSessionEndedJob])

  @Transactional
  @throws(classOf[JobExecutionException])
  override def executeInternal(jobExecutionContext: JobExecutionContext): Unit = {
    val bidSessionId = jobExecutionContext.getMergedJobDataMap.getLong("bidSessionId")

    logger.info("Bid session {} ended. Session end notifier job executing.", bidSessionId)

    bidSessionAsyncNotifierService.notifyBidSession(
      bidSessionId,
      new Response("session-ended", null, bidService.getStats(bidSessionId).orNull)
    )
  }
}
