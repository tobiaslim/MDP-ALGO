package networkmanager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import networkmanager.dto.Packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NetworkManager {
    private NetworkConnection networkConnection;
    private ObjectMapper objectMapper = new ObjectMapper();
    private List<NetworkSubscriber> subscriberList;
    private String serverIP;


    public NetworkManager(String serverIP, int port) throws IOException {
        this.serverIP = serverIP;
        subscriberList = new ArrayList<>();
            System.out.printf("Connecting to %s at %d\n", serverIP, port);
            networkConnection = new NetworkConnection(serverIP, port);
            networkConnection.listen(this);
    }

    public void subscribe(NetworkSubscriber subscriber){
        subscriberList.add(subscriber);
    }

    public void sendWtf(){
        networkConnection.send("wtf");
    }

    public void sendPacket(Packet packet) {
        try {
            networkConnection.send(objectMapper.writeValueAsString(packet));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    public void onMessage(String message) {
        if(serverIP.equals("192.168.25.25")){
            System.out.println("Client: Message received from server: " + message);
        }
        try {
            Packet packet = objectMapper.readValue(message, Packet.class);
            for (NetworkSubscriber subscriber: subscriberList) {
                subscriber.onPacket(packet);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }


    public void onError(Exception e){
        System.out.println("Error Occured: ");
        e.printStackTrace();
    }


}
