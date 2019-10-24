package algorithm;

import algorithm.constants.RobotAction;
import algorithm.models.ArenaCellCoordinate;
import com.fasterxml.jackson.databind.ObjectMapper;
import networkmanager.NetworkManager;
import networkmanager.NetworkRecipient;
import networkmanager.NetworkSubscriber;
import networkmanager.dto.*;

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

    public void sendActionToArduino(RobotAction action, ArenaCellCoordinate photoGrid){
        ActionDataPacket data = new ActionDataPacket();
        data.setAction(action);
        data.setGrid(photoGrid);
        data.setPhoto(true);
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

        try{
            WayPointPacket wayPointPacket = mapper.convertValue(packet.getData(), WayPointPacket.class);
            algorithmManager.onWaypoint(wayPointPacket);
        }
        catch (IllegalArgumentException e){
            System.out.println("Drop packet for waypoint signal");
        }
    }

    public void sendStupid(){
        networkManager.sendWtf();
    }
}
