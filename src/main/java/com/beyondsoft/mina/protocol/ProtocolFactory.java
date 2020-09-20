package com.beyondsoft.mina.protocol;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;

import java.nio.charset.Charset;

public class ProtocolFactory implements ProtocolCodecFactory {

    private final ProtocolDecoder decoder;
    private final ProtocolEncoder encoder;

    public ProtocolFactory(Charset charset){
        encoder = new ProtocolEncoder(charset);
        decoder = new ProtocolDecoder(charset);
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return decoder;
    }
}
