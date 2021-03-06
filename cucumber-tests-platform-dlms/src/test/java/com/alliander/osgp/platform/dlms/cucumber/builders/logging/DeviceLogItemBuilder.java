/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.builders.logging;

import org.springframework.stereotype.Component;

import com.alliander.osgp.logging.domain.entities.DeviceLogItem;
import com.alliander.osgp.platform.cucumber.steps.Defaults;

@Component
public class DeviceLogItemBuilder {

    private String deviceIdentification = Defaults.DEFAULT_DEVICE_IDENTIFICATION;

    private String organisationIdentification = Defaults.DEFAULT_ORGANISATION_IDENTIFICATION;

    private boolean incoming = true;

    private boolean valid = true;

    private String encoded = "encoded";

    private String decoded = "decoded";

    private int payloadMessageSerializedSize = 0;

    public DeviceLogItem build() {
        return new DeviceLogItem(this.organisationIdentification, this.deviceIdentification, this.deviceIdentification,
                this.incoming, this.valid, this.encoded, this.decoded, this.payloadMessageSerializedSize);
    }

    public DeviceLogItemBuilder withDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
        return this;
    }

    public DeviceLogItemBuilder withOrganisationIdentification(final String organisationIdentification) {
        this.organisationIdentification = organisationIdentification;
        return this;
    }

    public DeviceLogItemBuilder withIncoming(final boolean incoming) {
        this.incoming = incoming;
        return this;
    }

    public DeviceLogItemBuilder withValid(final boolean valid) {
        this.valid = valid;
        return this;
    }

    public DeviceLogItemBuilder withEncoded(final String encoded) {
        this.encoded = encoded;
        return this;
    }

    public DeviceLogItemBuilder withDecoded(final String decoded) {
        this.decoded = decoded;
        return this;
    }

    public DeviceLogItemBuilder withPayloadMessageSerializedSize(final int payloadMessageSerializedSize) {
        this.payloadMessageSerializedSize = payloadMessageSerializedSize;
        return this;
    }
}
