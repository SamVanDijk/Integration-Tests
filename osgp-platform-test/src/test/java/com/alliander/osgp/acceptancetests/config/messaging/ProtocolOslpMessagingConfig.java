/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.config.messaging;

import static org.mockito.Mockito.mock;

import javax.jms.MessageListener;

import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.alliander.osgp.adapter.protocol.oslp.application.config.QualifierProtocolOslp;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceRequestMessageListener;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceResponseMessageSender;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.OsgpRequestMessageSender;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.OsgpResponseMessageListener;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.OslpLogItemRequestMessageSender;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.SigningServerRequestMessageSender;
import com.alliander.osgp.signing.server.infra.messaging.SigningServerResponseMessageSender;

//@Configuration
public class ProtocolOslpMessagingConfig {

    // === JMS SETTINGS OSLP REQUESTS ===

    @Bean
    @QualifierProtocolOslp
    public ActiveMQDestination oslpRequestsQueue() {
        return new ActiveMQQueue(MessagingConfig.PROTOCOL_OSLP_1_0__OSGP_CORE_1_0__REQUESTS_QUEUE);
    }

    @Autowired
    @QualifierProtocolOslp
    private DeviceRequestMessageListener deviceRequestMessageListener;

    @Bean
    @QualifierProtocolOslp
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DefaultMessageListenerContainer oslpRequestsMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.oslpRequestsQueue());
        messageListenerContainer.setConcurrentConsumers(MessagingConfig.CONCURRENT_CONSUMERS);
        messageListenerContainer.setMaxConcurrentConsumers(MessagingConfig.MAX_CONCURRENT_CONSUMERS);
        messageListenerContainer.setMessageListener(this.deviceRequestMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    // === JMS SETTINGS: OSLP RESPONSES ===

    @Bean
    @QualifierProtocolOslp
    public JmsTemplate oslpResponsesJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.oslpResponsesQueue());
        jmsTemplate.setExplicitQosEnabled(MessagingConfig.EXPLICIT_QOS_ENABLED);
        jmsTemplate.setTimeToLive(MessagingConfig.TIME_TO_LIVE);
        jmsTemplate.setDeliveryPersistent(MessagingConfig.DELIVERY_PERSISTENT);
        jmsTemplate.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(MessagingConfig.RECEIVE_TIMEOUT);
        return jmsTemplate;
    }

    @Bean
    @QualifierProtocolOslp
    public ActiveMQDestination oslpResponsesQueue() {
        return new ActiveMQQueue(MessagingConfig.OSGP_CORE_1_0__PROTOCOL_OSLP_1_0__RESPONSES_QUEUE);
    }

    @Bean
    @QualifierProtocolOslp
    public DeviceResponseMessageSender oslpResponseMessageSender() {
        return new DeviceResponseMessageSender();
    }

    // === JMS SETTINGS: OSLP LOG ITEM REQUESTS ===

    @Bean
    @QualifierProtocolOslp
    public JmsTemplate oslpLogItemRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.oslpLogItemRequestsQueue());
        jmsTemplate.setExplicitQosEnabled(MessagingConfig.EXPLICIT_QOS_ENABLED);
        jmsTemplate.setTimeToLive(MessagingConfig.TIME_TO_LIVE);
        jmsTemplate.setDeliveryPersistent(MessagingConfig.DELIVERY_PERSISTENT);
        jmsTemplate.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(MessagingConfig.RECEIVE_TIMEOUT);
        return jmsTemplate;
    }

    @Bean
    @QualifierProtocolOslp
    public ActiveMQDestination oslpLogItemRequestsQueue() {
        return new ActiveMQQueue(MessagingConfig.OSLP_LOG_ITEM_REQUESTS_QUEUE);
    }

    @Bean
    @QualifierProtocolOslp
    public OslpLogItemRequestMessageSender oslpLogItemRequestMessageSender() {
        return mock(OslpLogItemRequestMessageSender.class);
    }

    // === OSGP REQUESTS ===

    @Bean
    @QualifierProtocolOslp
    public JmsTemplate osgpRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.osgpRequestsQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(MessagingConfig.EXPLICIT_QOS_ENABLED);
        jmsTemplate.setTimeToLive(MessagingConfig.TIME_TO_LIVE);
        jmsTemplate.setDeliveryPersistent(MessagingConfig.DELIVERY_PERSISTENT);
        jmsTemplate.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(MessagingConfig.RECEIVE_TIMEOUT);
        return jmsTemplate;
    }

    @Bean
    @QualifierProtocolOslp
    public ActiveMQDestination osgpRequestsQueue() {
        return new ActiveMQQueue(MessagingConfig.OSGP_CORE_1_0__PROTOCOL_OSLP_1_0__REQUESTS_QUEUE);
    }

    @Bean
    @QualifierProtocolOslp
    public OsgpRequestMessageSender osgpRequestMessageSender() {
        return new OsgpRequestMessageSender();
    }

    // === OSGP RESPONSES ===

    @Bean
    @QualifierProtocolOslp
    public ActiveMQDestination osgpResponsesQueue() {
        return new ActiveMQQueue(MessagingConfig.PROTOCOL_OSLP_1_0__OSGP_CORE_1_0__RESPONSES_QUEUE);
    }

    @Bean
    @QualifierProtocolOslp
    public DefaultMessageListenerContainer osgpResponsesMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.osgpResponsesQueue());
        messageListenerContainer.setConcurrentConsumers(MessagingConfig.CONCURRENT_CONSUMERS);
        messageListenerContainer.setMaxConcurrentConsumers(MessagingConfig.MAX_CONCURRENT_CONSUMERS);
        messageListenerContainer.setMessageListener(this.osgpResponseMessageListener());
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    @Bean
    @QualifierProtocolOslp
    public OsgpResponseMessageListener osgpResponseMessageListener() {
        return new OsgpResponseMessageListener();
    }

    // === SIGNING SERVER ===

    @Bean
    @QualifierProtocolOslp
    public JmsTemplate signingServerRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.signingServerRequestsQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(MessagingConfig.EXPLICIT_QOS_ENABLED);
        jmsTemplate.setTimeToLive(MessagingConfig.TIME_TO_LIVE);
        jmsTemplate.setDeliveryPersistent(MessagingConfig.DELIVERY_PERSISTENT);
        jmsTemplate.setConnectionFactory(MessagingConfig.connectionFactory());
        jmsTemplate.setReceiveTimeout(MessagingConfig.RECEIVE_TIMEOUT);
        return jmsTemplate;
    }

    @Bean
    @QualifierProtocolOslp
    public ActiveMQDestination signingServerRequestsQueue() {
        return new ActiveMQQueue(MessagingConfig.SIGNING_SERVER_1_0_REQUESTS);
    }

    @Bean
    @QualifierProtocolOslp
    public RedeliveryPolicy signingServerRequestsRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(MessagingConfig.INITIAL_REDELIVERY_DELAY);
        redeliveryPolicy.setMaximumRedeliveries(MessagingConfig.MAXIMUM_REDELIVERIES);
        redeliveryPolicy.setMaximumRedeliveryDelay(MessagingConfig.MAXIMUM_REDELIVERY_DELAY);
        redeliveryPolicy.setRedeliveryDelay(MessagingConfig.REDELIVERY_DELAY);
        redeliveryPolicy.setDestination(this.signingServerRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(MessagingConfig.BACK_OFF_MULTIPLIER);
        redeliveryPolicy.setUseExponentialBackOff(MessagingConfig.USE_EXPONENTIAL_BACK_OFF);
        return redeliveryPolicy;
    }

    @Bean
    @QualifierProtocolOslp
    public SigningServerRequestMessageSender signingServerRequestMessageSender() {
        return new SigningServerRequestMessageSender();
    }

    @Autowired
    @Qualifier("protocolOslpSigningServerResponsesMessageListener")
    @QualifierProtocolOslp
    private MessageListener signingServerResponsesMessageListener;

    /**
     * Instead of a fixed name for the responses queue, the signing-server uses a 'reply-to' responses queue. This
     * 'reply-to' responses queue is communicated to the signing-server by this Protocol-Adapter-OSLP instance when a
     * request message is sent to the signing-server. The signing-server will send signed response messages to the
     * 'reply-to' queue. This ensures that the signed response messages for this Protocol-Adapter-OSLP instance are sent
     * back to this instance. FOR THE TESTS THIS IS A FIXED QUEUE NAME.
     */
    @Bean
    @QualifierProtocolOslp
    public ActiveMQDestination replyToQueue() {
        return new ActiveMQQueue(MessagingConfig.SIGNING_SERVER_1_0_RESPONSES);
    }

    @Bean
    @QualifierProtocolOslp
    public RedeliveryPolicy signingServerResponsesRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(MessagingConfig.INITIAL_REDELIVERY_DELAY);
        redeliveryPolicy.setMaximumRedeliveries(MessagingConfig.MAXIMUM_REDELIVERIES);
        redeliveryPolicy.setMaximumRedeliveryDelay(MessagingConfig.MAXIMUM_REDELIVERY_DELAY);
        redeliveryPolicy.setRedeliveryDelay(MessagingConfig.REDELIVERY_DELAY);
        redeliveryPolicy.setDestination(this.replyToQueue());
        redeliveryPolicy.setBackOffMultiplier(MessagingConfig.BACK_OFF_MULTIPLIER);
        redeliveryPolicy.setUseExponentialBackOff(MessagingConfig.USE_EXPONENTIAL_BACK_OFF);
        return redeliveryPolicy;
    }

    @Bean
    @QualifierProtocolOslp
    public DefaultMessageListenerContainer signingResponsesMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.replyToQueue());
        messageListenerContainer.setConcurrentConsumers(MessagingConfig.CONCURRENT_CONSUMERS);
        messageListenerContainer.setMaxConcurrentConsumers(MessagingConfig.MAX_CONCURRENT_CONSUMERS);
        messageListenerContainer.setMessageListener(this.signingServerResponsesMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    // === JMS SETTINGS: SIGNING SERVER REQUESTS ===

    @Bean
    @QualifierProtocolOslp
    public ActiveMQDestination requestsQueue() {
        return new ActiveMQQueue(MessagingConfig.SIGNING_SERVER_1_0_REQUESTS);
    }

    @Bean
    @QualifierProtocolOslp
    public RedeliveryPolicy oslpRequestsRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(MessagingConfig.INITIAL_REDELIVERY_DELAY);
        redeliveryPolicy.setMaximumRedeliveries(MessagingConfig.MAXIMUM_REDELIVERIES);
        redeliveryPolicy.setMaximumRedeliveryDelay(MessagingConfig.MAXIMUM_REDELIVERY_DELAY);
        redeliveryPolicy.setRedeliveryDelay(MessagingConfig.REDELIVERY_DELAY);
        redeliveryPolicy.setDestination(this.requestsQueue());
        redeliveryPolicy.setBackOffMultiplier(MessagingConfig.BACK_OFF_MULTIPLIER);
        redeliveryPolicy.setUseExponentialBackOff(MessagingConfig.USE_EXPONENTIAL_BACK_OFF);
        return redeliveryPolicy;
    }

    @Bean
    @QualifierProtocolOslp
    public DefaultMessageListenerContainer requestsMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.requestsQueue());
        messageListenerContainer.setConcurrentConsumers(MessagingConfig.CONCURRENT_CONSUMERS);
        messageListenerContainer.setMaxConcurrentConsumers(MessagingConfig.MAX_CONCURRENT_CONSUMERS);
        messageListenerContainer.setMessageListener(this.requestsMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    @Autowired
    @Qualifier("protocolOslpRequestsMessageListener")
    @QualifierProtocolOslp
    private MessageListener requestsMessageListener;

    // === JMS SETTINGS: SIGNING SERVER RESPONSES ===

    @Bean
    @QualifierProtocolOslp
    public JmsTemplate responsesJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.responsesQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(MessagingConfig.EXPLICIT_QOS_ENABLED);
        jmsTemplate.setTimeToLive(MessagingConfig.TIME_TO_LIVE);
        jmsTemplate.setDeliveryPersistent(MessagingConfig.DELIVERY_PERSISTENT);
        jmsTemplate.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(MessagingConfig.RECEIVE_TIMEOUT);
        return jmsTemplate;
    }

    @Bean
    @QualifierProtocolOslp
    public ActiveMQDestination responsesQueue() {
        return new ActiveMQQueue(MessagingConfig.SIGNING_SERVER_1_0_RESPONSES);
    }

    @Bean
    @QualifierProtocolOslp
    public RedeliveryPolicy responsesRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(MessagingConfig.INITIAL_REDELIVERY_DELAY);
        redeliveryPolicy.setMaximumRedeliveries(MessagingConfig.MAXIMUM_REDELIVERIES);
        redeliveryPolicy.setMaximumRedeliveryDelay(MessagingConfig.MAXIMUM_REDELIVERY_DELAY);
        redeliveryPolicy.setRedeliveryDelay(MessagingConfig.REDELIVERY_DELAY);
        redeliveryPolicy.setDestination(this.responsesQueue());
        redeliveryPolicy.setBackOffMultiplier(MessagingConfig.BACK_OFF_MULTIPLIER);
        redeliveryPolicy.setUseExponentialBackOff(MessagingConfig.USE_EXPONENTIAL_BACK_OFF);
        return redeliveryPolicy;
    }

    @Bean
    @QualifierProtocolOslp
    public SigningServerResponseMessageSender responseMessageSender() {
        return new SigningServerResponseMessageSender();
    }
}
