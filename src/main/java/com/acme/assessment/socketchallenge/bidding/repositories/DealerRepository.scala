package com.acme.assessment.socketchallenge.bidding.repositories

import com.acme.assessment.socketchallenge.bidding.models.Dealer
import org.springframework.data.jpa.repository.JpaRepository

trait DealerRepository extends JpaRepository[Dealer, Long] {}
