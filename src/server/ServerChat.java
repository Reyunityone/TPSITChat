package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import Client.ClientHandlerThread;

public class ServerChat {
    private static final int PORT_NUMBER = 5000;
    private List<ClientHandlerThread> clients = new ArrayList<>();
    private boolean running = true;
    private DBHandler handler = new DBHandler();
    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
            System.out.println("Server listening on port " + PORT_NUMBER);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                ClientHandlerThread clientThread = new ClientHandlerThread(clientSocket, this);
                clients.add(clientThread);
                clientThread.start();
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer(){
        this.running = false;
    }

    public ArrayList<Chat> readChats(ChatRequest request) throws Exception{
        return handler.readChats();
    }

}
