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
import networkmanager.dto.FastestPathList;
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

//        use this to do calibration of sensor
//        Runnable r = new Runnable() {
//            @Override
//            public void run() {
//                while(true){
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    networkService.sendActionToArduino(RobotAction.START);
//                    robotModel.waitForReadyState();
//                }
//            }
//        };
//        Thread t = new Thread(r);
//        t.start();
    }

    public void startFastestPathAlgorithm(){
        exploredArenaMemory.redrawVirtual();
        algoThread = new Thread(new FastestPathAlgorithm(robotModel, exploredArenaMemory, waypoint));
        algoThread.setName("fastest path runnable");
        algoThread.start();
        try {
            algoThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*
            Access robotModel's fastest path string
         */
        FastestPathList fpl = new FastestPathList();
        fpl.setActions(robotModel.getMininalFPString());
        networkService.sendPacket(fpl, NetworkRecipient.ARDUINO);
        // To be changed
        System.out.println(robotModel.getMininalFPString());
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
        exploredArenaMemory.setWaypoint(new ArenaCellCoordinate(wayPointPacket.getX(), wayPointPacket.getY()));
        this.waypoint = new ArenaCellCoordinate(wayPointPacket.getX(), wayPointPacket.getY());
        System.out.printf("New waypoint setted x:%d y:%d \n", wayPointPacket.getX(), wayPointPacket.getY() );
    }
}