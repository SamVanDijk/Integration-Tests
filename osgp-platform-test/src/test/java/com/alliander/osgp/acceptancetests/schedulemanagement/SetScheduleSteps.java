/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.schedulemanagement;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.commons.codec.binary.Base64;
import org.givwenzen.annotations.DomainStep;
import org.givwenzen.annotations.DomainSteps;
import org.jboss.netty.channel.Channel;
import org.junit.Assert;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;

import com.alliander.osgp.acceptancetests.DateUtils;
import com.alliander.osgp.acceptancetests.OslpTestUtils;
import com.alliander.osgp.acceptancetests.ProtocolInfoTestUtils;
import com.alliander.osgp.adapter.domain.publiclighting.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.adapter.protocol.oslp.application.services.DeviceRegistrationService;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDeviceBuilder;
import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpChannelHandlerClient;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpDeviceService;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.SequenceNumberUtils;
import com.alliander.osgp.adapter.ws.publiclighting.application.mapping.ScheduleManagementMapper;
import com.alliander.osgp.adapter.ws.publiclighting.application.services.ScheduleManagementService;
import com.alliander.osgp.adapter.ws.publiclighting.endpoints.PublicLightingScheduleManagementEndpoint;
import com.alliander.osgp.adapter.ws.publiclighting.infra.jms.PublicLightingResponseMessageFinder;
import com.alliander.osgp.adapter.ws.schema.publiclighting.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.ActionTimeType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.LightValue;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.Schedule;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.TriggerType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.WeekDayType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.WindowType;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceAuthorizationBuilder;
import com.alliander.osgp.domain.core.entities.DeviceBuilder;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceFunctionMappingRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.Oslp.Status;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Configurable
@DomainSteps
public class SetScheduleSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetScheduleSteps.class);

    private static final String DATE_FORMAT = "yyyyMMdd";
    private static final String DEVICE_UID = "AAAAAAAAAAYAAAAA";
    private static final String ORGANISATION_ID = "ORGANISATION-01";
    private static final String ORGANISATION_PREFIX = "ORG";

    private static final Boolean PUBLIC_KEY_PRESENT = true;
    private static final String PROTOCOL = "OSLP";
    private static final String PROTOCOL_VERSION = "1.0";

    private static final int NEXT_SEQUENCE_NUMBER = 2;

    // WS Adapter fields
    private PublicLightingScheduleManagementEndpoint scheduleManagementEndpoint;

    private SetScheduleRequest request;
    private SetScheduleAsyncResponse setScheduleAsyncResponse;
    private SetScheduleAsyncRequest setScheduleAsyncRequest;
    private SetScheduleResponse response;

    @Autowired
    @Qualifier("wsPublicLightingScheduleManagementService")
    private ScheduleManagementService scheduleManagementService;

    @Autowired
    @Qualifier("wsPublicLightingIncomingResponsesMessageFinder")
    private PublicLightingResponseMessageFinder publicLightingResponseMessageFinder;
    @Autowired
    @Qualifier("wsPublicLightingIncomingResponsesJmsTemplate")
    private JmsTemplate publicLightingResponsesJmsTemplate;

    // Domain Adapter fields
    @Autowired
    @Qualifier("domainPublicLightingOutgoingWebServiceResponseMessageSender")
    private WebServiceResponseMessageSender webServiceResponseMessageSenderMock;

    private Ssld device;
    private Organisation organisation;

    @Autowired
    private DeviceRepository deviceRepositoryMock;
    @Autowired
    private SsldRepository ssldRepositoryMock;
    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepositoryMock;
    @Autowired
    private DeviceFunctionMappingRepository deviceFunctionMappingRepositoryMock;
    @Autowired
    private OrganisationRepository organisationRepositoryMock;
    @Autowired
    private DeviceLogItemRepository logItemRepositoryMock;

    // Protocol Adapter fields
    @Autowired
    private DeviceRegistrationService deviceRegistrationService;
    @Autowired
    private OslpDeviceService oslpDeviceService;
    private OslpDevice oslpDevice;
    @Autowired
    private OslpDeviceRepository oslpDeviceRepositoryMock;

    private OslpEnvelope oslpEnvelope;
    private OslpEnvelope oslpMessage;
    private OslpChannelHandlerClient oslpChannelHandler;
    @Autowired
    private Channel channelMock;

    // Test fields
    private Throwable throwable;

    // === GIVEN ===

    @DomainStep("a set schedule request for device (.*) with (.*), (.*), (.*), (.*), (.*), (.*), (.*) and (.*)")
    public void givenASetScheduleRequest(final String deviceIdentification, final String weekday,
            final String startday, final String endday, final String actiontime, final String time,
            final String triggerwindow, final String lightvalue, final String triggertype) throws Exception {

        LOGGER.info(String.format(
                "GIVEN: \"a set schedule request for device {} with {}, {}, {}, {}, {}, {}, {}, and {}\".",
                this.device, weekday, startday, endday, actiontime, time, triggerwindow, lightvalue, triggertype));

        this.setUp();

        this.request = new SetScheduleRequest();

        this.request.setDeviceIdentification(deviceIdentification);

        final Schedule schedule = new Schedule();
        schedule.setWeekDay(weekday == null || weekday.isEmpty() ? null : WeekDayType.valueOf(weekday));
        schedule.setStartDay(DateUtils.convertToXMLGregorianCalendar(startday, DATE_FORMAT));
        schedule.setEndDay(DateUtils.convertToXMLGregorianCalendar(endday, DATE_FORMAT));
        schedule.setActionTime(actiontime == null || actiontime.isEmpty() ? null : ActionTimeType.valueOf(actiontime));
        schedule.setTime(time);
        schedule.setTriggerWindow(this.createWindow(triggerwindow));
        schedule.getLightValue().addAll(this.createLightValues(lightvalue));
        schedule.setTriggerType(triggertype == null || triggertype.isEmpty() ? null : TriggerType.valueOf(triggertype));

        this.request.getSchedules().add(schedule);
    }

    @DomainStep("the set schedule request refers to device (.*) with status (.*) which always returns (.*)")
    public void givenTheSetScheduleRequestRefersToDeviceWithStatus(final String deviceIdentification,
            final String status, final String response) throws Exception {
        LOGGER.info("GIVEN: \"the set schedule request refers to a device {} with status {} and response {}\".",
                new Object[] { deviceIdentification, status, response });

        switch (status.toUpperCase()) {
        case "ACTIVE":
            this.createDevice(deviceIdentification, true);
            when(this.deviceRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(this.device);
            when(this.ssldRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(this.device);
            when(this.ssldRepositoryMock.findOne(1L)).thenReturn(this.device);
            when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(
                    this.oslpDevice);
            when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(this.oslpDevice);

            // create oslp response
            final com.alliander.osgp.oslp.Oslp.SetScheduleResponse oslpResponse = com.alliander.osgp.oslp.Oslp.SetScheduleResponse
                    .newBuilder().setStatus(Status.valueOf(response)).build();

            this.oslpEnvelope = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                    .withSequenceNumber(SequenceNumberUtils.convertIntegerToByteArray(NEXT_SEQUENCE_NUMBER))
                    .withPayloadMessage(Message.newBuilder().setSetScheduleResponse(oslpResponse).build()).build();

            this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpEnvelope,
                    this.channelMock, this.device.getNetworkAddress());
            this.oslpChannelHandler.setDeviceRegistrationService(this.deviceRegistrationService);
            this.oslpDeviceService.setOslpChannelHandler(this.oslpChannelHandler);

            break;
        case "UNKNOWN":
            when(this.deviceRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(null);
            when(this.ssldRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(null);
            when(this.ssldRepositoryMock.findOne(1L)).thenReturn(null);
            break;
        case "UNREGISTERED":
            this.createDevice(deviceIdentification, false);
            when(this.deviceRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(this.device);
            when(this.ssldRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(this.device);
            when(this.ssldRepositoryMock.findOne(1L)).thenReturn(this.device);
            break;
        default:
            throw new Exception("Unknown device status");
        }
    }

    @DomainStep("the set schedule request refers to an authorised organisation")
    public void givenTheSetScheduleRequestRefersToAnAuthorisedOrganisation() {
        LOGGER.info("GIVEN: \"the set schedule request refers to an authorised organisation\".");

        this.organisation = new Organisation(ORGANISATION_ID, ORGANISATION_ID, ORGANISATION_PREFIX,
                PlatformFunctionGroup.USER);
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ID)).thenReturn(
                this.organisation);

        final List<DeviceAuthorization> authorizations = new ArrayList<>();
        authorizations.add(new DeviceAuthorizationBuilder().withDevice(this.device).withOrganisation(this.organisation)
                .withFunctionGroup(DeviceFunctionGroup.SCHEDULING).build());
        when(this.deviceAuthorizationRepositoryMock.findByOrganisationAndDevice(this.organisation, this.device))
        .thenReturn(authorizations);

        final List<DeviceFunction> deviceFunctions = new ArrayList<>();
        deviceFunctions.add(DeviceFunction.SET_LIGHT_SCHEDULE);

        when(this.deviceFunctionMappingRepositoryMock.findByDeviceFunctionGroups(any(ArrayList.class))).thenReturn(
                deviceFunctions);
    }

    @DomainStep("a get set schedule response request with correlationId (.*) and deviceId (.*)")
    public void givenAGetSetScheduleResultRequestWithCorrelationId(final String correlationId, final String deviceId) {
        LOGGER.info("GIVEN: \"a get set schedule response with correlationId {} and deviceId {}\".", correlationId,
                deviceId);

        this.setUp();

        this.setScheduleAsyncRequest = new SetScheduleAsyncRequest();

        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setCorrelationUid(correlationId);
        asyncRequest.setDeviceId(deviceId);

        this.setScheduleAsyncRequest.setAsyncRequest(asyncRequest);
    }

    @DomainStep("a set schedule response message with correlationId (.*), deviceId (.*), qresult (.*) and qdescription (.*) is found in the queue (.*)")
    public void givenAScheduleLightResponseMessageIsFoundInTheQueue(final String correlationId, final String deviceId,
            final String qresult, final String qdescription, final Boolean isFound) {
        LOGGER.info(
                "GIVEN: \"a set schedule response message with correlationId {}, deviceId {}, qresult {} and qdescription {} is found {}\".",
                correlationId, deviceId, qresult, qdescription, isFound);

        if (isFound) {
            final ObjectMessage messageMock = mock(ObjectMessage.class);

            try {
                when(messageMock.getJMSCorrelationID()).thenReturn(correlationId);
                when(messageMock.getStringProperty("OrganisationIdentification")).thenReturn(ORGANISATION_ID);
                when(messageMock.getStringProperty("DeviceIdentification")).thenReturn(deviceId);

                final ResponseMessageResultType result = ResponseMessageResultType.valueOf(qresult);
                Serializable dataObject = null;
                OsgpException exception = null;
                if (result.equals(ResponseMessageResultType.NOT_OK)) {
                    dataObject = new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, null,
                            new ValidationException());
                    exception = (OsgpException) dataObject;
                }
                final ResponseMessage message = new ResponseMessage(correlationId, ORGANISATION_ID, deviceId,
                        ResponseMessageResultType.valueOf(qresult), exception, dataObject);
                when(messageMock.getObject()).thenReturn(message);
            } catch (final JMSException e) {
                e.printStackTrace();
            }

            when(this.publicLightingResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(messageMock);
        } else {
            when(this.publicLightingResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(null);
        }
    }

    // === WHEN ===

    @DomainStep("the set schedule request is received")
    public void whenTheSetScheduleRequestIsReceived() {
        LOGGER.info("WHEN: \"the set schedule request is received\".");

        try {

            this.setScheduleAsyncResponse = this.scheduleManagementEndpoint.setLightSchedule(ORGANISATION_ID,
                    this.request);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    @DomainStep("the get set schedule response request is received")
    public void whenTheGetSetScheduleResultReqeustIsReceived() {
        LOGGER.info("WHEN: \"the set schedule request is received\".");

        try {
            this.response = this.scheduleManagementEndpoint.getSetLightScheduleResponse(ORGANISATION_ID,
                    this.setScheduleAsyncRequest);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    // === THEN ===

    @DomainStep("the set schedule request should return an async response with a correlationId and deviceId (.*)")
    public boolean thenTheSetScheduleRequestShouldReturnAnAsyncResponseWithACorrelationIdAndDeviceId(
            final String deviceId) {
        LOGGER.info(
                "THEN: \"the set schedule request should return a async response with a correlationId and deviceId {}\".",
                deviceId);

        try {
            Assert.assertNotNull("Set Schedule Async Response should not be null", this.setScheduleAsyncResponse);
            Assert.assertNotNull("Async Response should not be null", this.setScheduleAsyncResponse.getAsyncResponse());
            Assert.assertNotNull("CorrelationId should not be null", this.setScheduleAsyncResponse.getAsyncResponse()
                    .getCorrelationUid());
            Assert.assertNull("Throwable should be null", this.throwable);
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("the set schedule request should return a validation error")
    public boolean thenTheSetLightRequestShouldReturnAValidationError() {
        LOGGER.info("THEN: \"the set schedule request should return a validation error\".");
        try {
            Assert.assertNull("Set Schedule Async Response should be null", this.setScheduleAsyncResponse);
            Assert.assertNotNull("Throwable should not be null", this.throwable);
            Assert.assertTrue(this.throwable.getCause() instanceof ValidationException);
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("a set schedule oslp message is sent to device (.*) should be (.*)")
    public boolean thenASetScheduleOslpMessageShouldBeSent(final String deviceIdentification,
            final Boolean isMessageSent) {
        LOGGER.info("THEN: \"a set schedule oslp message is sent to device [{}] should be [{}]\".",
                deviceIdentification, isMessageSent);

        final int count = isMessageSent ? 1 : 0;

        try {
            final ArgumentCaptor<OslpEnvelope> argument = ArgumentCaptor.forClass(OslpEnvelope.class);
            verify(this.channelMock, timeout(10000).times(count)).write(argument.capture());

            if (isMessageSent) {
                this.oslpMessage = argument.getValue();

                Assert.assertTrue("Message should contain set schedule request.", this.oslpMessage.getPayloadMessage()
                        .hasSetScheduleRequest());
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("an ovl set schedule result message with result (.*) and description (.*) should be sent to the ovl out queue")
    public boolean thenAnOvlSetScheduleResultMessageShouldBeSentToTheOvlOutQueue(final String result,
            final String description) {
        LOGGER.info(
                "THEN: \"an ovl set schedule result message with result [{}] and description [{}] should be sent to the ovl out queue\".",
                result, description);

        try {
            final ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
            verify(this.webServiceResponseMessageSenderMock, timeout(10000).times(1)).send(argument.capture());

            final String expected = result.equals("NULL") ? null : result;
            final String actual = argument.getValue().getResult().getValue();

            LOGGER.info("THEN: message description: "
                    + (argument.getValue().getOsgpException() == null ? "" : argument.getValue().getOsgpException()
                            .getMessage()));

            Assert.assertTrue("Invalid result, found: " + actual + " , expected: " + expected, actual.equals(expected));

        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("the get set schedule response request should return a set schedule response with result (.*) and description (.*)")
    public boolean thenTheGetSetScheduleResultRequestShouldReturnAGetSetScheduleResultResponseWithResult(
            final String result, final String description) {
        LOGGER.info(
                "THEN: \"the get set schedule result request should return a get set schedule response with result {} and description {}\".",
                result, description);

        try {
            if ("NOT_OK".equals(result)) {
                Assert.assertNull("Set Schedule Response should be null", this.response);
                Assert.assertNotNull("Throwable should not be null", this.throwable);
                Assert.assertTrue("Throwable should contain a validation exception",
                        this.throwable.getCause() instanceof ValidationException);
            } else {
                Assert.assertNotNull("Response should not be null", this.response);

                final String expectedResult = result.equals("NULL") ? null : result;
                final String actualResult = this.response.getResult().toString();

                Assert.assertTrue("Invalid result, found: " + actualResult + " , expected: " + expectedResult,
                        (actualResult == null && expectedResult == null) || actualResult.equals(expectedResult));
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    // === private methods ===

    private void setUp() {
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.ssldRepositoryMock,
                this.organisationRepositoryMock, this.logItemRepositoryMock, this.channelMock,
                this.webServiceResponseMessageSenderMock, this.oslpDeviceRepositoryMock });

        this.scheduleManagementEndpoint = new PublicLightingScheduleManagementEndpoint(this.scheduleManagementService,
                new ScheduleManagementMapper());
        this.deviceRegistrationService.setSequenceNumberMaximum(OslpTestUtils.OSLP_SEQUENCE_NUMBER_MAXIMUM);
        this.deviceRegistrationService.setSequenceNumberWindow(OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW);

        this.throwable = null;

        this.request = null;
        this.setScheduleAsyncResponse = null;
        this.setScheduleAsyncRequest = null;
        this.response = null;
    }

    private WindowType createWindow(final String windowString) throws Exception {
        if (windowString == null || windowString.isEmpty()) {
            return null;
        }

        final String[] windowArray = windowString.split(",");
        if (windowArray.length != 2) {
            throw new Exception("Invalid window");
        }

        final WindowType window = new WindowType();
        window.setMinutesBefore(Integer.parseInt(windowArray[0]));
        window.setMinutesAfter(Integer.parseInt(windowArray[1]));

        return window;
    }

    private List<LightValue> createLightValues(final String lightValueString) {

        if (lightValueString == null || lightValueString.isEmpty()) {
            return null;
        }

        final List<LightValue> lightValues = new ArrayList<>();

        final String[] lvsArray = lightValueString.split(";");
        for (final String lvString : lvsArray) {
            final String[] lvArray = lvString.split(",");

            final LightValue lightValue = new LightValue();

            if (lvArray[0] != null && !lvArray[0].isEmpty()) {
                lightValue.setIndex(Integer.parseInt(lvArray[0]));
            }

            if (lvArray[1] != null && !lvArray[1].isEmpty()) {
                lightValue.setOn(Boolean.parseBoolean(lvArray[1]));
            }

            if (lvArray.length == 3 && lvArray[2] != null && !lvArray[2].isEmpty()) {
                lightValue.setDimValue(Integer.parseInt(lvArray[2]));
            }

            lightValues.add(lightValue);
        }

        return lightValues;
    }

    private void createDevice(final String deviceIdentification, final Boolean activated) {
        LOGGER.info("Creating device [{}] with active [{}]", deviceIdentification, activated);

        this.device = (Ssld) new DeviceBuilder().withDeviceIdentification(deviceIdentification)
                .withNetworkAddress(activated ? InetAddress.getLoopbackAddress() : null)
                .withPublicKeyPresent(PUBLIC_KEY_PRESENT)
                .withProtocolInfo(ProtocolInfoTestUtils.getProtocolInfo(PROTOCOL, PROTOCOL_VERSION))
                .isActivated(activated).build();

        this.oslpDevice = new OslpDeviceBuilder().withDeviceIdentification(deviceIdentification)
                .withDeviceUid(DEVICE_UID).build();
    }
}
