package networkmanager.dto;

import algorithm.constants.RobotAction;

import java.util.List;

public class FastestPathList {
    public List<String> actions;

    public void setActions(List<String> action) {
        this.actions = action;
    }

    public List<String> getActions(){
        return this.actions;
    }
}
