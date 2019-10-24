package algorithm;

import algorithm.constants.Direction;
import algorithm.constants.RobotSensorPlacement;
import algorithm.contracts.AlgorithmContract;
import algorithm.models.ArenaCellCoordinate;
import algorithm.models.RobotModel;
import exception.OutOfGridException;

public class ExplorationAlgorithm implements AlgorithmContract {
    RobotModel robotModel;
    ArenaMemory arenaMemory;
    boolean resume;
    boolean wallOnRight = false;
    boolean subGoalAchieved = false;
    ArenaCellCoordinate goal;
    ArenaCellCoordinate subgoal;
    ArenaCellCoordinate start;
    private boolean rightWallEmptyFlag;
    private ArenaCellCoordinate imageGrid;

    public ExplorationAlgorithm(RobotModel robotModel, ArenaMemory arenaMemory){
        this.robotModel = robotModel;
        this.arenaMemory = arenaMemory;
        this.resume = true;
        arenaMemory.setStartZoneAsExplored();

        start =  new ArenaCellCoordinate(1, 1);
        subgoal = new ArenaCellCoordinate(13, 18);
        goal = new ArenaCellCoordinate(1, 1);
    }

    public boolean isRightWallEmptyFlag() {
        return rightWallEmptyFlag;
    }

    public ArenaCellCoordinate getImageGrid() {
        return imageGrid;
    }

    @Override
    public void run() {
        robotModel.startRobot();
        robotModel.waitForReadyState();

        while (!isGoalAchieved() && canPlay()){
            if(!subGoalAchieved){
                //use to detect if robot has passed the goal zone
                subGoalAchieved = robotModel.getRobotCenter().equals(subgoal);
             }

            boolean frontEmpty = robotModel.robotFrontSideEmpty();
            boolean rightEmpty = robotModel.robotRightSideEmpty();
            this.rightWallEmptyFlag = robotModel.rightMiddleEmpty();
            if(!this.rightWallEmptyFlag){
                try{
                    this.imageGrid = robotModel.getCoordinateOfCurrentDetection(RobotSensorPlacement.RIGHT_MIDDLE);
                }
                catch (OutOfGridException e){
                    this.imageGrid = null;
                }
            }

//            if(robotModel.calibrationCondtion()){
//                robotModel.callibrate();
//                robotModel.waitForReadyState();
//            }

            if(rightEmpty){
                if(wallOnRight){
                    robotModel.turnRightMoveOneAtomic();
                }
                else{
                    robotModel.turnRight();
                }
            }
            else{
                wallOnRight = true;
                if(frontEmpty){
                    robotModel.moveFrontOneStep();
                }
                else{
                    wallOnRight = false;
                    robotModel.turnLeft();
                }
            }
            robotModel.waitForReadyState();
        }
        setBackToNorth();
        System.out.println("Goal achieved!");
    }


    public void setBackToNorth(){
        while(robotModel.getCurrentDirection() != Direction.NORTH){
            if(robotModel.getCurrentDirection().getValue() > 2){
                robotModel.turnRight();
            }
            else{
                robotModel.turnLeft();
            }
            robotModel.waitForReadyState();
        }
    }


    /**
     * Check if robot has achieved subgoal and return back to original starting position
     * @return
     */
    public boolean isGoalAchieved(){
        return robotModel.getRobotCenter().equals(goal) && subGoalAchieved;
    }


    public synchronized void pauseAlgorithm(){
        this.resume = false;
        notifyAll();
    }

    synchronized public boolean canPlay(){
        while(!resume){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        notifyAll();
        return resume;
    }

    synchronized public void resumeAlgorithm(){
        this.resume = true;
        notifyAll();
    }



}
