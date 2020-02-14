package com.acme.assessment.socketchallenge.bidding.services

import com.acme.assessment.socketchallenge.bidding.models.Dealer
import com.acme.assessment.socketchallenge.bidding.repositories.DealerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DealerService @Autowired() (val dealerRepository: DealerRepository) {

  def getDealer(dealerId: Long): Option[Dealer] = Option(dealerRepository.getOne(dealerId))

  def createDealer : Dealer = dealerRepository.save(new Dealer)
}
