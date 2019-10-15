package networkmanager.dto;

import algorithm.constants.RobotAction;

import java.util.List;

public class FastestPathList {
    public List<RobotAction> actions;

    public void setActions(List<RobotAction> action) {
        this.actions = action;
    }

    public List<RobotAction> getActions(){
        return this.actions;
    }
}
