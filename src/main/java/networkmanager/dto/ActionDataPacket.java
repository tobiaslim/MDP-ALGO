package networkmanager.dto;

import algorithm.constants.RobotAction;

public class ActionDataPacket {
    public RobotAction action;

    public RobotAction getAction() {
        return action;
    }

    public void setAction(RobotAction action) {
        this.action = action;
    }
}
