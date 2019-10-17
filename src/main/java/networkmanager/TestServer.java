package networkmanager;

import algorithm.ArenaMemory;
import algorithm.constants.*;
import algorithm.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.OutOfGridException;
import networkmanager.dto.ControlSignalPacket;
import networkmanager.dto.Packet;
import networkmanager.dto.SensorInfoPacket;
import networkmanager.dto.WayPointPacket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * This class only used in simulation
 */
public class TestServer extends Thread{
    public ServerSocket serverSocket;
    private ObjectMapper objectMapper = new ObjectMapper();
    private MDFFormat mdf;
    int i = 0;
    ArenaMemory simulated;
    private ArenaCellCoordinate currentPosition;
    private Direction currentDirection;
    private Map<RobotSensorPlacement, Map<Zone,SensorValueMapping>> sensorPlacementSensorValueMapping;

    public TestServer(MDFFormat format) throws IOException {
        this.mdf = format;
        serverSocket = new ServerSocket(2555);
        simulated = new ArenaMemory();
        simulated.updateArenaWithMDF(mdf);
        currentPosition = new ArenaCellCoordinate(1, 1);
        currentDirection = Direction.SOUTH;
        this.sensorPlacementSensorValueMapping  = new HashMap<>();
        initializeSensorPlacement();
    }

    public void initializeSensorPlacement(){

        Map<Zone,SensorValueMapping> fl = new HashMap<>();
        fl.put(Zone.A, new SensorValueMapping(Zone.A, SensorContants.FRONT_LEFT_A_START , SensorContants.FRONT_LEFT_A_END));
//        fl.put(Zone.B, new SensorValueMapping(Zone.B, 16 ,25));
//        fl.put(Zone.C, new SensorValueMapping(Zone.C, 26 ,36));

        sensorPlacementSensorValueMapping.put(RobotSensorPlacement.FRONT_LEFT, fl);

        Map<Zone,SensorValueMapping> fc = new HashMap<>();
        fc.put(Zone.A, new SensorValueMapping(Zone.A, SensorContants.FRONT_CENTER_A_START ,SensorContants.FRONT_CENTER_A_END));
//        fc.put(Zone.B, new SensorValueMapping(Zone.B, 11 ,20));
//        fc.put(Zone.C, new SensorValueMapping(Zone.C, 21 ,30));

        sensorPlacementSensorValueMapping.put(RobotSensorPlacement.FRONT_CENTER, fc);

        Map<Zone,SensorValueMapping> fr = new HashMap<>();
        fr.put(Zone.A, new SensorValueMapping(Zone.A, SensorContants.FRONT_RIGHT_A_START ,SensorContants.FRONT_RIGHT_A_END));
//        fr.put(Zone.B, new SensorValueMapping(Zone.B, 15 ,25));
//        fr.put(Zone.C, new SensorValueMapping(Zone.C, 26 ,35));

        sensorPlacementSensorValueMapping.put(RobotSensorPlacement.FRONT_RIGHT, fr);

        Map<Zone,SensorValueMapping> rt = new HashMap<>();
        rt.put(Zone.A, new SensorValueMapping(Zone.A, SensorContants.RIGHT_TOP_A_START,SensorContants.RIGHT_TOP_A_END));
//        rt.put(Zone.B, new SensorValueMapping(Zone.B, 14 ,23));
//        rt.put(Zone.C, new SensorValueMapping(Zone.C, 24 ,35));

        sensorPlacementSensorValueMapping.put(RobotSensorPlacement.RIGHT_TOP, rt);

        Map<Zone,SensorValueMapping> rb = new HashMap<>();
        rb.put(Zone.A, new SensorValueMapping(Zone.A, SensorContants.RIGHT_BOTTOM_A_START ,SensorContants.RIGHT_BOTTOM_A_END));
//        rb.put(Zone.B, new SensorValueMapping(Zone.B, 15 ,28));
//        rb.put(Zone.C, new SensorValueMapping(Zone.C, 29 ,43));

        sensorPlacementSensorValueMapping.put(RobotSensorPlacement.RIGHT_BOTTOM, rb);

        Map<Zone,SensorValueMapping> lm = new HashMap<>();
        lm.put(Zone.A, new SensorValueMapping(Zone.A, 0 ,10));
        lm.put(Zone.B, new SensorValueMapping(Zone.B, 11 ,20));
        lm.put(Zone.C, new SensorValueMapping(Zone.C, SensorContants.LEFT_MIDDLE_C_START ,SensorContants.LEFT_MIDDLE_C_END));
        lm.put(Zone.D, new SensorValueMapping(Zone.D, SensorContants.LEFT_MIDDLE_D_START ,SensorContants.LEFT_MIDDLE_D_END));
        lm.put(Zone.E, new SensorValueMapping(Zone.E, SensorContants.LEFT_MIDDLE_E_START ,SensorContants.LEFT_MIDDLE_E_END));
//        lm.put(Zone.F, new SensorValueMapping(Zone.F, 59 ,68));

        sensorPlacementSensorValueMapping.put(RobotSensorPlacement.LEFT_MIDDLE, lm);



    }


    @Override
    public void run() {
            System.out.println("Server: Server thread :"+Thread.currentThread().getName() + " now running");
            try {
                Socket c = serverSocket.accept();
                System.out.println("Server: New connection from " + c.getInetAddress().getHostAddress());
                PrintWriter out = new PrintWriter(c.getOutputStream());

                /** To simulate bluetooth start command **/
//                simulateBluetoothStart(out);
                simulateWayPoint(out);

                BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    i++;
                    System.out.println("Server: Message received from client :" + inputLine);

                    JsonNode jsonNodeRoot = objectMapper.readTree(inputLine);
                    JsonNode data = jsonNodeRoot.get("data");
                    if(data.has("action")){
                        if(data.get("action").asText().equals(Character.toString(RobotAction.START.getValue()))){
//                            System.out.println("Server: starting action");
                        }
                        else if(data.get("action").asText().equals(Character.toString(RobotAction.MOVE_STRAIGHT.getValue()))){
                            moveFrontOneStep();
                        }
                        else if(data.get("action").asText().equals(Character.toString(RobotAction.TURN_LEFT.getValue()))){
                            turnLeft();
                        }
                        else if(data.get("action").asText().equals(Character.toString(RobotAction.TURN_RIGHT.getValue()))){
                            turnRight();
                        }
                        else if(data.get("action").asText().equals(Character.toString(RobotAction.X_ROUTINE.getValue()))){
                            turnRightGoStraight();
                        }
                        else{
//                            System.out.println("Server: unknown action");
                        }

//                        System.out.printf("Server: Simulated robot moved to x: %d, y: %d, direction: %s \n", currentPosition.getX(), currentPosition.getY(), currentDirection);

                        Map<RobotSensorPlacement, SensorValueMapping> newlyScanned = getFiveZones(currentPosition);


                        SensorInfoPacket sensorInfoPacket = prepareResponse(newlyScanned);
                        Packet p = new Packet();
                        p.setSender(NetworkRecipient.ARDUINO.toString());
                        p.setRecipient(NetworkRecipient.ALGORITHM.toString());
                        p.setData(sensorInfoPacket);

                        String jsonMessage = objectMapper.writeValueAsString(p);
                        System.out.println("Server: sending message"  + jsonMessage);
                        out.println(jsonMessage);
                        out.flush();
//                        System.out.println("Server: Sending back to client:" + jsonMessage);
                    }
                    else if (data.has("explorationStatusString")){
//                        System.out.println("Server: sending data to android");
                    }
                    else{
//                        System.out.println("Server: unknown packet");
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    public void moveFrontOneStep(){
        switch (this.currentDirection){
            case NORTH:
                currentPosition.setY(currentPosition.getY()+1);
                break;
            case EAST:
                currentPosition.setX(currentPosition.getX()+1);
                break;
            case SOUTH:
                currentPosition.setY(currentPosition.getY()-1);
                break;
            case WEST:
                currentPosition.setX(currentPosition.getX()-1);
        }
        System.out.printf("Server: new position is x: %d, y: %d\n", currentPosition.getX(), currentPosition.getY());
    }

    public void turnLeft(){
        int i = (currentDirection.getValue() - 1) % 4;
        if(i < 0) i += 4;
        currentDirection = Direction.value(i);
    }


    public void turnRight(){
        int i = (currentDirection.getValue() + 1) % 4;
        currentDirection = Direction.value(i);
    }

    public void turnRightGoStraight(){
        turnRight();
        moveFrontOneStep();
    }

    public Map<RobotSensorPlacement, SensorValueMapping> getFiveZones(ArenaCellCoordinate coordinate){
        Map<RobotSensorPlacement, SensorValueMapping> sensorvaluemappings = new HashMap<>();

        SensorValueMapping svm;

        svm = getSensorValueMapping(RobotSensorPlacement.FRONT_LEFT, getFrontLeftSensor(coordinate), getDirectionMatrix(currentFacingDirection(RobotSensorPlacement.FRONT_LEFT)));
        sensorvaluemappings.put(RobotSensorPlacement.FRONT_LEFT, svm);

        svm = getSensorValueMapping(RobotSensorPlacement.FRONT_CENTER, getFrontCenterSensor(coordinate), getDirectionMatrix(currentFacingDirection(RobotSensorPlacement.FRONT_CENTER)));
        sensorvaluemappings.put(RobotSensorPlacement.FRONT_CENTER, svm);

        svm = getSensorValueMapping(RobotSensorPlacement.FRONT_RIGHT, getFrontRightSensor(coordinate), getDirectionMatrix(currentFacingDirection(RobotSensorPlacement.FRONT_RIGHT)));
        sensorvaluemappings.put(RobotSensorPlacement.FRONT_RIGHT, svm);

        svm = getSensorValueMapping(RobotSensorPlacement.RIGHT_TOP, getFrontRightSensor(coordinate), getDirectionMatrix(currentFacingDirection(RobotSensorPlacement.RIGHT_TOP)));
        sensorvaluemappings.put(RobotSensorPlacement.RIGHT_TOP, svm);

        svm = getSensorValueMapping(RobotSensorPlacement.RIGHT_BOTTOM, getRightBotSensor(coordinate), getDirectionMatrix(currentFacingDirection(RobotSensorPlacement.RIGHT_BOTTOM)));
        sensorvaluemappings.put(RobotSensorPlacement.RIGHT_BOTTOM, svm);

        svm = getSensorValueMapping(RobotSensorPlacement.LEFT_MIDDLE, getLeftMiddleSensor(coordinate), getDirectionMatrix(currentFacingDirection(RobotSensorPlacement.LEFT_MIDDLE)));
        sensorvaluemappings.put(RobotSensorPlacement.LEFT_MIDDLE, svm);

        return sensorvaluemappings;
    }

    /**
     * Read state from answer map
     *
     * @param placement
     * @param sensorCoordinate
     * @param directionMatrix
     * @return
     */
    public SensorValueMapping getSensorValueMapping(RobotSensorPlacement placement,ArenaCellCoordinate sensorCoordinate, Matrix directionMatrix){
        Map<Zone, SensorValueMapping> reference = sensorPlacementSensorValueMapping.get(placement);
        System.out.printf("Sensor coordinate is x:%d y:%d", sensorCoordinate.getX(), sensorCoordinate.getY());
        for(int i = 0; i  < Zone.values().length; i++){
            try{
                System.out.println(placement + " getting next zone " + Zone.value(i+1));
                sensorCoordinate.addMatrixValue(directionMatrix);
            }catch (OutOfGridException e){
                System.out.println("oog");
                //out of grid, return current zone
//                System.out.print(placement.toString() + " detected out of grid assigning latest zone");
                Zone zone = Zone.value(i+1);
                if(zone == null){
                    //out of grid and zone not registered for this sensor
                    return null;
                }
//                System.out.println(", assingning zone" +zone.toString() );
                SensorValueMapping svm = reference.get(zone);
                return svm;

            }

            if(simulated.getCellModelByCoordinates(sensorCoordinate).getCellType() == ArenaCellType.BLOCK){
//                System.out.println(placement.toString() + " detected block at " + sensorCoordinate.getX() + " " + sensorCoordinate.getY());
                Zone zone = Zone.value(i+1);
                if(zone == null){
                    return null;
                }
                SensorValueMapping svm = reference.get(zone);
                return svm;
            }
        }
        return null;
    }

    public Zone getLastZone(int zoneValue){
        Zone newZone = Zone.value(zoneValue);
        if(newZone == null){
            return getLastZone(zoneValue-1);
        }
        else{
            return newZone;
        }
    }

    SensorInfoPacket prepareResponse(Map<RobotSensorPlacement, SensorValueMapping> results){
        SensorInfoPacket sensorInfoPacket = new SensorInfoPacket();
        SensorValueMapping svm;

        svm = results.get(RobotSensorPlacement.RIGHT_BOTTOM);
        if(svm == null){
            sensorInfoPacket.setRB(100);
        }
        else{
            sensorInfoPacket.setRB(random(results.get(RobotSensorPlacement.RIGHT_BOTTOM).getStartRange(), results.get(RobotSensorPlacement.RIGHT_BOTTOM).getEndRange()-results.get(RobotSensorPlacement.RIGHT_BOTTOM).getStartRange()));
        }

        svm = results.get(RobotSensorPlacement.RIGHT_TOP);
        if(svm == null){
            sensorInfoPacket.setRT(100);
        }
        else{
            sensorInfoPacket.setRT(random(results.get(RobotSensorPlacement.RIGHT_TOP).getStartRange(), results.get(RobotSensorPlacement.RIGHT_TOP).getEndRange()-results.get(RobotSensorPlacement.RIGHT_TOP).getStartRange()));
        }

        svm = results.get(RobotSensorPlacement.FRONT_RIGHT);
        if(svm == null){
            sensorInfoPacket.setFR(100);
        }
        else{
            sensorInfoPacket.setFR(random(results.get(RobotSensorPlacement.FRONT_RIGHT).getStartRange(), results.get(RobotSensorPlacement.FRONT_RIGHT).getEndRange()-results.get(RobotSensorPlacement.FRONT_RIGHT).getStartRange()));
        }

        svm = results.get(RobotSensorPlacement.FRONT_CENTER);
        if(svm == null){
            sensorInfoPacket.setFC(100);
        }
        else{
            sensorInfoPacket.setFC(random(results.get(RobotSensorPlacement.FRONT_CENTER).getStartRange(), results.get(RobotSensorPlacement.FRONT_CENTER).getEndRange()-results.get(RobotSensorPlacement.FRONT_CENTER).getStartRange()));
        }

        svm = results.get(RobotSensorPlacement.FRONT_LEFT);
        if(svm == null){
            sensorInfoPacket.setFL(100);
        }
        else{
            sensorInfoPacket.setFL(random(results.get(RobotSensorPlacement.FRONT_LEFT).getStartRange(), results.get(RobotSensorPlacement.FRONT_LEFT).getEndRange()-results.get(RobotSensorPlacement.FRONT_LEFT).getStartRange()));
        }

        svm = results.get(RobotSensorPlacement.LEFT_MIDDLE);
        if(svm == null){
            sensorInfoPacket.setLM(100);
        }
        else if (svm.getZone().getValue() < 3){
            sensorInfoPacket.setLM(random(0, 23));
        }
        else{
            sensorInfoPacket.setLM(random(results.get(RobotSensorPlacement.LEFT_MIDDLE).getStartRange(), results.get(RobotSensorPlacement.LEFT_MIDDLE).getEndRange()-results.get(RobotSensorPlacement.LEFT_MIDDLE).getStartRange()));
        }


        return sensorInfoPacket;
    }


    public int random(int base, int interval){
        return new Random().nextInt(interval+1)+base;
    }

    public ArenaMemory getArenaMemory(){
        return this.simulated;
    }


    public void simulateBluetoothStart(PrintWriter out){
        /**
         * Simulate sending of bluetooth
         */
        Runnable r = () -> {
            ControlSignalPacket csp = new ControlSignalPacket();
            csp.setAction(ControlSignalPacket.RobotSignal.START_EXPLORE);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            out.flush();
            Packet p = new Packet();
            p.setRecipient(NetworkRecipient.ALGORITHM.toString());
            p.setSender(NetworkRecipient.ANDROID.toString());
            p.setData(csp);
            try {
                String test = objectMapper.writeValueAsString(p);
                out.println(test);
                out.flush();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    public void simulateWayPoint(PrintWriter out){
        /**
         * Simulate sending of bluetooth
         */
        Runnable r = () -> {
            WayPointPacket wpp = new WayPointPacket();
            wpp.setX(4);
            wpp.setY(15);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            out.flush();
            Packet p = new Packet();
            p.setRecipient(NetworkRecipient.ALGORITHM.toString());
            p.setSender(NetworkRecipient.ANDROID.toString());
            p.setData(wpp);
            try {
                String test = objectMapper.writeValueAsString(p);
                System.out.println(test);
                out.println(test);
                out.flush();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        };
        Thread t = new Thread(r);
        t.start();
    }


    private ArenaCellCoordinate getFrontLeftSensor(ArenaCellCoordinate coordinate){
        System.out.printf("Robot Center is x: %d y: %d",coordinate.getX(), coordinate.getY());
        ArenaCellCoordinate acc = new ArenaCellCoordinate(0, 0);
        switch (currentDirection){
            case NORTH:
                acc = new ArenaCellCoordinate(coordinate.getX() - 1, coordinate.getY()+1);
                break;
            case EAST:
                acc = new ArenaCellCoordinate(coordinate.getX() + 1, coordinate.getY() + 1);
                break;
            case SOUTH:
                acc = new ArenaCellCoordinate(coordinate.getX() + 1, coordinate.getY() - 1 );
                break;
            case WEST:
                acc = new ArenaCellCoordinate(coordinate.getX() - 1, coordinate.getY() - 1);
                break;
        }
        return acc;
    }

    private ArenaCellCoordinate getFrontCenterSensor(ArenaCellCoordinate coordinate){
        ArenaCellCoordinate acc = new ArenaCellCoordinate(0, 0);
        switch (currentDirection){
            case NORTH:
                acc = new ArenaCellCoordinate(coordinate.getX(), coordinate.getY()+1);
                break;
            case EAST:
                acc = new ArenaCellCoordinate(coordinate.getX() + 1, coordinate.getY());
                break;
            case SOUTH:
                acc = new ArenaCellCoordinate(coordinate.getX(), coordinate.getY() - 1 );
                break;
            case WEST:
                acc = new ArenaCellCoordinate(coordinate.getX() - 1, coordinate.getY());
                break;
        }
        return acc;
    }

    private ArenaCellCoordinate getFrontRightSensor(ArenaCellCoordinate coordinate){
        ArenaCellCoordinate acc = new ArenaCellCoordinate(0, 0);
        switch (currentDirection){
            case NORTH:
                acc = new ArenaCellCoordinate(coordinate.getX() + 1, coordinate.getY() + 1);
                break;
            case EAST:
                acc = new ArenaCellCoordinate(coordinate.getX() + 1, coordinate.getY() - 1);
                break;
            case SOUTH:
                acc = new ArenaCellCoordinate(coordinate.getX() - 1, coordinate.getY() - 1);
                break;
            case WEST:
                acc = new ArenaCellCoordinate(coordinate.getX() - 1, coordinate.getY()+1);
                break;
        }
        return acc;
    }

    @Deprecated
    private ArenaCellCoordinate getRightTopSensor(ArenaCellCoordinate coordinate){
        ArenaCellCoordinate acc = new ArenaCellCoordinate(0, 0);
        switch (currentDirection){
            case NORTH:
                acc = new ArenaCellCoordinate(coordinate.getX()+2, coordinate.getY()+1);
                break;
            case EAST:
                acc = new ArenaCellCoordinate(coordinate.getX() + 1, coordinate.getY()-2);
                break;
            case SOUTH:
                acc = new ArenaCellCoordinate(coordinate.getX()-2, coordinate.getY() - 1 );
                break;
            case WEST:
                acc = new ArenaCellCoordinate(coordinate.getX() - 1, coordinate.getY()+2);
                break;
        }
        return acc;
    }


    private ArenaCellCoordinate getRightBotSensor(ArenaCellCoordinate coordinate){
        ArenaCellCoordinate acc = new ArenaCellCoordinate(0, 0);
        switch (currentDirection){
            case NORTH:
                acc = new ArenaCellCoordinate(coordinate.getX()+1, coordinate.getY()-1);
                break;
            case EAST:
                acc = new ArenaCellCoordinate(coordinate.getX() - 1, coordinate.getY()-1);
                break;
            case SOUTH:
                acc = new ArenaCellCoordinate(coordinate.getX()-1, coordinate.getY() + 1 );
                break;
            case WEST:
                acc = new ArenaCellCoordinate(coordinate.getX() + 1, coordinate.getY()+1);
                break;
        }
        return acc;
    }

    private ArenaCellCoordinate getRightMiddleSensor(ArenaCellCoordinate coordinate){
        ArenaCellCoordinate acc = new ArenaCellCoordinate(0, 0);
        switch (currentDirection){
            case NORTH:
                acc = new ArenaCellCoordinate(coordinate.getX()+1, coordinate.getY());
                break;
            case EAST:
                acc = new ArenaCellCoordinate(coordinate.getX(), coordinate.getY()-1);
                break;
            case SOUTH:
                acc = new ArenaCellCoordinate(coordinate.getX()-1, coordinate.getY() );
                break;
            case WEST:
                acc = new ArenaCellCoordinate(coordinate.getX(), coordinate.getY()+1);
                break;
        }
        return acc;
    }

    private ArenaCellCoordinate getLeftMiddleSensor(ArenaCellCoordinate coordinate){
        ArenaCellCoordinate acc = new ArenaCellCoordinate(0, 0);
        switch (currentDirection){
            case NORTH:
                acc = new ArenaCellCoordinate(coordinate.getX()-1, coordinate.getY());
                break;
            case EAST:
                acc = new ArenaCellCoordinate(coordinate.getX(), coordinate.getY()+1);
                break;
            case SOUTH:
                acc = new ArenaCellCoordinate(coordinate.getX()+1, coordinate.getY()-1);
                break;
            case WEST:
                acc = new ArenaCellCoordinate(coordinate.getX(), coordinate.getY()-1);
                break;
        }
        return acc;
    }

    /**
     * Retrieves the matrix to be applied to a scalar value to determine where is the block
     *
     * @param direction
     * @return
     */
    private Matrix getDirectionMatrix(Direction direction){
        Matrix matrix;
        switch (direction){
            case NORTH:
                matrix = new Matrix(0, 1);
                break;
            case EAST:
                matrix = new Matrix(1, 0);
                break;
            case SOUTH:
                matrix = new Matrix(0, -1);
                break;
            case WEST:
                matrix = new Matrix(-1, 0);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + direction);
        }
        return matrix;
    }


    /**
     * Get the current direction of where the sensor is facing
     *
     * @return
     */
    private Direction currentFacingDirection(RobotSensorPlacement placement){
        Direction sensorDirection = currentDirection;
        int directionValue = sensorDirection.getValue();
        switch (placement){
            case RIGHT_TOP:
            case RIGHT_BOTTOM:
            case RIGHT_MIDDLE:
                directionValue++;
                break;
            case LEFT_MIDDLE:
                directionValue--;
                break;
        }
        if(directionValue < 0){
            directionValue += 4;
        }
        return Direction.value(directionValue % 4);
    }


}