package com.acme.assessment.socketchallenge.bidding.services

import java.math.BigDecimal
import java.time.temporal.ChronoUnit
import java.util.{Date, UUID}

import com.acme.assessment.socketchallenge.bidding.dtos._
import com.acme.assessment.socketchallenge.bidding.models.{Bid, BidSession, Dealer, Vehicle}
import com.acme.assessment.socketchallenge.bidding.repositories.{BidRepository, BidSessionRepository}
import com.acme.assessment.socketchallenge.bidding.scheduling.NotifyBidSessionEndedJob
import org.quartz._
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

@Service
class BidService @Autowired() (val bidRepository: BidRepository,
                               val bidSessionRepository: BidSessionRepository,
                               val scheduler: Scheduler) {

  private def logger = LoggerFactory.getLogger(classOf[BidService])

  val MAX_BIDS = 20

  def createSession(vehicle: Vehicle, initialPrice: BigDecimal, start: Date, end: Date): Try[BidSession] = {
    val now = new Date()

    bidSessionRepository.findSession(vehicle).asScala.headOption match {
      case Some(ssn) if (ssn.getEnd.before(now)) => Success(ssn)
      case Some(ssn) if (now.before(ssn.getEnd)) => Failure(new IllegalStateException(s"Bidding session for vehicle ${vehicle.getId} is closed."))
      case _ =>
        val newSession = new BidSession
        newSession.setVehicle(vehicle)
        newSession.setInitialPrice(initialPrice)
        newSession.setStart(start)
        newSession.setEnd(end)
        newSession.setCreated(now)
        newSession.setUpdated(now)

        Try(bidSessionRepository.save(newSession)).map(s => {
          scheduleNotificationOnSessionEnd(s)
          s
        })
    }
  }

  private def scheduleNotificationOnSessionEnd(bidSession: BidSession): Unit = {
    Try(
      scheduler.scheduleJob(
        JobBuilder.newJob(classOf[NotifyBidSessionEndedJob])
          .withIdentity(UUID.randomUUID().toString)
          .usingJobData(new JobDataMap(Map("bidSessionId" -> bidSession.getId).asJava))
          .storeDurably(false)
          .build(),
        TriggerBuilder.newTrigger()
          .startAt(bidSession.getEnd)
          .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
          .build()
      )
    ) match {
      case Success(_) => logger.info("Scheduled session-ended notification job that executes on bid session {} end.", bidSession.getId)
      case Failure(e) => logger.error("Failed to schedule job to notify clients of bid session {} end.", bidSession.getId, e)
    }
  }

  def bid(bidSession: BidSession, dealer: Dealer, newPrice: BigDecimal): Try[Bid] = {
    if (bidSession.getEnd.before(new Date()))
      Failure(new IllegalStateException(s"Bid ${bidSession.getId} is closed."))
    else if (bidRepository.getDealerBidCount(bidSession, dealer) >= MAX_BIDS)
      Failure(new IllegalStateException(s"Too many bids by dealer ${dealer.getId}."))
    else {
      val leadingPrice = getCurrentPrice(bidSession)
      if (leadingPrice.compareTo(newPrice) < 0) {
        val b = new Bid()
        b.setSession(bidSession)
        b.setPrice(newPrice)
        b.setDealer(dealer)
        b.setPrice(newPrice)
        b.setCreated(new Date())
        b.setUpdated(b.getCreated)

        Success(bidRepository.save(b))

      } else {
        Failure(new IllegalStateException(s"The bid by ${dealer.getId} of ${newPrice} is lesser than the leading price of ${leadingPrice}."))
      }
    }
  }

  def getStats(bidSessionId: Long): Option[BidSessionStatsDTO] = {
    Option(bidSessionRepository.getOne(bidSessionId)).map(
      s => {
        val stats = new BidSessionStatsDTO

        stats.setBidSession({
          val bidSessionDTO = new BidSessionDTO

          bidSessionDTO.setId(s.getId)

          bidSessionDTO.setVehicle({
            val vehicleDTO = new VehicleDTO

            vehicleDTO.setId(s.getVehicle.getId)
            vehicleDTO.setVin(s.getVehicle.getVin)

            vehicleDTO
          })

          bidSessionDTO.setInitialPrice(s.getInitialPrice)
          bidSessionDTO.setStart(s.getStart)
          bidSessionDTO.setEnd(s.getEnd)

          bidSessionDTO
        })

        stats.setAcceptedBidsCount(bidRepository.getBidCount(s))
        stats.setDealerWithBidsCount(bidRepository.getCountOfDealersWhoBidded(s))

        stats.setWinningBid(getHighestBid(s).map(b => {
          val bidDTO = new BidDTO

          bidDTO.setId(b.getId)
          bidDTO.setPrice(b.getPrice)
          bidDTO.setDealer({
            val dealerDTO = new DealerDTO

            dealerDTO.setId(b.getDealer.getId)
            dealerDTO
          })

          bidDTO

        }).orNull)

        stats.setBidsProcessedPerSecond(
          stats.getAcceptedBidsCount / (1f * ChronoUnit.SECONDS.between(s.getStart.toInstant, s.getEnd.toInstant))
        )

        stats
      }
    )
  }

  def getBidSession(bidSessionId: Long): Option[BidSession] = Option(bidSessionRepository.getOne(bidSessionId))

  def getDealers(bidSession: BidSession): List[Dealer] = bidRepository.getDealers(bidSession).asScala.toList

  def getCurrentPrice(bidSession: BidSession): BigDecimal =
    getHighestBid(bidSession).map(_.getPrice).getOrElse(bidSession.getInitialPrice)

  def getHighestBid(bidSession: BidSession): Option[Bid] =
    bidRepository.getHighestBids(bidSession, PageRequest.of(0, 1)).asScala.headOption
}
