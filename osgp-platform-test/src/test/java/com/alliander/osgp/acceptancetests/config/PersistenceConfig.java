/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.config;

import static org.mockito.Mockito.mock;

import org.springframework.context.annotation.Bean;

import com.alliander.osgp.adapter.protocol.oslp.application.config.QualifierProtocolOslp;
import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.adapter.ws.infra.specifications.JpaDeviceSpecifications;
import com.alliander.osgp.adapter.ws.infra.specifications.JpaEventSpecifications;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceAuthorizationRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableManufacturerRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableSsldRepository;
import com.alliander.osgp.core.db.api.repositories.DeviceDataRepository;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceFunctionMappingRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.DomainInfoRepository;
import com.alliander.osgp.domain.core.repositories.EventRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.ProtocolInfoRepository;
import com.alliander.osgp.domain.core.repositories.ScheduledTaskRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.specifications.DeviceSpecifications;
import com.alliander.osgp.domain.core.specifications.EventSpecifications;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;
import com.alliander.osgp.logging.domain.repositories.WebServiceMonitorLogRepository;

public class PersistenceConfig {

    // // WS LOGGING
    @Bean
    public WebServiceMonitorLogRepository WebServiceLoggingRepository() {
        return mock(WebServiceMonitorLogRepository.class);
    }

    @Bean
    public WritableDeviceRepository writableDeviceRepositoryMock() {
        return mock(WritableDeviceRepository.class);
    }

    @Bean
    public WritableDeviceAuthorizationRepository writableDeviceAuthorizationRepositoryMock() {
        return mock(WritableDeviceAuthorizationRepository.class);
    }

    // OSGP CORE

    @Bean
    DeviceFunctionMappingRepository deviceFunctionMappingRepositoryMock() {
        return mock(DeviceFunctionMappingRepository.class);
    }

    @Bean
    public DeviceAuthorizationRepository deviceAuthorizationRepositoryMock() {
        return mock(DeviceAuthorizationRepository.class);
    }

    @Bean
    public DeviceRepository deviceRepositoryMock() {
        return mock(DeviceRepository.class);
    }

    @Bean
    public DeviceDataRepository deviceDataRepositoryMock() {
        return mock(DeviceDataRepository.class);
    }

    @Bean
    public SmartMeterRepository smartMeteringDeviceRepositoryMock() {
        return mock(SmartMeterRepository.class);
    }

    @Bean
    public SsldRepository ssldRepository() {
        return mock(SsldRepository.class);
    }

    @Bean
    public WritableSsldRepository writableSsldRepository() {
        return mock(WritableSsldRepository.class);
    }

    @Bean
    public DeviceSpecifications deviceSpecifications() {
        return new JpaDeviceSpecifications();
    }

    @Bean
    public DomainInfoRepository domainInfoRepositoryMock() {
        return mock(DomainInfoRepository.class);
    }

    @Bean
    public EventRepository eventRepositoryMock() {
        return mock(EventRepository.class);
    }

    @Bean
    public EventSpecifications eventSpecifications() {
        return new JpaEventSpecifications();
    }

    @Bean
    public DeviceLogItemRepository deviceLogItemRepositoryMock() {
        return mock(DeviceLogItemRepository.class);
    }

    @Bean
    public OrganisationRepository organisationRepositoryMock() {
        return mock(OrganisationRepository.class);
    }

    @Bean
    public ProtocolInfoRepository protocolInfoRepositoryMock() {
        return mock(ProtocolInfoRepository.class);
    }

    @Bean
    public ScheduledTaskRepository scheduleTaskRepositoryMock() {
        return mock(ScheduledTaskRepository.class);
    }

    @Bean
    public WritableManufacturerRepository writableManufacturerRepositoryMock() {
        return mock(WritableManufacturerRepository.class);
    }

    // OSLP
    @Bean
    @QualifierProtocolOslp
    public OslpDeviceRepository oslpDeviceRepositoryMock() {
        return mock(OslpDeviceRepository.class);
    }
}
