package com.beyondsoft.mina.protocol;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class ProtocolServer {

    /** 端口 */
    private static final int PORT = 7080;

    public static void main(String[] args) throws IOException {
        IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("coderc", new ProtocolCodecFilter(new ProtocolFactory(Charset.forName("UTF-8"))));
        acceptor.getSessionConfig().setReadBufferSize(1024);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,10);
        acceptor.setHandler(new MyHandler());
        acceptor.bind(new InetSocketAddress(PORT));
        System.out.println("server start ......");
    }

    static class MyHandler extends IoHandlerAdapter {
        @Override
        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
            System.out.println("server -> exceptionCaught");
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            ProtocolPack pack = (ProtocolPack) message;
            System.out.println("服务端接收" + pack);
        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
            System.out.println("server -> session idler");
        }

        @Override
        public void sessionCreated(IoSession session) throws Exception {
            System.out.println("创建连接成功-----");;
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            System.out.println("打开连接成功-----");;
        }
    }
}

