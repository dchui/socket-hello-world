package com.acme.assessment.socketchallenge.bidding.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.{EnableWebSocketMessageBroker, StompEndpointRegistry, WebSocketMessageBrokerConfigurer}

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig extends WebSocketMessageBrokerConfigurer {

  override def registerStompEndpoints(registry: StompEndpointRegistry): Unit = {
    registry.addEndpoint("/bidding").withSockJS
  }

  override def configureMessageBroker(registry: MessageBrokerRegistry): Unit = {
    registry.enableSimpleBroker("/topics")
    registry.setApplicationDestinationPrefixes("/bidding")
  }
}
