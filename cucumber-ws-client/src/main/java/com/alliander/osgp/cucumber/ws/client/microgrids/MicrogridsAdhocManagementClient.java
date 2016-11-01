package com.alliander.osgp.cucumber.ws.client.microgrids;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.ws.BindingProvider;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.definitions.osgp.microgrids.adhocmanagement_v1_0.MicrogridsAdHocManagementPort;
import com.alliander.definitions.osgp.microgrids.adhocmanagement_v1_0.MicrogridsAdHocManagementPortService;
import com.alliander.schemas.osgp.microgrids.adhocmanagement._2016._06.GetDataAsyncResponse;
import com.alliander.schemas.osgp.microgrids.adhocmanagement._2016._06.GetDataRequest;
import com.alliander.schemas.osgp.microgrids.adhocmanagement._2016._06.MeasurementFilter;
import com.alliander.schemas.osgp.microgrids.adhocmanagement._2016._06.SystemFilter;

public class MicrogridsAdhocManagementClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MicrogridsAdhocManagementClient.class);

    private static final String KEYSTORE_PATH = "<<<KEYSTORE_PATH>>>";
    private static final String KEYSTORE_PW = "<<<KEYSTORE_PW>>>";

    private static final String TRUSTSTORE_PATH = "<<<TRUSTSTORE_PATH";
    private static final String TRUSTSTORE_PW = "<<<TRUSTSTORE_PW>>>";

    private static final String ENDPOINT_URL = "https://localhost/osgp-adapter-ws-microgrids/microgrids/adHocManagementService/";

    public static void main(final String[] args) {

        final MicrogridsAdHocManagementPort service = new MicrogridsAdHocManagementPortService()
                .getMicrogridsAdHocManagementPortSoap11();

        final MicrogridsAdhocManagementClient client = new MicrogridsAdhocManagementClient();
        client.setBindings(service);
        client.setupTLS(ClientProxy.getClient(service));

        final GetDataAsyncResponse asyncResponse = service.getData(createGetDataRequest());

        LOGGER.info("CorrelationUID: {}", asyncResponse.getAsyncResponse().getCorrelationUid());

    }

    private static GetDataRequest createGetDataRequest() {
        final MeasurementFilter measurementFilter = new MeasurementFilter();
        measurementFilter.setNode("Health");

        final SystemFilter systemFilter = new SystemFilter();
        systemFilter.setId(1);
        systemFilter.setType("RTU");
        systemFilter.getMeasurementFilter().add(measurementFilter);

        final GetDataRequest request = new GetDataRequest();
        request.setDeviceIdentification("RTU00002");
        request.getSystem().add(systemFilter);

        return request;
    }

    private void setBindings(final MicrogridsAdHocManagementPort port) {
        final BindingProvider bp = (BindingProvider) port;

        // bp.getRequestContext().put("com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory",
        // createSocketFactory());

        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, ENDPOINT_URL);

        bp.getRequestContext().put("ApplicationName", "SoapUI");
        bp.getRequestContext().put("UserName", "Tester");
        bp.getRequestContext().put("OrganisationIdentification", "ZownStream");
    }

    private void setupTLS(final Client client) {
        final HTTPConduit httpConduit = (HTTPConduit) client.getConduit();

        final TLSClientParameters tlsCP = new TLSClientParameters();
        final KeyManager[] myKeyManagers = this.getKeyManagers();
        final TrustManager[] myTrustStoreKeyManagers = this.getTrustManagers();

        tlsCP.setDisableCNCheck(true);
        tlsCP.setKeyManagers(myKeyManagers);
        tlsCP.setTrustManagers(myTrustStoreKeyManagers);
        // tlsCP.setUseHttpsURLConnectionDefaultHostnameVerifier(false);
        // tlsCP.setUseHttpsURLConnectionDefaultSslSocketFactory(false);
        // tlsCP.setSSLSocketFactory(this.createSocketFactory());

        httpConduit.setTlsClientParameters(tlsCP);

    }

    private SSLSocketFactory createSocketFactory() {
        SSLContext sc;
        try {
            sc = SSLContext.getInstance("TLSv1");

            sc.init(this.getKeyManagers(), this.getTrustManagers(), null);

            return sc.getSocketFactory();

        } catch (final Exception e) {
            // TODO Refactor
            e.printStackTrace();
            return null;
        }

    }

    private KeyManager[] getKeyManagers() {
        try (FileInputStream ksFile = new FileInputStream(KEYSTORE_PATH);) {
            final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            final KeyStore ks = KeyStore.getInstance("pkcs12");
            ks.load(ksFile, KEYSTORE_PW.toCharArray());
            kmf.init(ks, KEYSTORE_PW.toCharArray());
            return kmf.getKeyManagers();
        } catch (final Exception e) {
            return null;
        }

    }

    private TrustManager[] getTrustManagers() {

        // final TrustManager[] trustAllCerts = new TrustManager[] { new
        // NaiveTrustManager() };
        // return trustAllCerts;

        try (FileInputStream tsFile = new FileInputStream(TRUSTSTORE_PATH)) {

            final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            final KeyStore ts = KeyStore.getInstance("pkcs12");
            ts.load(tsFile, TRUSTSTORE_PW.toCharArray());
            tmf.init(ts);
            return tmf.getTrustManagers();
        } catch (final Exception e) {
            return null;
        }
    }
}
