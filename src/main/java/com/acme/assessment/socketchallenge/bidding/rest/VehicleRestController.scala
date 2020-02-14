package com.acme.assessment.socketchallenge.bidding.rest

import com.acme.assessment.socketchallenge.bidding.models.Vehicle
import com.acme.assessment.socketchallenge.bidding.rest.models.ApiError
import com.acme.assessment.socketchallenge.bidding.services.VehicleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.{PostMapping, RequestBody, RestController}

import scala.util.{Failure, Success}

@RestController
class VehicleRestController @Autowired() (val vehicleService: VehicleService) {

  @PostMapping(Array("/api/rest/v1/vehicles"))
  @throws[IllegalStateException]
  def create(@RequestBody vehicle: Vehicle): ResponseEntity[Any] = {
    vehicleService.createVehicle(vehicle.getVin) match {
      case Success(v) => ResponseEntity.ok(v)
      case Failure(e) => ResponseEntity.badRequest().body(new ApiError(e))
    }
  }
}
