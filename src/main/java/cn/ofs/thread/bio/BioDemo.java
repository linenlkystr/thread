package cn.ofs.thread.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BioDemo {

    public void NoThreadSocket() throws IOException {

        ServerSocket serverSocket = new ServerSocket(8066);

        while (true) {

            Socket socket = serverSocket.accept();
            System.out.println("有客户端连接：" + socket.getRemoteSocketAddress());

            InputStream inputStream = socket.getInputStream();
            byte[] b = new byte[1024];

            ReadData(inputStream, b);
        }
    }


    public void ThreadSocket() throws IOException {

        ServerSocket serverSocket = new ServerSocket(8067);

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("有客户端连接：" + socket.getRemoteSocketAddress());

            new Thread(() -> {
                InputStream inputStream = null;
                try {
                    inputStream = socket.getInputStream();

                    byte[] b = new byte[1024];
                    ReadData(inputStream, b);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }
    }

    public void ThreadPoolSocket() throws IOException {
        ExecutorService executorService = Executors.newCachedThreadPool();

        ServerSocket serverSocket = new ServerSocket(8868);

        while (true) {
            Socket socket = serverSocket.accept();

            executorService.execute(() -> {

                try {
                    InputStream inputStream = socket.getInputStream();
                    byte[] b = new byte[1024];
                    ReadData(inputStream, b);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void ReadData(InputStream inputStream, byte[] b) throws IOException {
        while (true) {
            int data = inputStream.read(b);
            if (data != -1) {
                String info = new String(b, 0, data, "GBK");
                System.out.println(info);
            } else {
                break;
            }
        }
    }

    public static void main(String[] args) throws IOException {

    }
}
