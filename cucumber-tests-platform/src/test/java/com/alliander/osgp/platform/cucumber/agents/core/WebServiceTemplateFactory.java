/**
 * Copyright 2014-2016 Smart Society Services B.V.
 */
package com.alliander.osgp.platform.cucumber.agents.core;

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

@SuppressWarnings("deprecation")
public class WebServiceTemplateFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceTemplateFactory.class);

    private final Map<String, WebServiceTemplate> webServiceTemplates;
    private final Lock lock = new ReentrantLock();

    private static final String ORGANISATION_IDENTIFICATION_HEADER = "OrganisationIdentification";
    private static final String USER_NAME_HEADER = "UserName";
    private static final String APPLICATION_NAME_HEADER = "ApplicationName";

    private static final String NAMESPACE = "http://www.alliander.com/schemas/osp/common";

    private final String applicationName;

    private final Jaxb2Marshaller marshaller;
    private final SaajSoapMessageFactory messageFactory;
    private final String defaultUri;
    private final String keyStoreType;
    private final String keyStoreLocation;
    private final String keyStorePassword;
    private final KeyStoreFactoryBean trustStoreFactory;

    public WebServiceTemplateFactory(final Jaxb2Marshaller marshaller, final SaajSoapMessageFactory messageFactory,
            final String defaultUri, final String keyStoreType, final String keyStoreLocation,
            final String keyStorePassword, final KeyStoreFactoryBean trustStoreFactory, final String applicationName) {
        this.marshaller = marshaller;
        this.messageFactory = messageFactory;
        this.defaultUri = defaultUri;
        this.keyStoreType = keyStoreType;
        this.keyStoreLocation = keyStoreLocation;
        this.keyStorePassword = keyStorePassword;
        this.trustStoreFactory = trustStoreFactory;
        this.webServiceTemplates = new HashMap<>();
        this.applicationName = applicationName;
    }

    public WebServiceTemplate getTemplate(final String organisationIdentification, final String userName) {

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

    private WebServiceTemplate createTemplate(final String organisationIdentification, final String userName) {
        final WebServiceTemplate webServiceTemplate = new WebServiceTemplate(this.messageFactory);

        webServiceTemplate.setDefaultUri(this.defaultUri);
        webServiceTemplate.setMarshaller(this.marshaller);
        webServiceTemplate.setUnmarshaller(this.marshaller);
        webServiceTemplate.setInterceptors(new ClientInterceptor[] { new OrganisationIdentificationClientInterceptor(
                organisationIdentification, userName, this.applicationName, NAMESPACE,
                ORGANISATION_IDENTIFICATION_HEADER, USER_NAME_HEADER, APPLICATION_NAME_HEADER) });
        if (this.defaultUri.contains("proxy-server")) {
            webServiceTemplate.setCheckConnectionForFault(false);
        } else {
            webServiceTemplate.setCheckConnectionForFault(true);
        }

        try {
            webServiceTemplate.setMessageSender(this.webServiceMessageSender(organisationIdentification));
        } catch (GeneralSecurityException | IOException e) {
            LOGGER.error("key error", e);
        }

        return webServiceTemplate;
    }

    private HttpComponentsMessageSender webServiceMessageSender(final String keystore) throws GeneralSecurityException,
    IOException {

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
    }
}
