package algorithm.models;

import algorithm.constants.ArenaCellStatus;
import algorithm.constants.ArenaCellType;
import algorithm.contracts.ArenaCellSubscriber;
import simulator.Arena;

public class ArenaCellModel {
    ArenaCellStatus arenaCellStatus;
    ArenaCellType arenaCellType;
    ArenaCellSubscriber cellSubscriber;
    private boolean isVirtualWall;
    boolean isWaypoint;
    int index;
    private boolean setByLeftRange;

    public ArenaCellCoordinate getCoordinate() {
        return coordinate;
    }

    ArenaCellCoordinate coordinate;

    public ArenaCellModel(int index){
        this.arenaCellType = ArenaCellType.UNKNOWN;
        this.arenaCellStatus = ArenaCellStatus.UNEXPLORED;
        this.index = index;
        this.coordinate = calculateCoordinatesByArrayIndex(index);
        this.isVirtualWall = false;
        this.setByLeftRange = false;
    }

    public int getIndex(){
        return this.index;
    }

    /**
     * Method to call for attaching subscriber to this model
     * @param subscriber
     */
    public void subscribe(ArenaCellSubscriber subscriber){
        this.cellSubscriber = subscriber;
    }

    /**
     * Get coordinates based on index
     */
    public static ArenaCellCoordinate calculateCoordinatesByArrayIndex(int index){
        int x = index % Arena.NO_OF_COLUMNS;
        int y = index / Arena.NO_OF_COLUMNS;
        return new ArenaCellCoordinate(x,y);
    }

    public boolean isVirtualWall(){
        return this.isVirtualWall;
    }

    public void setVirtualWall(){
        this.isVirtualWall = true;
    }

    /**
     * Call to notify subscriber
     */
    public void notifySubscriber(){
        if(this.cellSubscriber != null) this.cellSubscriber.onUpdate();
    }

    /**
     * Set cell status
     */
    public void setStatusAsExplored(){
        this.setCellStatus(ArenaCellStatus.EXPLORED);
    }

    public void setStatusAsUnexplored(){
        this.setCellStatus(ArenaCellStatus.UNEXPLORED);
    }

    public boolean isSetByLeftRange() {
        return setByLeftRange;
    }

    public void setByLeftRange(boolean setByLeftRange) {
        this.setByLeftRange = setByLeftRange;
    }

    /**
     * set cell types
     */
    public void setCellTypeAsBlock(){
        this.isVirtualWall = false;
        this.setCellType(ArenaCellType.BLOCK);
    }



    public void setCellTypeAsEmpty(){
        this.setCellType(ArenaCellType.EMPTY);
    }

    public void setCellTypeAsStartZone(){
        this.setCellType(ArenaCellType.START_ZONE);
    }

    public void setCellTypeAsEndZone(){
        this.setCellType(ArenaCellType.END_ZONE);
    }

    public void setCellTypeAsUnknown(){
        this.setCellType(ArenaCellType.UNKNOWN);
    }


    /**
     * Updates cell status primitive method. Use this method to
     * update cell status as it wraps notify subscriber
     * @param cellStatus
     */
    private void setCellStatus(ArenaCellStatus cellStatus) {
        this.arenaCellStatus = cellStatus;
        this.notifySubscriber();
    }

    /**
     * Updates cell type primitive method. Use this method to
     * update cell type as it wraps notify subscriber
     * @param cellType
     */
    private void setCellType(ArenaCellType cellType) {
        this.arenaCellType = cellType;
        this.notifySubscriber();
    }

    public ArenaCellStatus getCellStatus() {
        return this.arenaCellStatus;
    }

    public ArenaCellType getCellType() {
        return this.arenaCellType;
    }

    public ArenaCellSubscriber getCellSubscriber() {
        return cellSubscriber;
    }

    public boolean isWaypoint() {
        return isWaypoint;
    }
    public void setWaypoint() {
        isWaypoint = true;
    }
    public void removeWaypoint() {
        isWaypoint = false;
    }
}
