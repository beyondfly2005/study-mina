
package com.beyondsoft.mina.protocal;

import java.nio.charset.Charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;

/**
 * @author
 * @date 创建时间：2018年10月12日 下午2:41:35
 * @Description 编解码工厂
 */
public class ProtocolFactory implements ProtocolCodecFactory {

    private final ProtocolDecoder decoder;
    private final ProtocolEncoder encoder;

    public ProtocolFactory(Charset charset) {
        encoder = new ProtocolEncoder(charset);
        decoder = new ProtocolDecoder(charset);
    }

    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return encoder;
    }

    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return decoder;
    }
}