package com.beyondsoft.mina.protocal;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 * @author
 * @date 创建时间：2018年10月12日 下午2:49:24
 * @Description 服务端实例
 */
public class ProtocolServer {

    private static final int port = 7080;

    public static void main(String[] args) throws IOException {

        IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("coderc", new ProtocolCodecFilter(
                new ProtocolFactory(Charset.forName("UTF-8"))));//设置编解码器
        acceptor.getSessionConfig().setReadBufferSize(1024);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        acceptor.setHandler(new MyHandler());
        acceptor.bind(new InetSocketAddress(port));
        System.out.println("server start......");
    }

    static class MyHandler extends IoHandlerAdapter {

        @Override
        public void sessionIdle(IoSession session, IdleStatus status)
                throws Exception {
            System.out.println("server->sessionIdle");
        }

        @Override
        public void exceptionCaught(IoSession session, Throwable cause)
                throws Exception {
            System.out.println("server->exceptionCaught");
        }

        @Override
        public void messageReceived(IoSession session, Object message)
                throws Exception {
            ProtocolPack pack = (ProtocolPack) message;
            System.out.println("服务端接收: " + pack);
        }
    }
}
