/**
 * Copyright 2016 Smart Society Services B.V. *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.smartmeteringconfiguration;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.SmartMetering;
import com.alliander.osgp.platform.cucumber.hooks.SimulatePushedAlarmsHooks;
import com.alliander.osgp.platform.cucumber.smartmeteringmonitoring.ActualMeterReadsGas;
import com.alliander.osgp.platform.cucumber.support.DatabaseReader;
import com.alliander.osgp.platform.cucumber.support.DeviceId;
import com.alliander.osgp.platform.cucumber.support.OrganisationId;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SetPushSetupAlarm extends SmartMetering {
    private static final String PATH_RESULT = "/Envelope/Body/SetPushSetupAlarmResponse/Result/text()";

    private static final String XPATH_MATCHER_RESULT = "OK";

    private static final String TEST_SUITE_XML = "SmartmeterConfiguration";
    private static final String TEST_CASE_XML = "125 Receive Alarm Notifications";
    private static final String TEST_CASE_NAME_REQUEST = "SetPushSetupAlarm - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetSetPushSetupAlarmResponse - Request 1";

    private static final Logger LOGGER = LoggerFactory.getLogger(ActualMeterReadsGas.class);
    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    private static final String KnownDevice = "E9998000014123414";
    private static final String UnknownDevice = "Z9876543210123456";

    @Autowired
    private DeviceId deviceId;

    @Autowired
    private OrganisationId organisationId;

    @Autowired
    private DatabaseReader databaseReader;

    @When("^an alarm notification is received from a known device$")
    public void anAlarmNotificationIsReceivedFromAKnownDevice() throws Throwable {
        try {
            SimulatePushedAlarmsHooks.simulateAlarm(KnownDevice, new byte[] { 0x2C, 0x00, 0x00, 0x01, 0x02 });
            SimulatePushedAlarmsHooks.simulateAlarm(KnownDevice, new byte[] { 0x2C, 0x04, 0x20, 0x00, 0x00 });
        } catch (final Exception e) {
            LOGGER.error("Error occured simulateAlarm: ", e);
        }

        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());

        this.RequestRunner(PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the alarm should be pushed to OSGP$")
    public void theAlarmShouldBePushedToOSGP() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);

        this.ResponseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, XPATH_MATCHER_RESULT));
    }

    @And("^the alarm should be pushed to the osgp_logging database \"([^\"]*)\" table$")
    public void theAlarmShouldBePushedToTheOsgpLoggingDatabaseTable(final String table) throws Throwable {
        final List<String> result = this.databaseReader.readDevideLogItem(table, KnownDevice, UnknownDevice);
        for (int i = 0; i < 4; i++) {
            LOGGER.info(result.get(i));
        }
    }

    @When("^an alarm notification is received from an unknown device$")
    public void anAlarmNotificationIsReceivedFromAnUnknownDevice() throws Throwable {
        try {
            SimulatePushedAlarmsHooks.simulateAlarm(UnknownDevice, new byte[] { 0x2C, 0x00, 0x00, 0x01, 0x02 });
            SimulatePushedAlarmsHooks.simulateAlarm(UnknownDevice, new byte[] { 0x2C, 0x04, 0x20, 0x00, 0x00 });
        } catch (final Exception e) {
            LOGGER.error("Error occured simulateAlarm: ", e);
        }

        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());

        this.RequestRunner(PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

}