package com.alliander.osgp.platform.cucumber.agents.ws.osgp.admin;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.xml.ws.BindingProvider;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.security.support.KeyStoreFactoryBean;

import com.alliander.definitions.osgp.admin.devicemanagement_v1.DeviceManagementPort;
import com.alliander.definitions.osgp.admin.devicemanagement_v1.DeviceManagementPortService;

@Component
public class DeviceManagementClient {

	private DeviceManagementPortService service;

	public DeviceManagementPort port;

	private KeyStoreFactoryBean trustStoreFactory;
	
	/**
	 * 
	 * @throws Throwable
	 */
	public DeviceManagementClient() throws Throwable {
		
		trustStoreFactory = new KeyStoreFactoryBean();
		trustStoreFactory.setType("jks");
		trustStoreFactory.setLocation(new FileSystemResource("/etc/ssl/certs/trust.jks"));
        trustStoreFactory.setPassword("1234");
        
		this.service = new DeviceManagementPortService();
		
		this.port = service.getDeviceManagementPortSoap11();
		
		BindingProvider bindingProvider = (BindingProvider) this.port;
		
		// Set the correct url
		bindingProvider.getRequestContext().put(
		      BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
		      "https://localhost/osgp-adapter-ws-admin/admin/deviceManagementService/");
		
	    // Set the certificate
		bindingProvider.getRequestContext().put(
				"com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory", 
				getCustomSocketFactory());
		
		//bindingProvider.getRequestContext().put("ApplicationName", "Automatic Test");
		//bindingProvider.getRequestContext().put("UserName", "TestUser");
		//bindingProvider.getRequestContext().put("OrganisationIdentification", "test-org");
	}
	
	private SSLConnectionSocketFactory getCustomSocketFactory() throws Throwable {
		
		char[] pw = "Lcmanager02".toCharArray();
		
		KeyStore keyStore = KeyStore.getInstance("pcks12");
		try (final InputStream is = new FileInputStream("/etc/ssl/certs/test-org.jks")) {
			keyStore.load(is, "Lcmanager02".toCharArray());
		}
		
		KeyStore trustStore = KeyStore.getInstance("jks");
		try (final InputStream is = new FileInputStream("/etc/ssl/certs/trust.jks")) {
			trustStore.load(is, "Lcmanager02".toCharArray());
		}

		//Put keystores in SSLContext
		final SSLContext sslContext = new SSLContextBuilder()
				.loadKeyMaterial(keyStore, pw )
				.loadTrustMaterial(trustStore)
				.build();
		
		//Put SSLContext in SSLConnectionSocketFactory and add to the builder
		final SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslContext);
		
		return sslConnectionFactory.getSocketFactory();
	}
}
