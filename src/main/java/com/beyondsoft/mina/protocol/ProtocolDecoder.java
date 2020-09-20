package com.beyondsoft.mina.protocol;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * 解码器
 */
public class ProtocolDecoder implements org.apache.mina.filter.codec.ProtocolDecoder {

    private final AttributeKey CONTEXT = new AttributeKey(this.getClass(), "context");
    private final Charset charset;
    private int maxPackLength = 100;

    public int getMaxPackLength() {
        return maxPackLength;
    }

    public void setMaxPackLength(int maxPackLength) {
        if (maxPackLength < 0) {
            throw new IllegalArgumentException("maxPackLength参数：" + maxPackLength);
        }
        this.maxPackLength = maxPackLength;
    }

    public Context getConText(IoSession session) {
        Context ctx = (Context) session.getAttribute(CONTEXT);
        if (ctx == null) {
            ctx = new Context();
            session.setAttribute(CONTEXT, ctx);
        }
        return ctx;
    }

    public void addpend(IoBuffer in) {

    }

    public Charset getCharset() {
        return charset;
    }

    public ProtocolDecoder() {
        this(Charset.defaultCharset());
    }

    public ProtocolDecoder(Charset charset) {
        this.charset = charset;
    }

    @Override
    public void decode(IoSession session, IoBuffer buffer, ProtocolDecoderOutput out) throws Exception {
        final int packHeadLength=5;
        Context ctx= this.getConText(session);
        ctx.append(buffer);
        IoBuffer buf = ctx.getBuffer();
        buf.flip();
        while(buf.remaining()>=packHeadLength){
            buf.mark();
            int length = buf.getInt();
            byte flag =buf.get();
            if(length<0 || length>maxPackLength){
                buf.reset();
                break;
            } else if(length>packHeadLength && length-packHeadLength<=buf.remaining()){
                int oldLimit = buf.limit();
                buf.limit(buf.position()+length-packHeadLength);
                String content = buf.getString(ctx.getDecoder());
                buf.limit(oldLimit);
                ProtocolPack _package = new ProtocolPack(flag,content);
                out.write(_package);

            } else {
                //半包结构：数据包不完整，只读取了一部分，需要缓存起来下次再读
                buf.clear();
                break;
            }
        }
        if(buf.hasRemaining()){ //是否还有剩余数据
            IoBuffer temp =IoBuffer.allocate((maxPackLength)).setAutoExpand(true);
            temp.put(buf);
            temp.flip();
            buf.reset();
            buf.put(temp);
        } else {
            buf.reset();
        }
    }

    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {

    }

    @Override
    public void dispose(IoSession session) throws Exception {
        Context ctx = (Context) session.getAttribute(CONTEXT);
        if (ctx != null) {
            session.removeAttribute(CONTEXT);
        }
    }

    private class Context {
        private final CharsetDecoder decoder;
        private IoBuffer buffer;

        private Context() {
            decoder = charset.newDecoder();
            buffer = IoBuffer.allocate(80).setAutoExpand(true);
        }

        public void append(IoBuffer in) {
            this.getBuffer().put(in);
        }

        public void reset() {
            decoder.reset();
        }

        public CharsetDecoder getDecoder() {
            return decoder;
        }

        public IoBuffer getBuffer() {
            return buffer;
        }

        public void setBuffer(IoBuffer buffer) {
            this.buffer = buffer;
        }
    }
}
