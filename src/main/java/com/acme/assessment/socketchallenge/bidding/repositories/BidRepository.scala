package com.acme.assessment.socketchallenge.bidding.repositories

import com.acme.assessment.socketchallenge.bidding.models.{Bid, BidSession, Dealer}
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.{JpaRepository, Query}
import org.springframework.data.repository.query.Param

trait BidRepository extends JpaRepository[Bid, Long] {

  @Query("SELECT b FROM Bid b WHERE b.session = :ssn ORDER BY b.price DESC")
  def getHighestBids(@Param("ssn") bidSession: BidSession, pageable: Pageable): java.util.List[Bid]

  @Query("SELECT COUNT(b) FROM Bid b WHERE b.session = :ssn AND b.dealer = :dealer")
  def getDealerBidCount(@Param("ssn") bidSession: BidSession, @Param("dealer") dealer: Dealer): Int

  @Query("SELECT COUNT(b) FROM Bid b WHERE b.session = :ssn")
  def getBidCount(@Param("ssn") bidSession: BidSession): Int

  @Query("SELECT COUNT(DISTINCT b.dealer) FROM Bid b WHERE b.session = :ssn")
  def getCountOfDealersWhoBidded(@Param("ssn") bidSession: BidSession): Int

  @Query("SELECT DISTINCT b.dealer FROM Bid b WHERE b.session = :ssn")
  def getDealers(@Param("ssn") bidSession: BidSession): java.util.List[Dealer]
}
