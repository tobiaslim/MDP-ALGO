package algorithm.models;

import algorithm.ArenaMemory;
import algorithm.constants.ArenaCellType;
import algorithm.constants.Direction;
import algorithm.constants.RobotSensorPlacement;
import algorithm.constants.Zone;
import exception.OutOfGridException;
import utility.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RobotSensor {
    private RobotSensorPlacement placement;
    private RobotModel robotModel;
    private Map<Zone, SensorValueMapping> valueMappings;

    private int offset;         // the minimum range to detect
    private int interval;       // the of each interval
    private int maxRange;       // any value after this is considered empty
    private int noOfSafeZone;   // no of safe grid if maxrange exceeds


    public RobotSensor(RobotModel robotModel, RobotSensorPlacement robotSensorPlacement, int offset, int interval, int maxRange, int noOfSafeZone){
        this.robotModel = robotModel;
        this.placement = robotSensorPlacement;
        this.offset = offset;
        this.interval = interval;
        this.maxRange = maxRange - offset;
        this.noOfSafeZone = noOfSafeZone;
    }

    public RobotSensor(Map<Zone, SensorValueMapping> mappings, RobotModel model, RobotSensorPlacement placement){
        this.robotModel = model;
        this.placement = placement;
        this.valueMappings = mappings;
    }

    public boolean isDirectFrontEmpty(ArenaMemory map){
        ArenaCellCoordinate acc = getTheCurrentCoordinateOfSensor();
        Matrix directionMatrix = getDirectionMatrix(currentFacingDirection());
        try{
            acc.addMatrixValue(directionMatrix);
        }
        catch (OutOfGridException e){
            // This case happens when robot is detecting outside the grid map.
            // we can assume that it is is wall thus we return false
            return false;
        }

        ArenaCellModel acm = map.getCellModelByCoordinates(acc);
        if(acm.getCellType() == ArenaCellType.BLOCK){
            return false;
        }
        else{
            return true;
        }
    }

    public void sense(int sensorValue) {
        sensorValue = sensorValue - offset;
        /**
         * Possible scenarios:
         * x is lower safe, y is upper safe
         * 1. Sensor values goes above the upper safe range (y+1 ~ infty)
         * 2. Sensor value between lower and upper safe range (x ~ y)
         * 3. Sensor value below the lower safe range (0 ~ x)
         *
         * Case 1: Make the assured range to be empty as empty.
         * Case 2: Make the block
         */
        if(sensorValue > maxRange){
            // crazy values, but assured x grid in front of sensor is empty(safeRange)

            ArenaCellCoordinate currentSensorCoordinate = getTheCurrentCoordinateOfSensor();
            Direction facingDirection = currentFacingDirection();

            Matrix directionMatrix = getDirectionMatrix(facingDirection);

            List<Pair<ArenaCellCoordinate, ArenaCellType>> results = new ArrayList<>();
            int gridOffset = offset/interval;
            for(int i = gridOffset; i < noOfSafeZone; i++){
                Matrix offset = new Matrix(directionMatrix, i);
                ArenaCellCoordinate cell;

                try{
                    cell = new ArenaCellCoordinate(currentSensorCoordinate, offset);
                }
                catch (OutOfGridException e){
                    cell = null;
                }

                Pair<ArenaCellCoordinate, ArenaCellType> row = new Pair<>();
                row.setT(cell);
                row.setV(ArenaCellType.EMPTY);
                results.add(row);
            }
            robotModel.sensorUpdateCallBack(results);
        }
        else{
            int noOfGridAhead = (sensorValue / interval);
            //Logic to detect where is the block
            //1. find sensor current coordinate
            //2. find sensor current facing direction
            //3. Based on sensor facing direction, get the matrix for the direction
            //4. multiply noOfGridAhead with matrix. Result matrix is the x and y distance from the sensor detecting
            //5. Add current sensor coordinate with the difference to get where the detected block is at
            ArenaCellCoordinate currentSensorCoordinate = getTheCurrentCoordinateOfSensor();
            Direction facingDirection = currentFacingDirection();

            Matrix directionMatrix = getDirectionMatrix(facingDirection);

            List<Pair<ArenaCellCoordinate, ArenaCellType>> results = new ArrayList<>();
            int gridOffset = offset/interval;
            for(int i = gridOffset; i <= noOfGridAhead; i++){
                Matrix offset = new Matrix(directionMatrix, i);
                ArenaCellCoordinate cell;
                try{
                    cell = new ArenaCellCoordinate(currentSensorCoordinate, offset);
                }
                catch (OutOfGridException e){
                    // this case happens when the grid detected is a wall
                    cell = null;
                }


                Pair<ArenaCellCoordinate, ArenaCellType> row = new Pair<>();
                row.setT(cell);
                if(noOfGridAhead == i){
                    row.setV(ArenaCellType.BLOCK);
                }
                else{
                    row.setV(ArenaCellType.EMPTY);
                }
                results.add(row);
            }


            robotModel.sensorUpdateCallBack(results);
        }
    }

    public void sensev2(int sensorValue){
        List<Pair<ArenaCellCoordinate, ArenaCellType>> results = new ArrayList<>();

        Zone zone = null;
        for(SensorValueMapping svm : valueMappings.values()){
            if(svm.isValueWithinRange(sensorValue)){
                zone = svm.getZone();
            }
        }

        int noOfGrids = 0;
        if(zone != null){
            //founded the zone
            noOfGrids = zone.getValue();
        }
        else{
            //no zone found, either exceeds or value before
            if(placement == RobotSensorPlacement.LEFT_MIDDLE){
                noOfGrids = 0;
            }
            else{
                noOfGrids = 2;
            }
        }

        ArenaCellCoordinate currentSensorCoordinate = getTheCurrentCoordinateOfSensor();
        Direction facingDirection = currentFacingDirection();
        Matrix directionMatrix = getDirectionMatrix(facingDirection);
        Matrix sumMatrix;
        for(int i = 0; i < noOfGrids; i++){
            sumMatrix = new Matrix(directionMatrix,i);
            ArenaCellCoordinate acc;
            try{
                acc = new ArenaCellCoordinate(currentSensorCoordinate, sumMatrix);
            }
            catch (OutOfGridException e){
                // if out of grid, no need map
                continue;
            }
            Pair<ArenaCellCoordinate, ArenaCellType> p = new Pair<ArenaCellCoordinate, ArenaCellType>();
            p.setT(acc);
            if((i+1) == noOfGrids && zone != null){
                System.out.printf("Robot Sensor: Setting x: %d y: %d as block\n", acc.getX(), acc.getY());
                p.setV(ArenaCellType.BLOCK);
            }
            else{
                p.setV(ArenaCellType.EMPTY);
            }
            results.add(p);
        }
        robotModel.sensorUpdateCallBack(results);
    }


    /**
     * retrieve the coordinates of the sensor. As the robot will move and coordinate of sensor will change,
     * currently it being calculated using an offset for each different sensor.
     *
     * @return
     */
    private ArenaCellCoordinate getTheCurrentCoordinateOfSensor(){
        ArenaCellCoordinate sensorCoordinate;
        switch (placement){
            case FRONT_LEFT:
                sensorCoordinate = getFrontLeftSensor();
                break;
            case FRONT_CENTER:
                sensorCoordinate = getFrontCenterSensor();
                break;
            case FRONT_RIGHT:
            case RIGHT_TOP:
                sensorCoordinate = getFrontRightSensor();
                break;
            case RIGHT_BOTTOM:
                sensorCoordinate = getRightBotSensor();
                break;
            case RIGHT_MIDDLE:
                sensorCoordinate = getRightMiddleSensor();
                break;
            case LEFT_MIDDLE:
                sensorCoordinate = getLeftMiddleSensor();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + placement);
        }

        return sensorCoordinate;
        
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
    private Direction currentFacingDirection(){
        Direction sensorDirection = robotModel.getCurrentDirection();
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

        if(directionValue < 0) directionValue +=4;

        return Direction.value(directionValue % 4);
    }


    private ArenaCellCoordinate getFrontLeftSensor(){
        ArenaCellCoordinate acc = robotModel.getRobotCenter();
        switch (robotModel.getCurrentDirection()){
            case NORTH:
                acc = new ArenaCellCoordinate(acc.getX() - 1, acc.getY()+1);
                break;
            case EAST:
                acc = new ArenaCellCoordinate(acc.getX() + 1, acc.getY() + 1);
                break;
            case SOUTH:
                acc = new ArenaCellCoordinate(acc.getX() + 1, acc.getY() - 1 );
                break;
            case WEST:
                acc = new ArenaCellCoordinate(acc.getX() - 1, acc.getY() - 1);
                break;
        }
        return acc;
    }

    private ArenaCellCoordinate getFrontCenterSensor(){
        ArenaCellCoordinate acc = robotModel.getRobotCenter();
        switch (robotModel.getCurrentDirection()){
            case NORTH:
                acc = new ArenaCellCoordinate(acc.getX(), acc.getY()+1);
                break;
            case EAST:
                acc = new ArenaCellCoordinate(acc.getX() + 1, acc.getY());
                break;
            case SOUTH:
                acc = new ArenaCellCoordinate(acc.getX(), acc.getY() - 1 );
                break;
            case WEST:
                acc = new ArenaCellCoordinate(acc.getX() - 1, acc.getY());
                break;
        }
        return acc;
    }

    private ArenaCellCoordinate getFrontRightSensor(){
        ArenaCellCoordinate acc = robotModel.getRobotCenter();
        switch (robotModel.getCurrentDirection()){
            case NORTH:
                acc = new ArenaCellCoordinate(acc.getX() + 1, acc.getY() + 1);
                break;
            case EAST:
                acc = new ArenaCellCoordinate(acc.getX() + 1, acc.getY() - 1);
                break;
            case SOUTH:
                acc = new ArenaCellCoordinate(acc.getX() - 1, acc.getY() - 1);
                break;
            case WEST:
                acc = new ArenaCellCoordinate(acc.getX() - 1, acc.getY()+1);
                break;
        }
        return acc;
    }


    private ArenaCellCoordinate getRightTopSensor(){
        ArenaCellCoordinate acc = robotModel.getRobotCenter();
        switch (robotModel.getCurrentDirection()){
            case NORTH:
                acc = new ArenaCellCoordinate(acc.getX()+2, acc.getY()+1);
                break;
            case EAST:
                acc = new ArenaCellCoordinate(acc.getX() + 1, acc.getY()-2);
                break;
            case SOUTH:
                acc = new ArenaCellCoordinate(acc.getX()-2, acc.getY() - 1 );
                break;
            case WEST:
                acc = new ArenaCellCoordinate(acc.getX() - 1, acc.getY()+2);
                break;
        }
        return acc;
    }


    private ArenaCellCoordinate getRightBotSensor(){
        ArenaCellCoordinate acc = robotModel.getRobotCenter();
        switch (robotModel.getCurrentDirection()){
            case NORTH:
                acc = new ArenaCellCoordinate(acc.getX()+1, acc.getY()-1);
                break;
            case EAST:
                acc = new ArenaCellCoordinate(acc.getX() - 1, acc.getY()-1);
                break;
            case SOUTH:
                acc = new ArenaCellCoordinate(acc.getX()-1, acc.getY() + 1 );
                break;
            case WEST:
                acc = new ArenaCellCoordinate(acc.getX() + 1, acc.getY()+1);
                break;
        }
        return acc;
    }

    private ArenaCellCoordinate getRightMiddleSensor(){
        ArenaCellCoordinate acc = robotModel.getRobotCenter();
        switch (robotModel.getCurrentDirection()){
            case NORTH:
                acc = new ArenaCellCoordinate(acc.getX()+1, acc.getY());
                break;
            case EAST:
                acc = new ArenaCellCoordinate(acc.getX(), acc.getY()-1);
                break;
            case SOUTH:
                acc = new ArenaCellCoordinate(acc.getX()-1, acc.getY() );
                break;
            case WEST:
                acc = new ArenaCellCoordinate(acc.getX(), acc.getY()+1);
                break;
        }
        return acc;
    }

    private ArenaCellCoordinate getLeftMiddleSensor(){
        ArenaCellCoordinate acc = robotModel.getRobotCenter();
        switch (robotModel.getCurrentDirection()){
            case NORTH:
                acc = new ArenaCellCoordinate(acc.getX()-1, acc.getY());
                break;
            case EAST:
                acc = new ArenaCellCoordinate(acc.getX(), acc.getY()+1);
                break;
            case SOUTH:
                acc = new ArenaCellCoordinate(acc.getX()+1, acc.getY()-1);
                break;
            case WEST:
                acc = new ArenaCellCoordinate(acc.getX(), acc.getY()-1);
                break;
        }
        return acc;
    }


}
