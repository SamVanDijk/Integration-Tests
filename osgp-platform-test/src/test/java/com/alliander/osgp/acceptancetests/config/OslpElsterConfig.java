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
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.springframework.context.annotation.Bean;

import com.alliander.osgp.adapter.protocol.oslp.elster.application.config.QualifierProtocolOslpElster;
import com.alliander.osgp.adapter.protocol.oslp.elster.application.mapping.OslpMapper;
import com.alliander.osgp.adapter.protocol.oslp.elster.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.oslp.elster.infra.networking.OslpChannelHandlerClient;
import com.alliander.osgp.adapter.protocol.oslp.elster.infra.networking.OslpChannelHandlerServer;
import com.alliander.osgp.adapter.protocol.oslp.elster.infra.networking.OslpSecurityHandler;
import com.alliander.osgp.oslp.OslpDecoder;
import com.alliander.osgp.oslp.OslpEncoder;

public class OslpElsterConfig {
    private static final int OSLP_TIMEOUT_CONNECT = 3000;
    private static final int OSLP_PORT_CLIENT = 12122;
    private static final int OSLP_PORT_CLIENTLOCAL = 12124;
    private static final int OSLP_PORT_SERVER = 12122;

    private static final String OSLP_SECURITY_KEYTYPE = "";
    private static final String OSLP_SECURITY_SIGNATURE = "";
    private static final String OSLP_SECURITY_PROVIDER = "";

    @Bean(destroyMethod = "releaseExternalResources", name="protocolOslpElsterClientBootstrap")
    public ClientBootstrap clientBootstrap() {
        final ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        final ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException,
            NoSuchProviderException {
                final ChannelPipeline pipeline = Channels.pipeline();

                pipeline.addLast("oslpEncoder", new OslpEncoder());
                pipeline.addLast("oslpDecoder", new OslpDecoder(OslpElsterConfig.this.oslpSignature(),
                        OslpElsterConfig.this.oslpSignatureProvider()));
                pipeline.addLast("oslpSecurity", OslpElsterConfig.this.oslpSecurityHandler());

                pipeline.addLast("oslpChannelHandler", OslpElsterConfig.this.oslpChannelHandlerClient());

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
    @QualifierProtocolOslpElster
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
                pipeline.addLast("oslpDecoder", new OslpDecoder(OslpElsterConfig.this.oslpSignature(),
                        OslpElsterConfig.this.oslpSignatureProvider()));
                pipeline.addLast("oslpSecurity", OslpElsterConfig.this.oslpSecurityHandler());

                pipeline.addLast("oslpChannelHandler", OslpElsterConfig.this.oslpChannelHandlerServer());

                return pipeline;
            }
        });

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", false);

        return bootstrap;
    }

    @Bean
    @QualifierProtocolOslpElster
    public OslpSecurityHandler oslpSecurityHandler() {
        return new OslpSecurityHandler();
    }

    @Bean
    @QualifierProtocolOslpElster
    public OslpDecoder oslpDecoder() throws ProtocolAdapterException {
        return new OslpDecoder(this.oslpSignature(), this.oslpSignatureProvider());
    }

    @Bean
    @QualifierProtocolOslpElster
    public String oslpKeyType() {
        return OSLP_SECURITY_KEYTYPE;
    }

    @Bean
    @QualifierProtocolOslpElster
    public String oslpSignatureProvider() {
        return OSLP_SECURITY_PROVIDER;
    }

    @Bean
    @QualifierProtocolOslpElster
    public String oslpSignature() {
        return OSLP_SECURITY_SIGNATURE;
    }

    @Bean
    @QualifierProtocolOslpElster
    public int connectionTimeout() {
        return OSLP_TIMEOUT_CONNECT;
    }

    @Bean
    @QualifierProtocolOslpElster
    public int oslpPortClient() {
        return OSLP_PORT_CLIENT;
    }

    @Bean
    @QualifierProtocolOslpElster
    public int oslpPortClientLocal() {
        return OSLP_PORT_CLIENTLOCAL;
    }

    @Bean
    @QualifierProtocolOslpElster
    public int oslpPortServer() {
        return OSLP_PORT_SERVER;
    }

    @Bean
    @QualifierProtocolOslpElster
    public OslpChannelHandlerClient oslpChannelHandlerClient() {
        return new OslpChannelHandlerClient();
    }

    @Bean
    @QualifierProtocolOslpElster
    public OslpChannelHandlerServer oslpChannelHandlerServer() {
        return new OslpChannelHandlerServer();
    }

    @Bean
    @QualifierProtocolOslpElster
    public ChannelHandlerContext channelHandlerContextMock() {
        return mock(ChannelHandlerContext.class);
    }

    @Bean
    @QualifierProtocolOslpElster
    public OslpMapper oslpMapper() {
        return new OslpMapper();
    }
}
