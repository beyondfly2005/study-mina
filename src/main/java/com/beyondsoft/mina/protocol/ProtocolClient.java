package com.beyondsoft.mina.protocol;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class ProtocolClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 7080;

    static long conuter = 0;

    final static int fil = 100;

    static long start = 0;

    public static void main(String[] args) {
        start = System.currentTimeMillis();
        IoConnector connector = new NioSocketConnector();
        connector.getFilterChain().addLast("coderc", new ProtocolCodecFilter(new ProtocolFactory(Charset.forName("UTF-8"))));
        connector.getSessionConfig().setReadBufferSize(1024);
        connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        connector.setHandler(new MyHandler());
        ConnectFuture connectFuture = connector.connect(new InetSocketAddress(HOST, PORT));
        connectFuture.addListener(new IoFutureListener<ConnectFuture>() {
            @Override
            public void operationComplete(ConnectFuture future) {
                if (future.isConnected()) {
                    IoSession session = future.getSession();
                    senddata(session);
                }
            }
        });
    }

    public static void senddata(IoSession session) {
        for (int i = 0; i < fil; i++) {
            String content = "watchmen:" + i;
            ProtocolPack pack = new ProtocolPack((byte) i, content);
            session.write(pack);
            System.out.println("客户端发送数据：" + pack);
        }
    }

    //内部类
    static class MyHandler extends IoHandlerAdapter {
        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            ProtocolPack pack = (ProtocolPack) message;
            System.out.println("client->" + pack);
        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
            if (status == IdleStatus.READER_IDLE) {
                session.close(true);
//                session.closeNow();
                System.out.println("客户端连接关闭");
            }
        }
    }
}


