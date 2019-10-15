package algorithm;

import algorithm.contracts.AlgorithmContract;
import algorithm.models.ArenaCellCoordinate;
import algorithm.models.ArenaCellModel;
import algorithm.models.RobotModel;
import simulator.Arena;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class FastestPathAlgorithm implements AlgorithmContract {
    RobotModel robotModel;
    ArenaMemory arenaMemory;
    boolean resume;
    ArenaCellCoordinate goal;
    ArenaCellCoordinate subgoal;
    ArenaCellCoordinate start;
    int delay;
    

    public FastestPathAlgorithm(RobotModel robotModel, ArenaMemory arenaMemory, ArenaCellCoordinate aCord){
        this.robotModel = robotModel;
        this.arenaMemory = arenaMemory;
        this.resume = true;
        delay = 0;

        start =  new ArenaCellCoordinate(1, 1);
        subgoal = aCord;
        goal = new ArenaCellCoordinate(13, 18);
    }

    @Override
    public void run() {
        Node initialNode = new Node(start.getY(), start.getX());
        Node waypointNode = new Node(subgoal.getY(), subgoal.getX());
        Node finalNode = new Node(goal.getY(), goal.getX());
        int rows = 20;
        int cols = 15;
        AStar aStar = new AStar(rows, cols, initialNode, waypointNode);

        // Set obstacle cells
        List<ArenaCellModel> listObstacles = arenaMemory.getAllObstacleCells();
        for(ArenaCellModel a: listObstacles){
            int x = a.getCoordinate().getX();
            int y = a.getCoordinate().getY();
            aStar.setBlock(y,x);
            System.out.println("Block set at row: "+y+", col: "+x);
        }

        // Set Virtual Cells as blocked
        List<ArenaCellModel> listVirtual = arenaMemory.getAllVirtualWall();
        for(ArenaCellModel a: listVirtual){
            int x = a.getCoordinate().getX();
            int y = a.getCoordinate().getY();
            aStar.setBlock(y,x);
            System.out.println("Virtual Block set at row: "+y+", col: "+x);
        }

        // Set edges as Virtual Cells. Blocked.
        for(int i=0;i<15;i++){
            aStar.setBlock(0,i);
            System.out.println("Side Block set at row: "+0+", col: "+i);
            aStar.setBlock(19,i);
            System.out.println("Side Block set at row: "+19+", col: "+i);
        }
        for(int i=1;i<19;i++){
            aStar.setBlock(i,0);
            System.out.println("Side Block set at row: "+i+", col: "+0);
            aStar.setBlock(i,14);
            System.out.println("Side Block set at row: "+i+", col: "+14);
        }

        // Set unexplored cells as blocked
        List<ArenaCellModel> listUnexplored = arenaMemory.getAllUnexploredCells();
        for(ArenaCellModel a: listUnexplored){
            int x = a.getCoordinate().getX();
            int y = a.getCoordinate().getY();
            aStar.setBlock(y,x);
            List<ArenaCellCoordinate> surroundings = arenaMemory.getSurroundingCoordinates(a.getCoordinate());
            for(ArenaCellCoordinate avs: surroundings){
                aStar.setBlock(avs.getY(),avs.getX());
            }
            System.out.println("Unexplored Block set at row: "+y+", col: "+x);
        }

        PriorityQueue<Node> openListTemp = aStar.getOpenList();
        Set<Node> closedSetTemp = aStar.getClosedSet();

        List<Node> path = aStar.findPath();
        // Print out the fastest path
        for (Node node : path) {
            System.out.println(node);
        }
        System.out.println("Fastest path started");
        moveRobot(path,aStar, new ArenaCellCoordinate(1,1));
        System.out.println("Reached waypoint");

        aStar.setInitialNode(waypointNode);
        aStar.setFinalNode(finalNode);
        aStar.clearLists();
        List<Node> path2 = aStar.findPath();
        for (Node node : path2) {
            System.out.println(node);
        }
        System.out.println("Fastest path 2 started");
        ArenaCellCoordinate ac = new ArenaCellCoordinate(subgoal.getX(),subgoal.getY());
        moveRobot(path2,aStar, ac);
        System.out.println("Reached Goal");

    }
    // Move Robot using a chain of commands
    public void moveRobot(List<Node> path, AStar aStar, ArenaCellCoordinate starts){
        int curX = starts.getX();
        int curY = starts.getY();
        Node[][] sa = aStar.getSearchArea();
        for(Node node : path){

            System.out.println("Moving to:"+ node.getCol()+", "+node.getRow());
            if (node.getRow()==curY && node.getCol()==curX){
            }
            // same row
            else if(node.getRow()==curY && node.getCol()!=curX){
                if(node.getCol()-curX==1){
                    try{
                        Thread.sleep(delay);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    robotModel.moveEast();
                }
                else if (node.getCol()-curX==-1){
                    try{
                        Thread.sleep(delay);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    robotModel.moveWest();
                }
            }
            // same column
            else if(node.getCol()==curX && node.getRow()!=curY){
                if(node.getRow()-curY==1){
                    try{
                        Thread.sleep(delay);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    robotModel.moveNorth();
                }
                else if(node.getRow()-curY==-1){
                    try{
                        Thread.sleep(delay);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    robotModel.moveSouth();
                }
            }
            // up right diagonal
            else if(node.getCol()-1==curX && node.getRow()-1==curY){
                try{
                    Thread.sleep(delay);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                if(sa[curY+1][curX].isBlock()){
                    robotModel.moveEast();
                    try{
                        Thread.sleep(delay);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    robotModel.moveNorth();
                }
                else{
                    robotModel.moveNorth();
                    try{
                        Thread.sleep(delay);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    robotModel.moveEast();
                }
            }
            // up left diagonal
            else if(node.getCol()+1==curX && node.getRow()-1==curY){
                try{
                    Thread.sleep(delay);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                if(sa[curY+1][curX].isBlock()){
                    robotModel.moveWest();
                    try{
                        Thread.sleep(delay);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    robotModel.moveNorth();
                }
                else{
                    robotModel.moveNorth();
                    try{
                        Thread.sleep(delay);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    robotModel.moveWest();
                }

            }

            // bottom left
            else if (node.getCol()+1==curX && node.getRow()+1==curY){
                robotModel.moveWest();
                robotModel.moveSouth();
            }
            // bottom right
            else if (node.getCol()+1==curX && node.getRow()-1==curY){
                robotModel.moveEast();
                robotModel.moveSouth();
            }
            //Uncomment this to move robot within simulation
            //curX = robotModel.getRobotCenter().getX();
            //curY = robotModel.getRobotCenter().getY();
            curX = node.getCol();
            curY = node.getRow();
        }
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

    public RobotModel getRobotModel() {
        return robotModel;
    }
}
