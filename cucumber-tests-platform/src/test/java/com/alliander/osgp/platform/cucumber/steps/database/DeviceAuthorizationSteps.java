/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.database;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getEnum;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.platform.cucumber.steps.Defaults;

import cucumber.api.java.en.Given;

public class DeviceAuthorizationSteps {

    @Autowired
    private DeviceAuthorizationRepository authorizationRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private OrganisationRepository organizationRepository;

    /**
     * Generic method which adds a device authorization using the settings.
     *
     * @param settings
     *            The settings for the device authorization to be used.
     * @throws Throwable
     */
    @Given("^a device authorization$")
    public void aDeviceAuthorization(final Map<String, String> settings) throws Throwable {

        final Device device = this.deviceRepository.findByDeviceIdentification(getString(settings,
                "DeviceIdentification", DeviceSteps.DEFAULT_DEVICE_IDENTIFICATION));

        final Organisation organization = this.organizationRepository.findByOrganisationIdentification(getString(
                settings, "OrganizationIdentification", Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        final DeviceFunctionGroup functionGroup = getEnum(settings, "DeviceFunctionGroup", DeviceFunctionGroup.class,
                DeviceFunctionGroup.OWNER);

        final DeviceAuthorization authorization = device.addAuthorization(organization, functionGroup);
        this.authorizationRepository.save(authorization);
        this.deviceRepository.save(device);
    }
}
