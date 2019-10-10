package networkmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkConnection {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private NetworkManager networkManager;

    private Thread listeningThread;

    public NetworkConnection(String serverIP, int port) throws IOException {
        this.clientSocket = new Socket(serverIP, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void send(String message){
        System.out.println("Client: Sending message: "+ message);
        out.println(message);
        out.flush();
    }

    public void listen(NetworkManager networkManager){
        this.networkManager = networkManager;
        Runnable listenRunnable = () -> {
            System.out.println("Client: Client listening on " + Thread.currentThread().getName()  + "...");
            String line;
            try{
                while (true) {
                    line = in.readLine();
                    networkManager.onMessage(line);
                }
            }catch (IOException e){
                networkManager.onError(e);
                e.printStackTrace();
            }
        };


        this.listeningThread = new Thread(listenRunnable);
        this.listeningThread.setName("Listening thread");
        this.listeningThread.start();
    }


}
