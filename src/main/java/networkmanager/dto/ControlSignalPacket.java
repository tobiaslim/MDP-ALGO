package networkmanager.dto;

public class ControlSignalPacket {
    public enum RobotSignal{
        START_EXPLORE, START_FASTEST
    }
    private RobotSignal action;

    public RobotSignal getAction() {
        return action;
    }

    public void setAction(RobotSignal action){
        this.action = action;
    }
}
