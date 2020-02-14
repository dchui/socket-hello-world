package com.acme.assessment.socketchallenge.bidding.services

import java.util.Date

import com.acme.assessment.socketchallenge.bidding.models.Vehicle
import com.acme.assessment.socketchallenge.bidding.repositories.VehicleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Try}

@Service
class VehicleService @Autowired() (val vehicleRepository: VehicleRepository)  {

  def createVehicle(vin: String): Try[Vehicle] = {
    vehicleRepository.findByVin(vin).asScala.headOption
      .map(_ => Failure(new IllegalStateException(s"Vehicle with VIN ${vin} already exists.")))
      .getOrElse({
        val newVehicle = new Vehicle
        val now = new Date()

        newVehicle.setVin(vin)
        newVehicle.setCreated(now)
        newVehicle.setUpdated(now)

        Try(vehicleRepository.save(newVehicle))
      })
  }

  def getAll(): java.util.List[Vehicle] = vehicleRepository.findAll()
}
