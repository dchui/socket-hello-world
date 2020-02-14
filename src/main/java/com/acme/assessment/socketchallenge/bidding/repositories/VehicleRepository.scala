package com.acme.assessment.socketchallenge.bidding.repositories

import com.acme.assessment.socketchallenge.bidding.models.Vehicle
import org.springframework.data.jpa.repository.{JpaRepository, Query}

trait VehicleRepository extends JpaRepository[Vehicle, Long]  {

  @Query("SELECT v FROM Vehicle v WHERE v.vin = :vin ORDER BY v.id")
  def findByVin(vin: String): java.util.List[Vehicle]
}
