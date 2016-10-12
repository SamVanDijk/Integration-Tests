/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.admin.devicemanagement;

import java.util.Map;

import org.junit.Assert;

import com.alliander.osgp.platform.cucumber.steps.ws.admin.AdminStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class FindDeviceAuthorizationSteps extends AdminStepsBase {

    private static final String TEST_SUITE = "DeviceManagement";
    private static final String TEST_CASE_NAME = "AT FindDeviceAuthorisations";
    private static final String TEST_CASE_NAME_REQUEST = "FindDeviceAuthorisations";

    @When("^receiving a find device authorizations request$")
    public void receivingAFindDeviceAuthorizationsRequest(final Map<String, String> requestSettings) throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_LABEL, requestSettings.get("DeviceIdentification"));

        this.requestRunner(TestStepStatus.UNKNOWN, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_NAME, TEST_SUITE);
    }

    /**
     * Verify that the deactivate device response is successful.
     * @throws Throwable
     */
    @Then("^the find device authorizations response contains$")
    public void the_find_device_authorizations_response_contains(final Map<String, String> expectedResponse) throws Throwable {

        // XPath Query for showing all nodes value
        final String devicePath = "//Envelope/Body/FindDeviceAuthorisationsResponse/DeviceAuthorisations/deviceIdentification/text()";
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, devicePath, expectedResponse.get("DeviceIdentification")));
        final String functionGroupPath = "//Envelope/Body/FindDeviceAuthorisationsResponse/DeviceAuthorisations/functionGroup/text()";
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, functionGroupPath, expectedResponse.get("DeviceFunctionGroup")));
    }
}
