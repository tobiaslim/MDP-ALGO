package algorithm;

import algorithm.constants.ArenaCellStatus;
import algorithm.constants.ArenaCellType;
import algorithm.contracts.MDFFormattable;
import algorithm.models.*;
import exception.OutOfGridException;
import simulator.Arena;
import utility.MDFFormatterUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ArenaMemory implements MDFFormattable {

    private List<ArenaCellModel> cells = Collections.synchronizedList(new ArrayList<>());

    public ArenaMemory(){
        int cellsLength = Arena.NO_OF_COLUMNS * Arena.NO_OF_ROWS;
        for(int i =0; i < cellsLength; i++){
            cells.add(new ArenaCellModel(i));
        }
    }

    public List<ArenaCellModel> getRowY(int Y){
        if(Y > Arena.NO_OF_ROWS-1){
            throw new RuntimeException("Exceeds number of row");
        }
        int start = Arena.NO_OF_COLUMNS * Y;
        int end = (Arena.NO_OF_COLUMNS * Y+1);
        return cells.subList(start, end);
    }

    public void setStartZoneAsExplored(){
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                getCellModelByCoordinates(new ArenaCellCoordinate(i, j)).setStatusAsExplored();
            }
        }
    }

    /**
     * Set grids that are start and goal zone
     */
    public void setStartZoneAndGoalZone(){
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                getCellModelByCoordinates(new ArenaCellCoordinate(i, j)).setCellTypeAsStartZone();
                getCellModelByCoordinates(new ArenaCellCoordinate((Arena.NO_OF_COLUMNS-1-j), (Arena.NO_OF_ROWS-1-i))).setCellTypeAsEndZone();
            }
        }
    }

    /**
     * Set surrounding as virtual walls. Should be called first.
     */
    public void setSurroundingAsVirtualWalls(){
        // set bottom and top surrounding
        for(int i = 0; i < Arena.NO_OF_COLUMNS; i++){
            getCellModelByArrayIndex(i).setVirtualWall();
            getCellModelByCoordinates(new ArenaCellCoordinate(i, Arena.NO_OF_ROWS-1)).setVirtualWall();
        }

        // set left and right surrounding
        for(int i = 0; i < Arena.NO_OF_ROWS; i++){
            getCellModelByCoordinates(new ArenaCellCoordinate(0, i)).setVirtualWall();
            getCellModelByCoordinates(new ArenaCellCoordinate(14, i)).setVirtualWall();
        }

    }

    /**
     * Set the surround cells of the passed in coordinates as virtual wall
     *
     * @param coordinates
     */
    private void setSurroundingGridAsVirtualWall(ArenaCellCoordinate coordinates){
        List<ArenaCellCoordinate> cellCoordinateList = new ArrayList<>();

        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                try{
                    cellCoordinateList.add(new ArenaCellCoordinate(coordinates.getX()+i, coordinates.getY()+j));
                }
                catch (OutOfGridException e){
//                    System.out.println(e.getDetailedMessage());
                }

            }
        }

        for (ArenaCellCoordinate c: cellCoordinateList) {
            ArenaCellModel acm = getCellModelByCoordinates(c);
            if(acm.getCellType() != ArenaCellType.BLOCK){
                acm.setVirtualWall();
            }
        }
    }

    /**
     * Retrieve a cell by coordinates
     *
     * @param coordinates
     * @return
     */
    public ArenaCellModel getCellModelByCoordinates(ArenaCellCoordinate coordinates){
        int x = coordinates.getX();
        int y = coordinates.getY();

        int index = (y * Arena.NO_OF_COLUMNS) + x;
        return getCellModelByArrayIndex(index);
    }

    /**
     * Load arena with MDF
     * @param mdfFormat
     */
    public void updateArenaWithMDF(MDFFormat mdfFormat){
        String explorationString = mdfFormat.getExplorationStatusString();
        String obstacleString = mdfFormat.getObstacleString();
        String[] explorationBin = explorationString.split("");
        String[] obstacleBin = obstacleString.split("");
        int explorationIndex = 2;
        int obstacleIndex = 0;
        for(int i = 0; i < explorationBin.length-4; i++){
            if(Integer.parseInt(explorationBin[explorationIndex]) == ArenaCellStatus.EXPLORED.getValue()){
                cells.get(i).setStatusAsExplored();

                if(obstacleBin[obstacleIndex].equals("1")){
                    cells.get(i).setCellTypeAsBlock();
                }
                else {
                    cells.get(i).setCellTypeAsEmpty();
                }

                obstacleIndex++;
            }
            explorationIndex++;
        }
    }


    public int getLength(){
        return cells.size();
    }

    public void setCellAsEmpty(int cellIndex){
        ArenaCellModel c = cells.get(cellIndex);
        if(c.getCellType() != ArenaCellType.START_ZONE && c.getCellType() != ArenaCellType.END_ZONE){
            c.setCellTypeAsEmpty();
        }
        c.setStatusAsExplored();
    }

    public void setCellAsEmpty(ArenaCellModel cellModel){
        setCellAsEmpty(cellModel.getIndex());
    }

    public void setCellAsEmpty(ArenaCellCoordinate cellCoordinate){
        setCellAsEmpty(cellCoordinate.calculateIndex());
    }

    public void setCellAsBlock(int cellIndex){
        ArenaCellModel c = cells.get(cellIndex);
        c.setStatusAsExplored();
        c.setCellTypeAsBlock();
        setSurroundingGridAsVirtualWall(c.getCoordinate());
    }

    public void setCellAsBlock(ArenaCellModel cellModel){
        setCellAsBlock(cellModel.getIndex());
    }

    public void setCellAsBlock(ArenaCellCoordinate coordinate){
        setCellAsBlock(coordinate.calculateIndex());
    }

    public ArenaCellModel getCellModelByArrayIndex(int index){
        return cells.get(index);
    }

    @Override
    public String getExploredStatusMdfString() {
        String mdf = "11";
        for (ArenaCellModel cell : cells){
            mdf+= cell.getCellStatus().getValue();
        }
        mdf +="11";
        return MDFFormatterUtility.convertBinaryStringToHexString(mdf);
    }

    @Override
    public String getObstacleStatusMdfString() {
        //get all known cells
        String mdf = cells.stream()
                .filter(acm->acm.getCellStatus() == ArenaCellStatus.EXPLORED)
                .map(arenaCellModel -> arenaCellModel.getCellType() == ArenaCellType.BLOCK ? "1" : "0")
                .collect(Collectors.joining());

        //find out how much bits needa be pad on the right
        int padCount = mdf.length() % 4;
        for(int i = 0; i < padCount; i++){
            mdf += "0";
        }
        //convert bitstring to hex string
        return MDFFormatterUtility.convertBinaryStringToHexString(mdf);
    }

    public long getNoOfExploredCells(){
        return cells.stream().filter(acm->acm.getCellStatus() == ArenaCellStatus.EXPLORED).count();
    }

    public List<ArenaCellModel> getAllObstacleCells(){
        return cells.stream().filter(acm->acm.getCellType() == ArenaCellType.BLOCK).collect(Collectors.toList());
    }

    public List<ArenaCellModel> getAllExploredAndEmpty(){
        return cells.stream().filter(acm-> !acm.isVirtualWall() &&
                acm.getCellStatus() == ArenaCellStatus.EXPLORED &&
                (acm.getCellType() == ArenaCellType.EMPTY ||
                        acm.getCellType() == ArenaCellType.START_ZONE ||
                        acm.getCellType() == ArenaCellType.END_ZONE)).collect(Collectors.toList());
    }

    public List<ArenaCellModel> getAllVirtualWall(){
        return cells.stream().filter(acm->acm.isVirtualWall()).collect(Collectors.toList());
    }

    public List<ArenaCellModel> getAllUnexploredCells(){
        return cells.stream().filter(acm->acm.getCellStatus()== ArenaCellStatus.UNEXPLORED).collect(Collectors.toList());
    }

    public List<ArenaCellCoordinate> getSurroundingCoordinates(ArenaCellCoordinate coordinates) {
        List<ArenaCellCoordinate> cellCoordinateList = new ArrayList<>();

        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                try{
                    cellCoordinateList.add(new ArenaCellCoordinate(coordinates.getX()+i, coordinates.getY()+j));
                }
                catch (OutOfGridException e){
//                    System.out.println(e.getDetailedMessage());
                }

            }
        }
        return cellCoordinateList;
    }

}