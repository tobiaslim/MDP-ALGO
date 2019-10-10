package algorithm.models;

import algorithm.ArenaMemory;
import algorithm.constants.*;
import exception.OutOfGridException;
import utility.Pair;

import java.util.ArrayList;
import java.util.Collection;
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

    public void sense(int sensorValue){
        System.out.printf("***** Start of %s *****\n", placement.toString());
        List<Pair<ArenaCellCoordinate, ArenaCellType>> results = new ArrayList<>();
        /**
         * This section detects which part of the sensor direct front has a block.
         * This is achieved by looping through the zones registered for each sensor.
         * If a zone is found, the zone variable will not be null.
         * if zone is not found, we will check if it is a short range sensor by checking the sensor placement.
         * We will then set noOfGrids based on the condition previously to determine how many empty grids are
         * of the sensor.
         */
        Zone zone = null;
        for(SensorValueMapping svm : valueMappings.values()){
            if(svm.isValueWithinRange(sensorValue)){
                zone = svm.getZone();
            }
        }

        //check if sensor values falls in safe zone
        int noOfGrids = 0;
        if(zone != null){
            //founded the zone
            noOfGrids = zone.getValue();
        }
        else{
            //no zone found, either exceeds or value before
            if(placement == RobotSensorPlacement.LEFT_MIDDLE){
                SensorValueMapping firstSensorMapping = valueMappings.values().iterator().next();
                SensorValueMapping lastSensorMapping = (SensorValueMapping) valueMappings.values().toArray()[valueMappings.values().size()-1];
                if(sensorValue < firstSensorMapping.getStartRange()){
                    //to cater for the starting blind zone of long range
                    noOfGrids = 0;

                }
                else if(sensorValue > firstSensorMapping.getStartRange() && sensorValue < lastSensorMapping.getEndRange()){
                    // falls not between any zone, but assured that first 2 blocks is empty
                    noOfGrids = 2;
                }
                else{
                    //if exceeds the furthest range
                    noOfGrids = 4;
                }

            }
            else{
                noOfGrids = 1; //no of safe grids if no zone found
            }
        }

        /**
         * This section populates all the coordinates that need to be map based on the previous result.
         */

        ArenaCellCoordinate currentSensorCoordinate = getTheCurrentCoordinateOfSensor();
        Direction facingDirection = currentFacingDirection();
        Matrix directionMatrix = getDirectionMatrix(facingDirection);
        System.out.println(facingDirection.toString() + " " + currentSensorCoordinate.getX() + " " + currentSensorCoordinate.getY());
        System.out.println(facingDirection.toString()+" "+directionMatrix.getI() + " " +directionMatrix.getJ());
        Matrix sumMatrix;

        //populating the coordinates that need to be mapped.
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
                p.setV(ArenaCellType.BLOCK);
            }
            else{
                p.setV(ArenaCellType.EMPTY);
            }
            results.add(p);
        }
        robotModel.sensorUpdateCallBack(results);
        System.out.printf("***** End of %s*****\n", placement.toString());
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
