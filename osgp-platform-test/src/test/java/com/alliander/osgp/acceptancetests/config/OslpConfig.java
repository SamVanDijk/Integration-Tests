/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.config;

import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import com.alliander.osgp.acceptancetests.OslpTestUtils;
import com.alliander.osgp.adapter.protocol.oslp.application.config.QualifierProtocolOslp;
import com.alliander.osgp.adapter.protocol.oslp.application.mapping.OslpMapper;
import com.alliander.osgp.adapter.protocol.oslp.device.FirmwareLocation;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpChannelHandlerClient;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpChannelHandlerServer;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpSecurityHandler;
import com.alliander.osgp.oslp.OslpDecoder;
import com.alliander.osgp.oslp.OslpEncoder;
import com.alliander.osgp.oslp.OslpUtils;
import com.alliander.osgp.shared.application.config.PagingSettings;
import com.alliander.osgp.shared.security.CertificateHelper;

public class OslpConfig {
    private static final int OSLP_TIMEOUT_CONNECT = 3000;
    private static final int OSLP_PORT_CLIENT = 12121;
    private static final int OSLP_PORT_CLIENTLOCAL = 12123;
    private static final int OSLP_PORT_SERVER = 12121;

    private static final int PAGING_MAXIMUM_PAGE_SIZE = 30;
    private static final int PAGING_DEFAULT_PAGE_SIZE = 15;

    private static final String FIRMWARE_DOMAIN = "flexovltest.cloudapp.net";
    private static final String FIRMWARE_PATH = "firmware";
    private static final String FIRMWARE_FILE_EXTENSION = "zip";

    @Bean
    @QualifierProtocolOslp
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public FirmwareLocation firmwareLocation() {
        return new FirmwareLocation(FIRMWARE_DOMAIN, FIRMWARE_PATH, FIRMWARE_FILE_EXTENSION);
    }

    @Bean
    @QualifierProtocolOslp
    public OslpDecoder oslpDecoder() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException,
            NoSuchProviderException {
        return new OslpDecoder(this.oslpSignature(), this.oslpSignatureProvider());
    }

    @Bean
    @QualifierProtocolOslp
    public PublicKey publicKey() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException,
            NoSuchProviderException {
        return CertificateHelper.createPublicKeyFromBase64(OslpTestUtils.PUBLIC_KEY_BASE_64, OslpTestUtils.KEY_TYPE,
                OslpTestUtils.provider());
    }

    @Bean
    @Qualifier("signingServerPrivateKey")
    @QualifierProtocolOslp
    public PrivateKey privateKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException,
            NoSuchProviderException {
        return CertificateHelper.createPrivateKeyFromBase64(OslpTestUtils.PRIVATE_KEY_BASE_64, OslpTestUtils.KEY_TYPE,
                OslpTestUtils.provider());
    }

    @Bean
    @Qualifier("signingServerSignatureProvider")
    @QualifierProtocolOslp
    public String oslpSignatureProvider() {
        return OslpTestUtils.provider();
    }

    @Bean
    @QualifierProtocolOslp
    public Integer sequenceNumberWindow() {
        return OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW;
    }

    @Bean
    @QualifierProtocolOslp
    public Integer sequenceNumberMaximum() {
        return OslpTestUtils.OSLP_SEQUENCE_NUMBER_MAXIMUM;
    }

    @Bean
    @Qualifier("signingServerSignature")
    @QualifierProtocolOslp
    public String oslpSignature() {
        return OslpTestUtils.SIGNATURE;
    }

    @Bean
    @Qualifier("signingServerKeyType")
    @QualifierProtocolOslp
    public String oslpKeyType() {
        return OslpTestUtils.KEY_TYPE;
    }

    @Bean
    @QualifierProtocolOslp
    public int connectionTimeout() {
        return OSLP_TIMEOUT_CONNECT;
    }

    @Bean
    @QualifierProtocolOslp
    public int oslpPortClient() {
        return OSLP_PORT_CLIENT;
    }

    @Bean
    @QualifierProtocolOslp
    public int oslpPortClientLocal() {
        return OSLP_PORT_CLIENTLOCAL;
    }

    @Bean
    @QualifierProtocolOslp
    public int oslpPortServer() {
        return OSLP_PORT_SERVER;
    }

    @Bean
    @QualifierProtocolOslp
    public Channel channelMock() {
        return mock(Channel.class);
    }

    @Bean
    @QualifierProtocolOslp
    public OslpUtils oslpUtils() {
        return new OslpUtils();
    }

    @Bean(destroyMethod = "releaseExternalResources")
    @QualifierProtocolOslp
    public ClientBootstrap clientBootstrap() {
        final ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        final ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException,
                    NoSuchProviderException {
                final ChannelPipeline pipeline = Channels.pipeline();

                pipeline.addLast("oslpEncoder", new OslpEncoder());
                pipeline.addLast("oslpDecoder",
                        new OslpDecoder(OslpConfig.this.oslpSignature(), OslpConfig.this.oslpSignatureProvider()));
                pipeline.addLast("oslpSecurity", OslpConfig.this.oslpSecurityHandler());

                pipeline.addLast("oslpChannelHandler", OslpConfig.this.oslpChannelHandlerClient());

                return pipeline;
            }
        };

        final ClientBootstrap bootstrap = new ClientBootstrap(factory);

        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", false);
        bootstrap.setOption("connectTimeoutMillis", this.connectionTimeout());

        bootstrap.setPipelineFactory(pipelineFactory);

        return bootstrap;
    }

    @Bean(destroyMethod = "releaseExternalResources")
    @QualifierProtocolOslp
    public ServerBootstrap serverBootstrap() {
        final ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        final ServerBootstrap bootstrap = new ServerBootstrap(factory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException,
                    NoSuchProviderException {
                final ChannelPipeline pipeline = Channels.pipeline();

                pipeline.addLast("oslpEncoder", new OslpEncoder());
                pipeline.addLast("oslpDecoder",
                        new OslpDecoder(OslpConfig.this.oslpSignature(), OslpConfig.this.oslpSignatureProvider()));
                pipeline.addLast("oslpSecurity", OslpConfig.this.oslpSecurityHandler());

                pipeline.addLast("oslpChannelHandler", OslpConfig.this.oslpChannelHandlerServer());

                return pipeline;
            }
        });

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", false);

        return bootstrap;
    }

    @Bean
    @QualifierProtocolOslp
    public OslpSecurityHandler oslpSecurityHandler() {
        return new OslpSecurityHandler();
    }

    @Bean
//    @Qualifier("protocolOslpOslpChannelHandlerClient")
    // @QualifierProtocolOslp
    public OslpChannelHandlerClient oslpChannelHandlerClient() {
        return new OslpChannelHandlerClient();
    }

    @Bean
    @QualifierProtocolOslp
    public OslpChannelHandlerServer oslpChannelHandlerServer() {
        return new OslpChannelHandlerServer();
    }

    @Bean
    @QualifierProtocolOslp
    public ChannelHandlerContext channelHandlerContextMock() {
        return mock(ChannelHandlerContext.class);
    }

    @Bean
    @QualifierProtocolOslp
    public MessageEvent messageEvent() {
        return mock(MessageEvent.class);
    }

    @Bean
    @QualifierProtocolOslp
    public PagingSettings oslpPagingSettings() {
        final PagingSettings settings = new PagingSettings(PAGING_MAXIMUM_PAGE_SIZE, PAGING_DEFAULT_PAGE_SIZE);

        return settings;
    }

    @Bean
    @QualifierProtocolOslp
    public OslpMapper oslpMapper() {
        return new OslpMapper();
    }
}
