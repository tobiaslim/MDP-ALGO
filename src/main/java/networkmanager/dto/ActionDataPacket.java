package networkmanager.dto;

import algorithm.constants.RobotAction;
import algorithm.models.ArenaCellCoordinate;

public class ActionDataPacket {
    public RobotAction action;
    public boolean photo;

    public ArenaCellCoordinate grid;

    public RobotAction getAction() {
        return action;
    }

    public void setAction(RobotAction action) {
        this.action = action;
    }

    public void setPhoto(boolean photo) {
        this.photo = photo;
    }

    public void setGrid(ArenaCellCoordinate grid) {
        this.grid = grid;
    }
}
