/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.microgrids.adhocmanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataResponse;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataSystemIdentifier;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.Measurement;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.Profile;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.helpers.SettingsHelper;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.ws.microgrids.MicrogridsStepsBase;
import com.alliander.osgp.platform.cucumber.support.ws.microgrids.adhocmanagement.AdHocManagementServiceAdapter;
import com.alliander.osgp.platform.cucumber.support.ws.microgrids.adhocmanagement.GetDataRequestBuilder;

import cucumber.api.PendingException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GetDataSteps extends MicrogridsStepsBase {

    /**
     * Delta value for which two measurement values are considered equal if
     * their difference does not exceed it.
     */
    private static final double DELTA_FOR_MEASUREMENT_VALUE = 0.0001;

    @Autowired
    private AdHocManagementServiceAdapter adHocManagementServiceAdapter;

    @When("^a get data request is received$")
    public void aGetDataRequestIsReceived(final Map<String, String> requestParameters) throws Throwable {

        final String organizationIdentification = (String) ScenarioContext.Current()
                .get(Keys.KEY_ORGANISATION_IDENTIFICATION, Defaults.DEFAULT_ORGANISATION_IDENTIFICATION);
        ScenarioContext.Current().put(Keys.KEY_ORGANISATION_IDENTIFICATION, organizationIdentification);
        final String userName = (String) ScenarioContext.Current().get(Keys.KEY_USER_NAME, Defaults.DEFAULT_USER_NAME);
        ScenarioContext.Current().put(Keys.KEY_USER_NAME, userName);

        final GetDataRequest getDataRequest = GetDataRequestBuilder.fromParameterMap(requestParameters);
        final GetDataAsyncResponse response = this.adHocManagementServiceAdapter.getDataAsync(getDataRequest);

        ScenarioContext.Current().put(Keys.KEY_CORRELATION_UID, response.getAsyncResponse().getCorrelationUid());
    }

    @Then("^the get data response should be returned$")
    public void theGetDataResponseShouldBeReturned(final Map<String, String> responseParameters) throws Throwable {

        final String correlationUid = (String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID);
        final Map<String, String> extendedParameters = SettingsHelper.addDefault(responseParameters,
                Keys.KEY_CORRELATION_UID, correlationUid);

        final GetDataAsyncRequest getDataAsyncRequest = GetDataRequestBuilder.fromParameterMapAsync(extendedParameters);
        final GetDataResponse response = this.adHocManagementServiceAdapter.getData(getDataAsyncRequest);

        final String expectedResult = responseParameters.get(Keys.KEY_RESULT);
        assertNotNull("Result", response.getResult());
        assertEquals("Result", expectedResult, response.getResult().name());

        if (!responseParameters.containsKey(Keys.KEY_NUMBER_OF_SYSTEMS)) {
            throw new AssertionError("The Step DataTable must contain the expected number of systems with key \""
                    + Keys.KEY_NUMBER_OF_SYSTEMS + "\" when confirming a returned get data response.");
        }
        final int expectedNumberOfSystems = Integer.parseInt(responseParameters.get(Keys.KEY_NUMBER_OF_SYSTEMS));

        final List<GetDataSystemIdentifier> systemIdentifiers = response.getSystem();
        assertEquals("Number of Systems", expectedNumberOfSystems, systemIdentifiers.size());

        for (int i = 0; i < expectedNumberOfSystems; i++) {
            this.assertSystemResponse(responseParameters, systemIdentifiers, i);
        }
    }

    private void assertSystemResponse(final Map<String, String> responseParameters,
            final List<GetDataSystemIdentifier> systemIdentifiers, final int systemIndex) {

        final int numberOfSystems = systemIdentifiers.size();
        final String indexPostfix = "_" + (systemIndex + 1);
        final String systemDescription = "System[" + (systemIndex + 1) + "/" + numberOfSystems + "]";

        final GetDataSystemIdentifier systemIdentifier = systemIdentifiers.get(systemIndex);

        if (responseParameters.containsKey(Keys.KEY_SYSTEM_TYPE.concat(indexPostfix))) {
            final String expectedType = responseParameters.get(Keys.KEY_SYSTEM_TYPE.concat(indexPostfix));
            assertEquals(systemDescription + " type", expectedType, systemIdentifier.getType());
        }

        if (responseParameters.containsKey(Keys.KEY_NUMBER_OF_MEASUREMENTS.concat(indexPostfix))) {
            this.assertMeasurements(responseParameters, systemIdentifier.getMeasurement(), numberOfSystems, systemIndex,
                    systemDescription, indexPostfix);
        }

        if (responseParameters.containsKey(Keys.KEY_NUMBER_OF_PROFILES.concat(indexPostfix))) {
            this.assertProfiles(responseParameters, systemIdentifier.getProfile(), numberOfSystems, systemIndex,
                    systemDescription, indexPostfix);
        }
    }

    private void assertMeasurements(final Map<String, String> responseParameters, final List<Measurement> measurements,
            final int numberOfSystems, final int systemIndex, final String systemDescription,
            final String indexPostfix) {

        final int expectedNumberOfMeasurements = Integer.parseInt(
                responseParameters.get(Keys.KEY_NUMBER_OF_MEASUREMENTS.concat(indexPostfix)));
        assertEquals(systemDescription + " number of Measurements", expectedNumberOfMeasurements, measurements.size());
        for (int i = 0; i < expectedNumberOfMeasurements; i++) {
            this.assertMeasurementResponse(responseParameters, numberOfSystems, systemIndex, systemDescription,
                    measurements, i);
        }
    }

    private void assertMeasurementResponse(final Map<String, String> responseParameters, final int numberOfSystems,
            final int systemIndex, final String systemDescription, final List<Measurement> measurements,
            final int measurementIndex) {

        final int numberOfMeasurements = measurements.size();
        final String indexPostfix = "_" + (systemIndex + 1) + "_" + (measurementIndex + 1);
        final String measurementDescription = systemDescription + " Measurement[" + (measurementIndex + 1) + "/"
                + numberOfMeasurements + "]";

        final Measurement measurement = measurements.get(measurementIndex);

        final Integer expectedId = Integer
                .valueOf(responseParameters.get(Keys.KEY_MEASUREMENT_ID.concat(indexPostfix)));
        assertEquals(measurementDescription + " id", expectedId, measurement.getId());

        final String expectedNode = responseParameters.get(Keys.KEY_MEASUREMENT_NODE.concat(indexPostfix));
        assertEquals(measurementDescription + " node", expectedNode, measurement.getNode());

        final int expectedQualifier = Integer
                .parseInt(responseParameters.get(Keys.KEY_MEASUREMENT_QUALIFIER.concat(indexPostfix)));
        assertEquals(measurementDescription + " Qualifier", expectedQualifier, measurement.getQualifier());

        assertNotNull(measurementDescription + " Time", measurement.getTime());

        final double expectedValue = Double
                .parseDouble(responseParameters.get(Keys.KEY_MEASUREMENT_VALUE.concat(indexPostfix)));
        assertEquals(measurementDescription + " Value", expectedValue, measurement.getValue(),
                DELTA_FOR_MEASUREMENT_VALUE);
    }

    private void assertProfiles(final Map<String, String> responseParameters, final List<Profile> profiles,
            final int numberOfSystems, final int systemIndex, final String systemDescription,
            final String indexPostfix) {

        final int expectedNumberOfProfiles = Integer
                .parseInt(responseParameters.get(Keys.KEY_NUMBER_OF_PROFILES.concat(indexPostfix)));
        assertEquals(systemDescription + " number of Profiles", expectedNumberOfProfiles, profiles.size());
        for (int i = 0; i < expectedNumberOfProfiles; i++) {
            this.assertProfileResponse(responseParameters, numberOfSystems, systemIndex, systemDescription, profiles,
                    i);
        }
    }

    private void assertProfileResponse(final Map<String, String> responseParameters, final int numberOfSystems,
            final int systemIndex, final String systemDescription, final List<Profile> profiles,
            final int profileIndex) {
        throw new PendingException();
    }
}
