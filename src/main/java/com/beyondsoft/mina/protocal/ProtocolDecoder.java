
package com.beyondsoft.mina.protocal;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * @author
 * @date 创建时间：2018年10月12日 下午1:56:45
 * @Description 解码器（将字节流转成对象）
 */
public class ProtocolDecoder implements org.apache.mina.filter.codec.ProtocolDecoder {

    private final AttributeKey CONTEXT = new AttributeKey(this.getClass(), "context");//设置上下文存储
    private final Charset charset;
    private int maxPackLength = 100;//设置最大长度，过滤

    public int getMaxPackLength() {
        return maxPackLength;
    }

    public void setMaxPackLength(int maxPackLength) {
        if (maxPackLength < 0) {
            throw new IllegalArgumentException("maxPackLength参数:" + maxPackLength);
        }
        this.maxPackLength = maxPackLength;
    }

    //默认
    public ProtocolDecoder() {
        this(Charset.defaultCharset());
    }

    public ProtocolDecoder(Charset charset) {
        this.charset = charset;
    }

    public Context getContext(IoSession session) {
        Context ctx = (Context) session.getAttribute(CONTEXT);
        if (ctx == null) {
            ctx = new Context();
            session.setAttribute(CONTEXT, ctx);
        }
        return ctx;
    }

    public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        final int packHeadlength = 5;//代表包头的长度
        Context ctx = this.getContext(session);
        ctx.append(in);
        IoBuffer buf = ctx.getBuf();
        buf.flip();//缓冲区的指针从0开始
        while (buf.remaining() >= packHeadlength) {
            buf.mark();
            int length = buf.getInt();
            byte flag = buf.get();
            if (length < 0 || length > maxPackLength) {
                buf.reset();
                break;
            } else if (length >= packHeadlength && length - packHeadlength <= buf.remaining()) {
                int oldLimit = buf.limit();
                buf.limit(buf.position() + length - packHeadlength);
                String content = buf.getString(ctx.getDecoder());
                buf.limit(oldLimit);
                ProtocolPack pakeage = new ProtocolPack(flag, content);
                out.write(pakeage);//发送数据包
            } else { //半包
                buf.clear();
                break;
            }
        }

        //读完是否还有数据
        if (buf.hasRemaining()) {
            IoBuffer temp = IoBuffer.allocate(maxPackLength).setAutoExpand(true);
            temp.put(buf);
            temp.flip();
            buf.reset();
            buf.put(temp);

        } else {
            buf.reset();//清空
        }
    }

    public void finishDecode(IoSession session, ProtocolDecoderOutput out)
            throws Exception {

    }

    public void dispose(IoSession session) throws Exception {
        Context ctx = (Context) session.getAttribute(CONTEXT);
        if (ctx != null) {
            session.removeAttribute(CONTEXT);
        }
    }

    private class Context { //上下文对象
        private final CharsetDecoder decoder;
        private IoBuffer buf;

        private Context() {
            decoder = charset.newDecoder();
            buf = IoBuffer.allocate(80).setAutoExpand(true);
        }

        public void append(IoBuffer in) {
            this.getBuf().put(in);
        }

        public void rest() {
            decoder.reset();
        }

        public IoBuffer getBuf() {
            return buf;
        }

        public void setBuf(IoBuffer buf) {
            this.buf = buf;
        }

        public CharsetDecoder getDecoder() {
            return decoder;
        }
    }
}