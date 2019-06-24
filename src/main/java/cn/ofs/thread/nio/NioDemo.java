package cn.ofs.thread.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class NioDemo {

    private Selector selector;
    private static final String LOCLA_CHARSET = "UTF-8";

    public void initServer(int port) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(port));

        this.selector = Selector.open();

        serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        System.out.println("服务器启动成功: " + port);
    }

    public void clientListener() throws IOException {

        while (true) {

            this.selector.select();

            Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                handler(selectionKey);
            }
        }
    }

    public void handler(SelectionKey selectionKey) throws IOException {

        if (selectionKey.isAcceptable()) {

            ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
            SocketChannel socketChannel = channel.accept();
            socketChannel.configureBlocking(false);

            socketChannel.register(this.selector, SelectionKey.OP_READ);

        } else if (selectionKey.isReadable()) {

            SocketChannel channel = (SocketChannel) selectionKey.channel();
            // 创建读取的缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);//1kb
            int readData = channel.read(buffer);
            if (readData > 0) {
                String msg = new String(buffer.array(), "GBK").trim();// 先讲缓冲区数据转化成byte数组,再转化成String
                System.out.println("服务端收到信息：" + msg);

                //回写数据
                ByteBuffer writeBackBuffer = ByteBuffer.wrap(("服务端收到信息：" + msg).getBytes(LOCLA_CHARSET));
                channel.write(writeBackBuffer);// 将消息回送给客户端
            } else {
                System.out.println("客户端关闭咯...");
                //SelectionKey对象会失效，这意味着Selector再也不会监控与它相关的事件
                selectionKey.cancel();
            }
        }
    }

    public static void main(String[] args) throws IOException {

        NioDemo nioDemo = new NioDemo();
        nioDemo.initServer(8088);
        nioDemo.clientListener();
    }
}
