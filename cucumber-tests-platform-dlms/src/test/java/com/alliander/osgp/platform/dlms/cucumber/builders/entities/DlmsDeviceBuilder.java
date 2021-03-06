/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.builders.entities;

import java.util.Map;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;

import com.alliander.osgp.platform.dlms.cucumber.steps.Defaults;
import com.alliander.osgp.platform.dlms.cucumber.steps.Keys;

public class DlmsDeviceBuilder implements CucumberBuilder<DlmsDevice> {

    private String deviceIdentification = Defaults.DEVICE_IDENTIFICATION;
    private Long version = Defaults.VERSION;
    private String iccId = Defaults.ICC_ID;
    private String communicationProvider = Defaults.COMMUNICATION_PROVIDER;
    private String communicationMethod = Defaults.COMMUNICATION_METHOD;
    private boolean hls3active = Defaults.HLS3ACTIVE;
    private boolean hls4active = Defaults.HLS4ACTIVE;
    private boolean hls5active = Defaults.HLS5ACTIVE;
    private Integer challengeLength = Defaults.CHALLENGE_LENGTH;
    private boolean withListSupported = Defaults.WITH_LIST_SUPPORTED;
    private boolean selectiveAccessSupported = Defaults.SELECTIVE_ACCESS_SUPPORTED;
    private boolean ipAddressIsStatic = Defaults.IP_ADDRESS_IS_STATIC;
    private Long port = Defaults.PORT;
    private Long clientId = Defaults.CLIENT_ID;
    private Long logicalId = Defaults.LOGICAL_ID;
    private boolean inDebugMode = Defaults.IN_DEBUG_MODE;

    private final SecurityKeyBuilder authenticationSecurityKeyBuilder = new SecurityKeyBuilder().setSecurityKeyType(
            SecurityKeyType.E_METER_AUTHENTICATION).setKey(Defaults.SECURITY_KEY_A);
    private final SecurityKeyBuilder encryptionSecurityKeyBuilder = new SecurityKeyBuilder().setSecurityKeyType(
            SecurityKeyType.E_METER_ENCRYPTION).setKey(Defaults.SECURITY_KEY_E);
    private final SecurityKeyBuilder masterSecurityKeyBuilder = new SecurityKeyBuilder().setSecurityKeyType(
            SecurityKeyType.E_METER_MASTER).setKey(Defaults.SECURITY_KEY_M);

    public DlmsDeviceBuilder setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
        return this;
    }

    public DlmsDeviceBuilder setVersion(final Long version) {
        this.version = version;
        return this;
    }

    public DlmsDeviceBuilder setIccId(final String iccId) {
        this.iccId = iccId;
        return this;
    }

    public DlmsDeviceBuilder setCommunicationProvider(final String communicationProvider) {
        this.communicationProvider = communicationProvider;
        return this;
    }

    public DlmsDeviceBuilder setCommunicationMethod(final String communicationMethod) {
        this.communicationMethod = communicationMethod;
        return this;
    }

    public DlmsDeviceBuilder setHls3Active(final boolean hls3active) {
        this.hls3active = hls3active;
        return this;
    }

    public DlmsDeviceBuilder setHls4Active(final boolean hls4active) {
        this.hls4active = hls4active;
        return this;
    }

    public DlmsDeviceBuilder setHls5Active(final boolean hls5active) {
        this.hls5active = hls5active;
        return this;
    }

    public DlmsDeviceBuilder setChallengeLength(final Integer challengeLength) {
        this.challengeLength = challengeLength;
        return this;
    }

    public DlmsDeviceBuilder setWithListSupported(final boolean withListSupported) {
        this.withListSupported = withListSupported;
        return this;
    }

    public DlmsDeviceBuilder setSelectiveAccessSupported(final boolean selectiveAccessSupported) {
        this.selectiveAccessSupported = selectiveAccessSupported;
        return this;
    }

    public DlmsDeviceBuilder setIpAddressIsStatic(final boolean ipAddressIsStatic) {
        this.ipAddressIsStatic = ipAddressIsStatic;
        return this;
    }

    public DlmsDeviceBuilder setPort(final Long port) {
        this.port = port;
        return this;
    }

    public DlmsDeviceBuilder setClientId(final Long clientId) {
        this.clientId = clientId;
        return this;
    }

    public DlmsDeviceBuilder setLogicalId(final Long logicalId) {
        this.logicalId = logicalId;
        return this;
    }

    public DlmsDeviceBuilder setInDebugMode(final boolean inDebugMode) {
        this.inDebugMode = inDebugMode;
        return this;
    }

    /**
     * Retrieve the SecurityKeyBuilder in order to manipulate its values. A
     * SecurityKey can not be set directly because there is a circular
     * dependency with the DlmsDevice. This dependency is resolved in the
     * {@link #build()} method of this class.
     *
     * @return Security key builder for authentication key.
     */
    public SecurityKeyBuilder getAuthenticationSecurityKeyBuilder() {
        return this.authenticationSecurityKeyBuilder;
    }

    /**
     * Retrieve the SecurityKeyBuilder in order to manipulate its values. A
     * SecurityKey can not be set directly because there is a circular
     * dependency with the DlmsDevice. This dependency is resolved in the
     * {@link #build()} method of this class.
     *
     * @return Security key builder for encryption key.
     */
    public SecurityKeyBuilder getEncryptionSecurityKeyBuilder() {
        return this.encryptionSecurityKeyBuilder;
    }

    /**
     * Retrieve the SecurityKeyBuilder in order to manipulate its values. A
     * SecurityKey can not be set directly because there is a circular
     * dependency with the DlmsDevice. This dependency is resolved in the
     * {@link #build()} method of this class.
     *
     * @return Security key builder for master key.
     */
    public SecurityKeyBuilder getMasterSecurityKeyBuilder() {
        return this.masterSecurityKeyBuilder;
    }

    @Override
    public DlmsDeviceBuilder withSettings(final Map<String, String> inputSettings) {
        if (inputSettings.containsKey(Keys.DEVICE_IDENTIFICATION)) {
            this.setDeviceIdentification((inputSettings.get(Keys.DEVICE_IDENTIFICATION)));
        }
        if (inputSettings.containsKey(Keys.VERSION)) {
            this.setVersion(Long.parseLong(inputSettings.get(Keys.VERSION)));
        }
        if (inputSettings.containsKey(Keys.ICC_ID)) {
            this.setIccId((inputSettings.get(Keys.ICC_ID)));
        }
        if (inputSettings.containsKey(Keys.COMMUNICATION_PROVIDER)) {
            this.setCommunicationProvider((inputSettings.get(Keys.COMMUNICATION_PROVIDER)));
        }
        if (inputSettings.containsKey(Keys.COMMUNICATION_METHOD)) {
            this.setCommunicationMethod((inputSettings.get(Keys.COMMUNICATION_METHOD)));
        }
        if (inputSettings.containsKey(Keys.HLS3ACTIVE)) {
            this.setHls3Active(Boolean.parseBoolean(inputSettings.get(Keys.HLS3ACTIVE)));
        }
        if (inputSettings.containsKey(Keys.HLS4ACTIVE)) {
            this.setHls4Active(Boolean.parseBoolean(inputSettings.get(Keys.HLS4ACTIVE)));
        }
        if (inputSettings.containsKey(Keys.HLS5ACTIVE)) {
            this.setHls5Active(Boolean.parseBoolean(inputSettings.get(Keys.HLS5ACTIVE)));
        }
        if (inputSettings.containsKey(Keys.CHALLENGE_LENGTH)) {
            this.setChallengeLength(Integer.parseInt(inputSettings.get(Keys.CHALLENGE_LENGTH)));
        }
        if (inputSettings.containsKey(Keys.WITH_LIST_SUPPORTED)) {
            this.setWithListSupported(Boolean.parseBoolean(inputSettings.get(Keys.WITH_LIST_SUPPORTED)));
        }
        if (inputSettings.containsKey(Keys.SELECTIVE_ACCESS_SUPPORTED)) {
            this.setSelectiveAccessSupported(Boolean.parseBoolean(inputSettings.get(Keys.SELECTIVE_ACCESS_SUPPORTED)));
        }
        if (inputSettings.containsKey(Keys.IP_ADDRESS_IS_STATIC)) {
            this.setIpAddressIsStatic(Boolean.parseBoolean(inputSettings.get(Keys.IP_ADDRESS_IS_STATIC)));
        }
        if (inputSettings.containsKey(Keys.PORT)) {
            this.setPort(Long.parseLong(inputSettings.get(Keys.PORT)));
        }
        if (inputSettings.containsKey(Keys.CLIENT_ID)) {
            this.setClientId(Long.parseLong(inputSettings.get(Keys.CLIENT_ID)));
        }
        if (inputSettings.containsKey(Keys.LOGICAL_ID)) {
            this.setLogicalId(Long.parseLong(inputSettings.get(Keys.LOGICAL_ID)));
        }
        if (inputSettings.containsKey(Keys.IN_DEBUG_MODE)) {
            this.setInDebugMode(Boolean.parseBoolean(inputSettings.get(Keys.IN_DEBUG_MODE)));
        }

        return this;
    }

    @Override
    public DlmsDevice build() {
        final DlmsDevice dlmsDevice = new DlmsDevice();
        dlmsDevice.setDeviceIdentification(this.deviceIdentification);
        dlmsDevice.setVersion(this.version);
        dlmsDevice.setIccId(this.iccId);
        dlmsDevice.setCommunicationProvider(this.communicationProvider);
        dlmsDevice.setCommunicationMethod(this.communicationMethod);
        dlmsDevice.setHls3Active(this.hls3active);
        dlmsDevice.setHls4Active(this.hls4active);
        dlmsDevice.setHls5Active(this.hls5active);
        dlmsDevice.setChallengeLength(this.challengeLength);
        dlmsDevice.setWithListSupported(this.withListSupported);
        dlmsDevice.setSelectiveAccessSupported(this.selectiveAccessSupported);
        dlmsDevice.setIpAddressIsStatic(this.ipAddressIsStatic);
        dlmsDevice.setPort(this.port);
        dlmsDevice.setClientId(this.clientId);
        dlmsDevice.setLogicalId(this.logicalId);
        dlmsDevice.setInDebugMode(this.inDebugMode);

        /**
         * It is not ideal that the build() method for security keys is called
         * here, but necessary because the security key needs the dlmsDevice in
         * order to be created. This seems to be the only way to work around
         * this circular dependency.
         */
        dlmsDevice.addSecurityKey(this.authenticationSecurityKeyBuilder.setDlmsDevice(dlmsDevice).build());
        dlmsDevice.addSecurityKey(this.encryptionSecurityKeyBuilder.setDlmsDevice(dlmsDevice).build());
        dlmsDevice.addSecurityKey(this.masterSecurityKeyBuilder.setDlmsDevice(dlmsDevice).build());

        return dlmsDevice;
    }
}
