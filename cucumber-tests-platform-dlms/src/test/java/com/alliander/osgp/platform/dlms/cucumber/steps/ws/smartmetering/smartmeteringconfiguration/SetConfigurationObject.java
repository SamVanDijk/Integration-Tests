/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.smartmeteringconfiguration;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SetConfigurationObject extends SmartMeteringStepsBase {
    private static final String PATH_RESULT = "/Envelope/Body/SetConfigurationObjectResponse/Result/text()";

    private static final String TEST_SUITE_XML = "SmartmeterConfiguration";
    private static final String TEST_CASE_XML = "216 Retrieve SetConfigurationObject result";
    private static final String TEST_CASE_NAME_REQUEST = "SetConfigurationObject - Request 1";
    private static final String TEST_CASE_NAME_GETRESPONSE_REQUEST = "GetSetConfigurationObjectResponse - Request 1";

    @When("^the set configuration object request is received$")
    public void theSetConfigurationObjectRequestIsReceived(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_IDENTIFICATION,
                getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP
                .put(Keys.KEY_ORGANISATION_IDENTIFICATION,
                        getString(settings, Keys.KEY_ORGANISATION_IDENTIFICATION,
                                Defaults.DEFAULT_ORGANISATION_IDENTIFICATION));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the configuration object should be set on the device$")
    public void theConfigurationObjectShouldBeSetOnTheDevice(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_IDENTIFICATION,
                getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP
                .put(Keys.KEY_CORRELATION_UID, ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_GETRESPONSE_REQUEST, TEST_CASE_XML,
                TEST_SUITE_XML);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, Defaults.EXPECTED_RESULT_OK));
    }
}
