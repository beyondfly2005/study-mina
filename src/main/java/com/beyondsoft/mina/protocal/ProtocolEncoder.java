package com.beyondsoft.mina.protocal;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * @author
 * @date 创建时间：2018年10月12日 下午1:38:04
 * @Description 编码器（将对象转成字节流）
 */
public class ProtocolEncoder extends ProtocolEncoderAdapter {

    private final Charset charset;//定义编码型

    public ProtocolEncoder(Charset charset) {
        this.charset = charset;
    }

    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        ProtocolPack value = (ProtocolPack) message;//报文信息
        IoBuffer buf = IoBuffer.allocate(value.getLength());//设置缓冲区
        buf.setAutoExpand(true);//自动增长
        buf.putInt(value.getLength());//设置包头
        buf.put(value.getFlag());
        if (value.getContent() != null) {//设置内容
            buf.put(value.getContent().getBytes());
        }
        buf.flip();
        out.write(buf);//发送出去
    }
}