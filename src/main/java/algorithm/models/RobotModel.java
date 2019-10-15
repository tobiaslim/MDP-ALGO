package algorithm.models;

import algorithm.ArenaMemory;
import algorithm.NetworkService;
import algorithm.constants.*;
import algorithm.contracts.RobotSubscriber;
import networkmanager.dto.SensorInfoPacket;
import simulator.Mode;
import utility.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static algorithm.constants.SensorContants.*;


public class RobotModel{
    private Direction currentDirection;
    private ArenaCellCoordinate robotCenter;
    private List<RobotSubscriber> robotSubscriberList;
    private RobotAction lastAction;
    private ArenaMemory explored;
    private Map<RobotSensorPlacement, RobotSensor> sensors;
    private RobotState state;
    private RobotSensor virtualRightSensor;
    private Mode mode;
    private NetworkService networkService;

    public RobotModel(NetworkService networkService, ArenaMemory arenaMemory, Mode mode){
        this.mode = mode;
        this.networkService = networkService;
        robotSubscriberList = new ArrayList<>();
        robotCenter = new ArenaCellCoordinate(1, 1);
        currentDirection = Direction.SOUTH;
        this.explored = arenaMemory;
        setRobotState(RobotState.READY);
        sensors = new HashMap<>();

//        if(mode == Mode.SIMULATION) {
//            Map<Zone, SensorValueMapping> flv = new HashMap<>();
//            flv.put(Zone.A, new SensorValueMapping(Zone.A, SIMULATOR_FRONT_LEFT_A_START,SIMULATOR_FRONT_LEFT_A_END));
////            flv.put(Zone.B, new SensorValueMapping(Zone.B, SIMULATOR_FRONT_LEFT_B_START,SIMULATOR_FRONT_LEFT_B_END));
////            flv.put(Zone.C, new SensorValueMapping(Zone.C, SIMULATOR_FRONT_LEFT_C_START,SIMULATOR_FRONT_LEFT_C_END));
//            RobotSensor fl = new RobotSensor(flv, this, RobotSensorPlacement.FRONT_LEFT);
//            sensors.put(RobotSensorPlacement.FRONT_LEFT,fl);
//
//            Map<Zone,SensorValueMapping> fcv = new HashMap<>();
//            fcv.put(Zone.A, new SensorValueMapping(Zone.A, SIMULATOR_FRONT_CENTER_A_START ,SIMULATOR_FRONT_CENTER_A_END));
////            fcv.put(Zone.B, new SensorValueMapping(Zone.B, SIMULATOR_FRONT_CENTER_B_START ,SIMULATOR_FRONT_CENTER_B_END));
////            fcv.put(Zone.C, new SensorValueMapping(Zone.C, SIMULATOR_FRONT_CENTER_C_START,SIMULATOR_FRONT_CENTER_C_END));
//            RobotSensor fc = new RobotSensor(fcv, this, RobotSensorPlacement.FRONT_CENTER);
//            sensors.put(RobotSensorPlacement.FRONT_CENTER, fc);
//
//            Map<Zone,SensorValueMapping> frv = new HashMap<>();
//            frv.put(Zone.A, new SensorValueMapping(Zone.A, SIMULATOR_FRONT_RIGHT_A_START ,SIMULATOR_FRONT_RIGHT_A_END));
////            frv.put(Zone.B, new SensorValueMapping(Zone.B, SIMULATOR_FRONT_RIGHT_B_START ,SIMULATOR_FRONT_RIGHT_B_END));
////            frv.put(Zone.C, new SensorValueMapping(Zone.C, SIMULATOR_FRONT_RIGHT_C_START ,SIMULATOR_FRONT_RIGHT_C_END));
//            RobotSensor fr = new RobotSensor(frv, this, RobotSensorPlacement.FRONT_RIGHT);
//            sensors.put(RobotSensorPlacement.FRONT_RIGHT, fr);
//
//            Map<Zone,SensorValueMapping> rtv = new HashMap<>();
//            rtv.put(Zone.A, new SensorValueMapping(Zone.A, SIMULATOR_RIGHT_TOP_A_START ,SIMULATOR_RIGHT_TOP_A_END));
////            rtv.put(Zone.B, new SensorValueMapping(Zone.B, SIMULATOR_RIGHT_TOP_B_START ,SIMULATOR_RIGHT_TOP_B_END));
////            rtv.put(Zone.C, new SensorValueMapping(Zone.C, SIMULATOR_RIGHT_TOP_C_START ,SIMULATOR_RIGHT_TOP_C_END));
//            RobotSensor rt = new RobotSensor(rtv, this, RobotSensorPlacement.RIGHT_TOP);
//            sensors.put(RobotSensorPlacement.RIGHT_TOP, rt);
//
//            Map<Zone,SensorValueMapping> rbv = new HashMap<>();
//            rbv.put(Zone.A, new SensorValueMapping(Zone.A, SIMULATOR_RIGHT_BOTTOM_A_START ,SIMULATOR_RIGHT_BOTTOM_A_END));
////            rbv.put(Zone.B, new SensorValueMapping(Zone.B, SIMULATOR_RIGHT_BOTTOM_B_START ,SIMULATOR_RIGHT_BOTTOM_B_END));
////            rbv.put(Zone.C, new SensorValueMapping(Zone.C, SIMULATOR_RIGHT_BOTTOM_C_START ,SIMULATOR_RIGHT_BOTTOM_C_END));
//            RobotSensor rb = new RobotSensor(rbv, this, RobotSensorPlacement.RIGHT_BOTTOM);
//            sensors.put(RobotSensorPlacement.RIGHT_BOTTOM, rb);
//
//            Map<Zone,SensorValueMapping> lmv = new HashMap<>();
////            lmv.put(Zone.A, new SensorValueMapping(Zone.A, SIMULATOR_LEFT_MIDDLE_A_START ,SIMULATOR_LEFT_MIDDLE_A_END));
////            lmv.put(Zone.B, new SensorValueMapping(Zone.B, SIMULATOR_LEFT_MIDDLE_B_START ,SIMULATOR_LEFT_MIDDLE_B_END));
//            lmv.put(Zone.C, new SensorValueMapping(Zone.C, SIMULATOR_LEFT_MIDDLE_C_START ,SIMULATOR_LEFT_MIDDLE_C_END));
//            lmv.put(Zone.D, new SensorValueMapping(Zone.D, SIMULATOR_LEFT_MIDDLE_D_START ,SIMULATOR_LEFT_MIDDLE_D_END));
//            lmv.put(Zone.E, new SensorValueMapping(Zone.E, SIMULATOR_LEFT_MIDDLE_E_START ,SIMULATOR_LEFT_MIDDLE_E_END));
//            lmv.put(Zone.F, new SensorValueMapping(Zone.F, SIMULATOR_LEFT_MIDDLE_F_START ,SIMULATOR_LEFT_MIDDLE_F_END));
//            RobotSensor lm = new RobotSensor(lmv, this, RobotSensorPlacement.LEFT_MIDDLE);
//            sensors.put(RobotSensorPlacement.LEFT_MIDDLE, lm);
//
//        }
//        else{
            //TODO sensors declaration for actual run
            Map<Zone, SensorValueMapping> flv = new HashMap<>();
            flv.put(Zone.A, new SensorValueMapping(Zone.A, FRONT_LEFT_A_START,FRONT_LEFT_A_END));
//            flv.put(Zone.B, new SensorValueMapping(Zone.B, FRONT_LEFT_B_START,FRONT_LEFT_B_END));
//            flv.put(Zone.C, new SensorValueMapping(Zone.C, FRONT_LEFT_C_START,FRONT_LEFT_C_END));
            RobotSensor fl = new RobotSensor(flv, this, RobotSensorPlacement.FRONT_LEFT);
            sensors.put(RobotSensorPlacement.FRONT_LEFT,fl);

            Map<Zone,SensorValueMapping> fcv = new HashMap<>();
            fcv.put(Zone.A, new SensorValueMapping(Zone.A, FRONT_CENTER_A_START ,FRONT_CENTER_A_END));
//            fcv.put(Zone.B, new SensorValueMapping(Zone.B, FRONT_CENTER_B_START ,FRONT_CENTER_B_END));
//            fcv.put(Zone.C, new SensorValueMapping(Zone.C, FRONT_CENTER_C_START,FRONT_CENTER_C_END));
            RobotSensor fc = new RobotSensor(fcv, this, RobotSensorPlacement.FRONT_CENTER);
            sensors.put(RobotSensorPlacement.FRONT_CENTER, fc);

            Map<Zone,SensorValueMapping> frv = new HashMap<>();
            frv.put(Zone.A, new SensorValueMapping(Zone.A, FRONT_RIGHT_A_START ,FRONT_RIGHT_A_END));
//            frv.put(Zone.B, new SensorValueMapping(Zone.B, FRONT_RIGHT_B_START ,FRONT_RIGHT_B_END));
//            frv.put(Zone.C, new SensorValueMapping(Zone.C, FRONT_RIGHT_C_START ,FRONT_RIGHT_C_END));
            RobotSensor fr = new RobotSensor(frv, this, RobotSensorPlacement.FRONT_RIGHT);
            sensors.put(RobotSensorPlacement.FRONT_RIGHT, fr);

            Map<Zone,SensorValueMapping> rtv = new HashMap<>();
            rtv.put(Zone.A, new SensorValueMapping(Zone.A, RIGHT_TOP_A_START ,RIGHT_TOP_A_END));
//            rtv.put(Zone.B, new SensorValueMapping(Zone.B, RIGHT_TOP_B_START ,RIGHT_TOP_B_END));
//            rtv.put(Zone.C, new SensorValueMapping(Zone.C, RIGHT_TOP_C_START ,RIGHT_TOP_C_END));
            RobotSensor rt = new RobotSensor(rtv, this, RobotSensorPlacement.RIGHT_TOP);
            sensors.put(RobotSensorPlacement.RIGHT_TOP, rt);

            Map<Zone,SensorValueMapping> rbv = new HashMap<>();
            rbv.put(Zone.A, new SensorValueMapping(Zone.A, RIGHT_BOTTOM_A_START ,RIGHT_BOTTOM_A_END));
//            rbv.put(Zone.B, new SensorValueMapping(Zone.B, RIGHT_BOTTOM_B_START ,RIGHT_BOTTOM_B_END));
//            rbv.put(Zone.C, new SensorValueMapping(Zone.C, RIGHT_BOTTOM_C_START ,RIGHT_BOTTOM_C_END));
            RobotSensor rb = new RobotSensor(rbv, this, RobotSensorPlacement.RIGHT_BOTTOM);
            sensors.put(RobotSensorPlacement.RIGHT_BOTTOM, rb);

            Map<Zone,SensorValueMapping> lmv = new HashMap<>();
//            lmv.put(Zone.A, new SensorValueMapping(Zone.A, LEFT_MIDDLE_A_START ,LEFT_MIDDLE_A_END));
//            lmv.put(Zone.B, new SensorValueMapping(Zone.B, LEFT_MIDDLE_B_START ,LEFT_MIDDLE_B_END));
            lmv.put(Zone.C, new SensorValueMapping(Zone.C, LEFT_MIDDLE_C_START ,LEFT_MIDDLE_C_END));
            lmv.put(Zone.D, new SensorValueMapping(Zone.D, LEFT_MIDDLE_D_START ,LEFT_MIDDLE_D_END));
            lmv.put(Zone.E, new SensorValueMapping(Zone.E, LEFT_MIDDLE_E_START ,LEFT_MIDDLE_E_END));
//            lmv.put(Zone.F, new SensorValueMapping(Zone.F, LEFT_MIDDLE_F_START ,LEFT_MIDDLE_F_END));
            RobotSensor lm = new RobotSensor(lmv, this, RobotSensorPlacement.LEFT_MIDDLE);
            sensors.put(RobotSensorPlacement.LEFT_MIDDLE, lm);

//        }

        virtualRightSensor = new RobotSensor(this, RobotSensorPlacement.RIGHT_MIDDLE, 0, 0,0,0);
    }

    public void subscribe(RobotSubscriber subscriber){
        this.robotSubscriberList.add(subscriber);
    }

    public void startRobot(){
        explored.setStartZoneAsExplored();
        this.setLastAction(RobotAction.START);
        moveDone();
    }

    public void moveFrontOneStep(){
        switch (currentDirection){
            case NORTH:
                robotCenter.setY(robotCenter.getY()+1);
                break;
            case EAST:
                robotCenter.setX(robotCenter.getX()+1);
                break;
            case SOUTH:
                robotCenter.setY(robotCenter.getY()-1);
                break;
            case WEST:
                robotCenter.setX(robotCenter.getX()-1);
        }
        setLastAction(RobotAction.MOVE_STRAIGHT);
        moveDone();
    }

    /**
     * Update the state of the robot to turn left. Calls moveDone() and effect() to update SIM and real world robot
     */
    public void turnLeft(){
        int i = (currentDirection.getValue() - 1) % 4;
        if(i < 0) i += 4;
        currentDirection = Direction.value(i);
        setLastAction(RobotAction.TURN_LEFT);
        moveDone();
    }


    /**
     * Update the state of the robot to turn right. Calls moveDone() and effect() to update SIM and real world robot
     */
    public void turnRight(){
        int i = (currentDirection.getValue() + 1) % 4;
        currentDirection = Direction.value(i);
        setLastAction(RobotAction.TURN_RIGHT);
        moveDone();
    }

    public void moveWest(){
        int i = currentDirection.getValue();
        switch(i){
            case 3:
                moveFrontOneStep();
                break;
            case 0:
                turnLeft();
                moveFrontOneStep();
                break;
            case 1:
                turnLeft();
                turnLeft();
                moveFrontOneStep();
                break;
            case 2:
                turnRight();
                moveFrontOneStep();
                break;
        }
        //currentDirection = Direction.value(3);
        moveDone();
    }

    public void moveEast(){
        int i = currentDirection.getValue();
        switch(i){
            case 1:
                moveFrontOneStep();
                break;
            case 0:
                turnRight();
                moveFrontOneStep();
                break;
            case 3:
                turnLeft();
                turnLeft();
                moveFrontOneStep();
                break;
            case 2:
                turnLeft();
                moveFrontOneStep();
                break;
        }
        //currentDirection = Direction.value(1);
        moveDone();
    }

    public void moveNorth(){
        int i = currentDirection.getValue();
        switch(i){
            case 0:
                moveFrontOneStep();
                break;
            case 1:
                turnLeft();
                moveFrontOneStep();
                break;
            case 2:
                turnLeft();
                turnLeft();
                moveFrontOneStep();
                break;
            case 3:
                turnRight();
                moveFrontOneStep();
                break;
        }
        //currentDirection = Direction.value(0);
        moveDone();
    }
    public void moveSouth(){
        int i = currentDirection.getValue();
        switch(i){
            case 2:
                moveFrontOneStep();
                break;
            case 3:
                turnLeft();
                moveFrontOneStep();
                break;
            case 0:
                turnLeft();
                turnLeft();
                moveFrontOneStep();
                break;
            case 1:
                turnRight();
                moveFrontOneStep();
                break;
        }
        //currentDirection = Direction.value(2);
        moveDone();
    }

    /**
     * Set callibrate command
     */
    public void callibrate(){
        setLastAction(RobotAction.CALLIBRATE);
        moveDone();
    }


    /**
     * Method to update subscriber
     */
    private void moveDone(){
        this.state = RobotState.WAITING;
        for (RobotSubscriber rs : robotSubscriberList){
            rs.onMove();
        }
    }

    /**
     * Get the current state of the robot center in ArenaCellCoordinates
     * @return
     */
    public ArenaCellCoordinate getRobotCenter() {
        return robotCenter;
    }

    /**
     * Get the current state of the robot direction
     * @return
     */
    public Direction getCurrentDirection() {
        return currentDirection;
    }

    /**
     * set last action of robot
     * @param action
     */
    public void setLastAction(RobotAction action){
        this.lastAction = action;
    }

    /**
     * Get last action of robot. Used by network service to determine what action to send
     * @return
     */
    public RobotAction getLastAction(){
        return this.lastAction;
    }


    /**
     * Call by network service to ask robotmodel to update map
     *
     * @param sensorInfoPacket
     */
    public void updateMap(SensorInfoPacket sensorInfoPacket){
        System.out.println("\n---------------------- Start of values Mapping------------------------");
        System.out.printf("Current Robot position: x: %d, y: %d \n", robotCenter.getX(), robotCenter.getY());
        sensors.get(RobotSensorPlacement.FRONT_CENTER).sense(sensorInfoPacket.getFC());
        sensors.get(RobotSensorPlacement.FRONT_LEFT).sense(sensorInfoPacket.getFL());
        sensors.get(RobotSensorPlacement.FRONT_RIGHT).sense(sensorInfoPacket.getFR());
        sensors.get(RobotSensorPlacement.RIGHT_TOP).sense(sensorInfoPacket.getRT());
        sensors.get(RobotSensorPlacement.RIGHT_BOTTOM).sense(sensorInfoPacket.getRB());
        sensors.get(RobotSensorPlacement.LEFT_MIDDLE).sense(sensorInfoPacket.getLM());
        System.out.println("---------------------- End of values Mapping------------------------\n");
        if(this.state == RobotState.WAITING){
            setRobotState(RobotState.READY);
        }
    }


    /**
     * This method updates the area memory based aaon the results provided by the sensors
     * The results passed into this method should already be describing each coordinate and the cell type.
     *
     * @param results
     */
    public void sensorUpdateCallBack(List<Pair<ArenaCellCoordinate, ArenaCellType>> results){
        for (Pair<ArenaCellCoordinate, ArenaCellType> row : results) {
            if(row.getT() == null){
                //This case is where the sensor try to map a value out of grid. in such case we do not need to map
                continue;
            }
            if(row.getV() == ArenaCellType.BLOCK){
                System.out.printf("Coord x: %d y:%d is a block!\n", row.getT().getX(), row.getT().getY());
                ArenaCellModel acm = explored.getCellModelByCoordinates(row.getT());
                if(acm.arenaCellType == ArenaCellType.EMPTY){
                    continue;
                }
                explored.setCellAsBlock(row.getT());
            }
            else if(row.getV() == ArenaCellType.EMPTY){
                if(explored.getCellModelByCoordinates(row.getT()).getCellType() != ArenaCellType.BLOCK){
                    System.out.printf("Coord x: %d y:%d is not a block!\n", row.getT().getX(), row.getT().getY());
                    explored.setCellAsEmpty(row.getT());
                }

            }
        }
    }

    public boolean calibrationCondtion(){
        RobotSensor rightTop = sensors.get(RobotSensorPlacement.RIGHT_TOP);
        RobotSensor rightBottom = sensors.get(RobotSensorPlacement.RIGHT_BOTTOM);
        RobotSensor frontLeft = sensors.get(RobotSensorPlacement.FRONT_LEFT);
        RobotSensor frontRight = sensors.get(RobotSensorPlacement.FRONT_RIGHT);
        return !rightTop.isDirectFrontEmpty(explored)
                && !rightBottom.isDirectFrontEmpty(explored)
                && !frontLeft.isDirectFrontEmpty(explored)
                && !frontRight.isDirectFrontEmpty(explored);
    }

    public boolean robotFrontSideEmpty(){
        RobotSensor frontLeft = sensors.get(RobotSensorPlacement.FRONT_LEFT);
        RobotSensor frontCenter = sensors.get(RobotSensorPlacement.FRONT_CENTER);
        RobotSensor frontRight = sensors.get(RobotSensorPlacement.FRONT_RIGHT);
        return frontLeft.isDirectFrontEmpty(explored)
                && frontCenter.isDirectFrontEmpty(explored)
                && frontRight.isDirectFrontEmpty(explored);
    }

    public boolean robotRightSideEmpty(){
        RobotSensor rightTop = sensors.get(RobotSensorPlacement.RIGHT_TOP);
        RobotSensor rightBot = sensors.get(RobotSensorPlacement.RIGHT_BOTTOM);
        return rightBot.isDirectFrontEmpty(explored) && rightTop.isDirectFrontEmpty(explored) && virtualRightSensor.isDirectFrontEmpty(explored);
    }


    public synchronized void waitForReadyState()  {
        while(this.state != RobotState.READY){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        notifyAll();
    }

    public synchronized void setRobotState(RobotState robotState){
        this.state = robotState;
        notifyAll();
    }

}

