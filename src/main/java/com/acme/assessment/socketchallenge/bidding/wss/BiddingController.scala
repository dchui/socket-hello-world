package com.acme.assessment.socketchallenge.bidding.wss

import java.math.BigDecimal
import java.util.Random

import com.acme.assessment.socketchallenge.bidding.dtos.{BidDTO, BidSessionDTO, DealerDTO, VehicleDTO}
import com.acme.assessment.socketchallenge.bidding.models.BidState
import com.acme.assessment.socketchallenge.bidding.services.{BidService, DealerService}
import com.acme.assessment.socketchallenge.bidding.wss.messages.Response
import javax.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.{DestinationVariable, MessageMapping, SendTo}
import org.springframework.stereotype.Controller

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success}

@Controller
class BiddingController @Autowired() (val bidService: BidService, val dealerService: DealerService) {

  private val BID_PRICE_INCREMENTS = List(500d, 300d, 100d)

  @Transactional
  @MessageMapping(Array("/bid/{bidSessionId}/{dealerId}"))
  @SendTo(Array("/topics/bid/{bidSessionId}"))
  def onBid(
             @DestinationVariable("bidSessionId") bidSessionId: Long,
             @DestinationVariable("dealerId") dealerId: Long
           ): Response = {
    (bidService.getBidSession(bidSessionId), dealerService.getDealer(dealerId)) match {
      case (Some(s), Some(d)) =>
        val currentPrice = bidService.getCurrentPrice(s).add(BigDecimal.valueOf(getRandomBidPriceIncrement))
        bidService.bid(s, d, currentPrice) match {
          case Success(_) => new Response("bid-successful", null, getBidState(bidSessionId))
          case Failure(e) => new Response("bid-failed", e.getMessage, getBidState(bidSessionId))
        }
      case (None, _) => new Response("bid-failed", s"Bid session ${bidSessionId} not found.", getBidState(bidSessionId))
      case (Some(_), None) => new Response("bid-failed", s"Dealer ${dealerId} not found.", getBidState(bidSessionId))
    }
  }

  def getRandomBidPriceIncrement: Double = {
    BID_PRICE_INCREMENTS(new Random(System.currentTimeMillis()).nextInt(BID_PRICE_INCREMENTS.length))
  }


  @Transactional
  @MessageMapping(Array("/bid/{bidSessionId}"))
  @SendTo(Array("/topics/bid/{bidSessionId}"))
  def onSubscribeToBidSession(@DestinationVariable("bidSessionId") bidSessionId: Long): Response =
    new Response("state-update", null, getBidState(bidSessionId))

  private def getBidState(bidSessionId: Long): BidState = {
    bidService.getBidSession(bidSessionId).map(s => {

      val state = new BidState

      state.setBidSession({
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

      state.setCurrentPrice(bidService.getCurrentPrice(s))

      state.setHighestBid(
        bidService.getHighestBid(s).map(b => {
          val bidDTO = new BidDTO

          bidDTO.setId(b.getId)
          bidDTO.setPrice(b.getPrice)
          bidDTO.setDealer({
            val dealerDTO = new DealerDTO

            dealerDTO.setId(b.getDealer.getId)
            dealerDTO
          })

          bidDTO

        }).orNull
      )

      state.setDealers(bidService.getDealers(s).map(
        d => {
          val dealerDTO = new DealerDTO

          dealerDTO.setId(d.getId)
          dealerDTO

        }
      ).asJava)

      state

    }).orNull
  }
}


