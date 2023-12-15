package server;

public class TestServer {
    public static void main(String[] args) throws ClassNotFoundException {
        ServerChat server = new ServerChat();
            Thread thread = new Thread(() -> server.startServer());
            thread.start();
    }
}
