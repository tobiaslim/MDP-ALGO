package algorithm;

import algorithm.contracts.AlgorithmContract;
import algorithm.models.RobotModel;

public class ExplorationAlgorithm implements AlgorithmContract {
    RobotModel robotModel;
    ArenaMemory arenaMemory;
    boolean resume;
    boolean wallOnRight = false;

    public ExplorationAlgorithm(RobotModel robotModel, ArenaMemory arenaMemory){
        this.robotModel = robotModel;
        this.arenaMemory = arenaMemory;
        this.resume = true;
        arenaMemory.setStartZoneAsExplored();
    }

    @Override
    public void run() {
        robotModel.startRobot();
        robotModel.waitForReadyState();

        while (true && canPlay()){
            boolean frontEmpty = robotModel.robotFrontSideEmpty();
            boolean rightEmpty = robotModel.robotRightSideEmpty();
            if(rightEmpty){
                if(wallOnRight){
                    turnRightMoveOne();
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
            try{
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            robotModel.waitForReadyState();
        }
    }

    public void turnRightMoveOne(){
        robotModel.turnRight();
        robotModel.waitForReadyState();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(robotModel.robotFrontSideEmpty()){
            robotModel.moveFrontOneStep();
        }
    }


    public synchronized  void pauseAlgorithm(){
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
