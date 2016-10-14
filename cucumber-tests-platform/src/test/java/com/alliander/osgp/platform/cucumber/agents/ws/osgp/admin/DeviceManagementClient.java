package com.alliander.osgp.platform.cucumber.agents.ws.osgp.admin;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.ws.BindingProvider;

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
		
	    //final SSLContext sslContext = (new SslContextFactory()).createSslContext("test-org");
         
		// Set the certificate
		bindingProvider.getRequestContext().put(
				"com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory", 
		//		//sslContext.getSocketFactory());
				getCustomSocketFactory());
		
		bindingProvider.getRequestContext().put("ApplicationName", "Automatic Test");
		bindingProvider.getRequestContext().put("UserName", "TestUser");
		bindingProvider.getRequestContext().put("OrganisationIdentification", "test-org");
	}
	
	private SSLSocketFactory getCustomSocketFactory() throws Throwable {
		
		/*String keyStoreType = "pkcs12";
		String keyStoreLocation = "/etc/ssl/certs";
		String keyStore = "test-org";
		String keyStorePassword = "1234";
		
        final KeyStoreFactoryBean keyStoreFactory = new KeyStoreFactoryBean();
        keyStoreFactory.setType(keyStoreType);
        keyStoreFactory.setLocation(new FileSystemResource(keyStoreLocation + "/" + keyStore + ".pfx"));
        keyStoreFactory.setPassword(keyStorePassword);
        keyStoreFactory.afterPropertiesSet();

        final KeyStore ks = keyStoreFactory.getObject();
        if (keyStore == null || ks.size() == 0) {
            throw new KeyStoreException("Key store is empty");
        }

    	// SSL, SSLv2, SSLv3, TLS, TLSv1, TLSv1.1, TLSv1.2
    	SSLContext sc = SSLContext.getInstance("TLS");

    	KeyManagerFactory kmf =
        	    KeyManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );
        
    	kmf.init( ks, keyStorePassword.toCharArray() );

    	sc.init( kmf.getKeyManagers(), null, null );
    	
    	return sc.getSocketFactory();
    	*/
		
		/*SSLContext sslContext = SSLContext.getInstance("TLS");
		
		TrustManager[] trust_mgr = get_trust_mgr();
		sslContext.init(null, // key manager
                trust_mgr, // trust manager
                new SecureRandom()); // random number generator

		return sslContext.getSocketFactory();*/
		
		final KeyStore keyStore = KeyStore.getInstance("JKS");
		try (final InputStream is = new FileInputStream("/etc/ssl/certs/trust.jks")) {
			keyStore.load(is, "123456".toCharArray());
		}
		final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
				.getDefaultAlgorithm());
		kmf.init(keyStore, "1234".toCharArray());
		final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory
				.getDefaultAlgorithm());
		tmf.init(keyStore);
		
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new java.security.SecureRandom());
        
        return sslContext.getSocketFactory();
	}
	
	/*
    private SocketFactory getCustomSocketFactory() throws Throwable {
    	
    	// SSL, SSLv2, SSLv3, TLS, TLSv1, TLSv1.1, TLSv1.2
    	SSLContext sc = SSLContext.getInstance("TLS");
    	
    	String certPath = "/etc/ssl/certs/test-org.pfx";
    	String certPasswd = "1234";

    	KeyManagerFactory kmf =
    	    KeyManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );

    	// "jks", "pkcs12"
    	KeyStore ks = KeyStore.getInstance( "pkcs12" );
    	ks.load(new FileInputStream( certPath ), certPasswd.toCharArray() );

    	kmf.init( ks, certPasswd.toCharArray() );

    	sc.init( kmf.getKeyManagers(), null, null );
    	
    	return sc.getSocketFactory();
	} */
}
