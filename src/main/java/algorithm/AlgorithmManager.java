package algorithm;

import algorithm.constants.RobotAction;
import algorithm.contracts.RobotSubscriber;
import algorithm.models.MDFFormat;
import algorithm.models.RobotModel;
import networkmanager.NetworkManager;
import networkmanager.NetworkRecipient;
import networkmanager.dto.ControlSignalPacket;
import networkmanager.dto.SensorInfoPacket;
import simulator.Mode;

import java.util.HashMap;
import java.util.Map;

public class AlgorithmManager implements RobotSubscriber {
    private ArenaMemory exploredArenaMemory;
    private Mode mode;
    private RobotModel robotModel;
    private ExplorationAlgorithm explorationAlgorithm;
    private NetworkService networkService;
    private Thread currentExecutingAlgorithm;


    public AlgorithmManager(Mode mode, NetworkManager networkManager){
        this.mode = mode;
        this.networkService = new NetworkService(networkManager, this);
        this.exploredArenaMemory = new ArenaMemory();
        this.robotModel = new RobotModel(networkService, exploredArenaMemory, mode);
        exploredArenaMemory.setStartZoneAndGoalZone();
        this.explorationAlgorithm = new ExplorationAlgorithm(robotModel, exploredArenaMemory);
        robotModel.subscribe(this);
    }

    public void startAlgo(){
        currentExecutingAlgorithm = new Thread(this.explorationAlgorithm);
        currentExecutingAlgorithm.setName("Algorithm Runnable");
        currentExecutingAlgorithm.start();
    }

    public void pauseAlgo(){
        explorationAlgorithm.pauseAlgorithm();
    }

    public void resumeAlgo(){
        explorationAlgorithm.resumeAlgorithm();
    }

    public ArenaMemory getExploredArenaMemory() {
        return exploredArenaMemory;
    }

    public RobotModel getRobotModel(){
        return this.robotModel;
    }

    @Override
    public void onMove() {
        RobotAction action = robotModel.getLastAction();
        networkService.sendActionToArduino(action);
        Map<String, Object> mapAndPos = new HashMap<>();
        mapAndPos.put("direction", robotModel.getCurrentDirection());
        mapAndPos.put("pos", robotModel.getRobotCenter());
        mapAndPos.put("mdf", new MDFFormat(exploredArenaMemory));
        networkService.sendPacket(mapAndPos, NetworkRecipient.ANDROID);
    }

    public void onSensorsInformation(SensorInfoPacket sensorInfoPacket){
        robotModel.updateMap(sensorInfoPacket);
    }

    public void onControlSignal(ControlSignalPacket controlSignalPacket){
        if(controlSignalPacket.getAction() == ControlSignalPacket.RobotSignal.START_EXPLORE){
            startAlgo();
        }
        else{
//            System.out.println("unknown signal");
        }
    }
}
