package algorithm;

import algorithm.constants.RobotAction;
import algorithm.contracts.AlgorithmContract;
import algorithm.contracts.RobotSubscriber;
import algorithm.models.ArenaCellCoordinate;
import algorithm.models.MDFFormat;
import algorithm.models.RobotModel;
import networkmanager.NetworkManager;
import networkmanager.NetworkRecipient;
import networkmanager.dto.ControlSignalPacket;
import networkmanager.dto.SensorInfoPacket;
import networkmanager.dto.WayPointPacket;
import simulator.Mode;

import java.util.HashMap;
import java.util.Map;

public class AlgorithmManager implements RobotSubscriber {
    private ArenaMemory exploredArenaMemory;
    private Mode mode;
    private RobotModel robotModel;
    private AlgorithmContract currentAlgo;
    private NetworkService networkService;
    private Thread algoThread;
    private ArenaCellCoordinate waypoint;


    public AlgorithmManager(Mode mode, NetworkManager networkManager){
        this.mode = mode;
        this.networkService = new NetworkService(networkManager, this);
        this.exploredArenaMemory = new ArenaMemory();
        this.robotModel = new RobotModel(networkService, exploredArenaMemory, mode);
        exploredArenaMemory.setStartZoneAndGoalZone();
        robotModel.subscribe(this);
    }

    public void startExplorationAlgorithm(){
        this.currentAlgo = new ExplorationAlgorithm(robotModel, exploredArenaMemory);
        exploredArenaMemory.setSurroundingAsVirtualWalls();
        algoThread = new Thread(currentAlgo);
        algoThread.setName("exploration runnable");
        algoThread.start();
    }

    public void startFastestPathAlgorithm(){
        waypoint = new ArenaCellCoordinate(4,15);
        algoThread = new Thread(new FastestPathAlgorithm(robotModel, exploredArenaMemory, waypoint));
        algoThread.setName("fastest path runnable");
        algoThread.start();
    }

    public void pauseAlgo(){
        currentAlgo.pauseAlgorithm();
    }

    public void resumeAlgo(){
        currentAlgo.resumeAlgorithm();
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
            startExplorationAlgorithm();
        }
        else{
            startFastestPathAlgorithm();
        }
    }

    public void onWaypoint(WayPointPacket wayPointPacket){
        this.waypoint = new ArenaCellCoordinate(wayPointPacket.getX(), wayPointPacket.getY());
    }
}