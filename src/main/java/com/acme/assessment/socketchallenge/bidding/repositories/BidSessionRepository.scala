package com.acme.assessment.socketchallenge.bidding.repositories

import com.acme.assessment.socketchallenge.bidding.models.{BidSession, Vehicle}
import org.springframework.data.jpa.repository.{JpaRepository, Query}

trait BidSessionRepository extends JpaRepository[BidSession, Long] {

  @Query("SELECT bs FROM BidSession bs WHERE bs.vehicle = :vehicle ORDER BY bs.id")
  def findSession(vehicle: Vehicle): java.util.List[BidSession]
}
