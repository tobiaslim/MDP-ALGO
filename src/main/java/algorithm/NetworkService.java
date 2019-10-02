package algorithm;

import algorithm.constants.RobotAction;
import com.fasterxml.jackson.databind.ObjectMapper;
import networkmanager.NetworkManager;
import networkmanager.NetworkRecipient;
import networkmanager.NetworkSubscriber;
import networkmanager.dto.ActionDataPacket;
import networkmanager.dto.ControlSignalPacket;
import networkmanager.dto.Packet;
import networkmanager.dto.SensorInfoPacket;

public class NetworkService implements NetworkSubscriber {
    private NetworkManager networkManager;
    private ObjectMapper mapper;
    private AlgorithmManager algorithmManager;

    public NetworkService(NetworkManager networkManager, AlgorithmManager algorithmManager){
        mapper = new ObjectMapper();
        this.networkManager = networkManager;
        networkManager.subscribe(this);

        this.algorithmManager = algorithmManager;
    }

    public void sendPacket(Object dataToSend, NetworkRecipient recipient){
        Packet packet = new Packet();
        packet.setSender(NetworkRecipient.ALGORITHM.toString());
        packet.setData(dataToSend);
        packet.setRecipient(recipient.toString());
        networkManager.sendPacket(packet);
    }

    public void sendActionToArduino(RobotAction action){
        ActionDataPacket data = new ActionDataPacket();
        data.setAction(action);
        sendPacket(data, NetworkRecipient.ARDUINO);
    }

    @Override
    public void onPacket(Packet packet) {
        try{
            SensorInfoPacket sensorInfoPacket = mapper.convertValue(packet.getData(), SensorInfoPacket.class);
            algorithmManager.onSensorsInformation(sensorInfoPacket);
            return;
        }
        catch (IllegalArgumentException e){
            System.out.println("Drop packet for sensor info");
        }
        try{
            ControlSignalPacket signalPacket = mapper.convertValue(packet.getData(), ControlSignalPacket.class);
            algorithmManager.onControlSignal(signalPacket);
            return;
        }
        catch (IllegalArgumentException e){
            System.out.println("Drop packet for robot signal");
        }
    }
}
