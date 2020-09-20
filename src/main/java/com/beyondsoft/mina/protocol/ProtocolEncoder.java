package com.beyondsoft.mina.protocol;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;

/**
 * 编码器
 */
public class ProtocolEncoder extends ProtocolEncoderAdapter {

    private final Charset charset;

    public ProtocolEncoder(Charset charset) {
        this.charset = charset;
    }

    @Override
    public void encode(IoSession session, Object object, ProtocolEncoderOutput out) throws Exception {
        ProtocolPack value = (ProtocolPack) object;
        IoBuffer buffer = IoBuffer.allocate(value.getLength());
        buffer.setAutoExpand(true); //自动增长
        buffer.putInt(value.getLength());
        buffer.put(value.getFlag());
        if (value.getContent() != null) {
            buffer.put(value.getContent().getBytes());
        }
        buffer.flip();
        out.write(buffer);//发送出去
    }
}
