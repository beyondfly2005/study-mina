package com.beyondsoft.mina.demo;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class MinaClient {

    private static String host="127.0.0.1";
    private static int port =7080;

    public static void main(String[] args) {
        IoConnector connector=new NioSocketConnector();
        connector.setConnectTimeout(3000);
        //设置过滤器
        connector.getFilterChain().addLast("codec",new ProtocolCodecFilter(
                new TextLineCodecFactory(
                        Charset.forName("UTF-8"),
                        LineDelimiter.WINDOWS.getValue(),
                        LineDelimiter.WINDOWS.getValue()
                )
        ));
        connector.setHandler(new MyClientHandler());
        ConnectFuture connect = connector.connect(new InetSocketAddress(host, port));
        connect.awaitUninterruptibly(); //等待我们的连接
        IoSession session = connect.getSession();
        session.write("你好,jerry");
        session.getCloseFuture().awaitUninterruptibly();//等待关闭连接
        connector.dispose();
    }
}
