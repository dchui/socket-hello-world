package com.acme.assessment.socketchallenge;

import com.acme.assessment.socketchallenge.bidding.models.BidSession;
import com.acme.assessment.socketchallenge.bidding.services.BidService;
import com.acme.assessment.socketchallenge.bidding.services.DealerService;
import com.acme.assessment.socketchallenge.bidding.services.VehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import scala.util.Try;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@SpringBootApplication
public class SocketchallengeApplication implements ApplicationRunner {

	private static final Logger logger = LoggerFactory.getLogger(SocketchallengeApplication.class);

	private final int defaultSessionLengthSeconds;

	private final DealerService dealerService;

	private final VehicleService vehicleService;

	private final BidService bidService;

	@Autowired
	public SocketchallengeApplication(
			@Value("${socketchallange.bid.session.default-length}") int defaultSessionLengthSeconds,
			DealerService dealerService,
			VehicleService vehicleService,
			BidService bidService) {
		this.defaultSessionLengthSeconds = defaultSessionLengthSeconds;
		this.dealerService = dealerService;
		this.vehicleService = vehicleService;
		this.bidService = bidService;
	}

	public static void main(String[] args) {
		SpringApplication.run(SocketchallengeApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) {
		seedData();
	}

	private void seedData() {
		seedDealersData();
		seedVehiclesData();
		seedBidSessionData();
	}

	private void seedDealersData() {
		for (int i = 0; i < 100; i++) {
			logger.info("Created dealer {}.", dealerService.createDealer().getId());
		}
	}

	private void seedVehiclesData() {
		logger.info("Created vehicle with VIN {}.", vehicleService.createVehicle(UUID.randomUUID().toString()).get().getVin());
	}

	private void seedBidSessionData() {
		vehicleService.getAll().forEach(v -> {
			Try<BidSession> bidSessionCreation = bidService.createSession(
					v,
					BigDecimal.valueOf(1000d),
					new Date(),
					Date.from(new Date().toInstant().plus(defaultSessionLengthSeconds, ChronoUnit.SECONDS))
			);

			if (bidSessionCreation.isSuccess()) {
				logger.info("Created bid session {} for vehicle {}.", bidSessionCreation.get().getId(), v.getVin());
			}
		});
	}
}
