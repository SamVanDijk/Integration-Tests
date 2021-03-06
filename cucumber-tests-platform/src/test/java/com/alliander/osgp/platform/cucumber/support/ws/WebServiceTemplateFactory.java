/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.support.ws;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.security.support.KeyStoreFactoryBean;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.alliander.osgp.adapter.ws.smartmetering.infra.ws.OrganisationIdentificationClientInterceptor;

public class WebServiceTemplateFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceTemplateFactory.class);

    private static final String ORGANISATION_IDENTIFICATION_HEADER = "OrganisationIdentification";
    private static final String USER_NAME_HEADER = "UserName";
    private static final String APPLICATION_NAME_HEADER = "ApplicationName";

    private static final String NAMESPACE = "http://www.alliander.com/schemas/osp/common";

    private final Map<String, WebServiceTemplate> webServiceTemplates = new HashMap<>();
    private final Lock lock = new ReentrantLock();

    private String applicationName;
    private Jaxb2Marshaller marshaller;
    private SaajSoapMessageFactory messageFactory;
    private String defaultUri;
    private String keyStoreType;
    private String keyStoreLocation;
    private String keyStorePassword;
    private KeyStoreFactoryBean trustStoreFactory;

    public static class Builder {

        private String applicationName;
        private Jaxb2Marshaller marshaller;
        private SaajSoapMessageFactory messageFactory;
        private String defaultUri;
        private String keyStoreType;
        private String keyStoreLocation;
        private String keyStorePassword;
        private KeyStoreFactoryBean trustStoreFactory;

        public Builder setApplicationName(final String applicationName) {
            this.applicationName = applicationName;
            return this;
        }

        public Builder setMarshaller(final Jaxb2Marshaller marshaller) {
            this.marshaller = marshaller;
            return this;
        }

        public Builder setMessageFactory(final SaajSoapMessageFactory messageFactory) {
            this.messageFactory = messageFactory;
            return this;
        }

        public Builder setDefaultUri(final String defaultUri) {
            this.defaultUri = defaultUri;
            return this;
        }

        public Builder setKeyStoreType(final String keyStoreType) {
            this.keyStoreType = keyStoreType;
            return this;
        }

        public Builder setKeyStoreLocation(final String keyStoreLocation) {
            this.keyStoreLocation = keyStoreLocation;
            return this;
        }

        public Builder setKeyStorePassword(final String keyStorePassword) {
            this.keyStorePassword = keyStorePassword;
            return this;
        }

        public Builder setTrustStoreFactory(final KeyStoreFactoryBean trustStoreFactory) {
            this.trustStoreFactory = trustStoreFactory;
            return this;
        }

        public WebServiceTemplateFactory build() {
            final WebServiceTemplateFactory webServiceTemplateFactory = new WebServiceTemplateFactory();
            webServiceTemplateFactory.marshaller = this.marshaller;
            webServiceTemplateFactory.messageFactory = this.messageFactory;
            webServiceTemplateFactory.defaultUri = this.defaultUri;
            webServiceTemplateFactory.keyStoreType = this.keyStoreType;
            webServiceTemplateFactory.keyStoreLocation = this.keyStoreLocation;
            webServiceTemplateFactory.keyStorePassword = this.keyStorePassword;
            webServiceTemplateFactory.trustStoreFactory = this.trustStoreFactory;
            webServiceTemplateFactory.applicationName = this.applicationName;
            return webServiceTemplateFactory;
        }
    }

    public WebServiceTemplate getTemplate(final String organisationIdentification, final String userName)
            throws WebServiceSecurityException {

        if (StringUtils.isEmpty(organisationIdentification)) {
            LOGGER.error("organisationIdentification is empty or null");
        }
        if (StringUtils.isEmpty(userName)) {
            LOGGER.error("userName is empty or null");
        }
        if (StringUtils.isEmpty(this.applicationName)) {
            LOGGER.error("applicationName is empty or null");
        }

        WebServiceTemplate webServiceTemplate = null;
        try {
            this.lock.lock();

            // Create new webservice template, if not yet available for
            // organisation
            final String key = organisationIdentification.concat("-").concat(userName).concat(this.applicationName);
            if (!this.webServiceTemplates.containsKey(key)) {
                this.webServiceTemplates.put(key, this.createTemplate(organisationIdentification, userName));
            }

            webServiceTemplate = this.webServiceTemplates.get(key);

        } finally {
            this.lock.unlock();
        }

        return webServiceTemplate;
    }

    private WebServiceTemplate createTemplate(final String organisationIdentification, final String userName)
            throws WebServiceSecurityException {
        final WebServiceTemplate webServiceTemplate = new WebServiceTemplate(this.messageFactory);

        webServiceTemplate.setDefaultUri(this.defaultUri);
        webServiceTemplate.setMarshaller(this.marshaller);
        webServiceTemplate.setUnmarshaller(this.marshaller);

        webServiceTemplate.setInterceptors(
                new ClientInterceptor[] { new OrganisationIdentificationClientInterceptor(organisationIdentification,
                        userName, this.applicationName, NAMESPACE, ORGANISATION_IDENTIFICATION_HEADER, USER_NAME_HEADER,
                        APPLICATION_NAME_HEADER) });

        try {
            webServiceTemplate.setMessageSender(this.webServiceMessageSender(organisationIdentification));
        } catch (final WebServiceSecurityException e) {
            LOGGER.warn("Security exception, cause: {}", e);
            throw e;
        }

        return webServiceTemplate;
    }

    private HttpComponentsMessageSender webServiceMessageSender(final String keystore)
            throws WebServiceSecurityException {

        try {
            // Open keystore, assuming same identity
            final KeyStoreFactoryBean keyStoreFactory = new KeyStoreFactoryBean();
            keyStoreFactory.setType(this.keyStoreType);
            keyStoreFactory.setLocation(new FileSystemResource(this.keyStoreLocation + "/" + keystore + ".pfx"));
            keyStoreFactory.setPassword(this.keyStorePassword);
            keyStoreFactory.afterPropertiesSet();

            final KeyStore keyStore = keyStoreFactory.getObject();
            if (keyStore == null || keyStore.size() == 0) {
                throw new KeyStoreException("Key store is empty");
            }

            // Create HTTP sender and associate keystore to it
            final HttpComponentsMessageSender sender = new HttpComponentsMessageSender();
            final HttpClient client = sender.getHttpClient();
            final SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore, this.keyStorePassword,
                    this.trustStoreFactory.getObject());

            final Scheme scheme = new Scheme("https", 443, socketFactory);
            client.getConnectionManager().getSchemeRegistry().register(scheme);

            return sender;
        } catch (IOException | GeneralSecurityException e) {
            throw new WebServiceSecurityException("An exception occured while creating a secured connection.", e);
        }
    }

}
