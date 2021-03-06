/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.builders.entities;

import java.util.Map;

import com.alliander.osgp.domain.core.entities.Device;

public class DeviceBuilder extends BaseDeviceBuilder<DeviceBuilder> implements CucumberBuilder<Device> {

    @Override
    public DeviceBuilder withSettings(final Map<String, String> inputSettings) {
        super.withSettings(inputSettings);
        return this;
    }

    @Override
    public Device build() {
        final Device device = new Device(this.deviceIdentification, this.alias, this.containerCity,
                this.containerPostalCode, this.containerStreet, this.containerNumber, this.containerMunicipality,
                this.gpsLatitude, this.gpsLongitude);
        device.setActive(this.isActive);
        device.updateRegistrationData(this.networkAddress, this.deviceType);

        // After updateRegistrationData because that sets active to true again.
        device.setActivated(this.isActivated);

        device.updateProtocol(this.protocolInfo);
        device.updateInMaintenance(this.inMaintenance);
        device.updateGatewayDevice(this.gatewayDevice);
        device.setVersion(this.version);
        device.setDeviceModel(this.deviceModel);
        device.setTechnicalInstallationDate(this.technicalInstallationDate);

        return device;
    }
}
