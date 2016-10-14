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

import com.alliander.definitions.osgp.admin.devicemanagement_v1.ActivateDeviceRequest;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.ws.admin.AdminStepsBase;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ActivateDeviceSteps extends AdminStepsBase {

    @When("^receiving a activate device request$")
    public void receivingAActivateDeviceRequest(final Map<String, String> requestSettings) throws Throwable {
    	
    	ActivateDeviceRequest request = new ActivateDeviceRequest();
    	request.setDeviceIdentification(getString(requestSettings, Defaults.DEVICE_IDENTIFICATION_LABEL, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

    	this.client.port.activateDevice(request);
    }
    
    /**
     * Verify that the activate device response is successful.
     * @throws Throwable
     */
    @Then("^the activate device response contains$")
    public void the_activate_device_response_contains(Map<String, String> expectedResponse) throws Throwable {
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, "/Envelope/Body/ActivateDeviceResponse/Result/text()", expectedResponse.get("Result")));
    }
}
