package networkmanager.dto;

public class SensorInfoPacket {
    public int success;
    public int FL;
    public int FC;
    public int FR;
    public int RT;
    public int RB;
    public int LM;

    public int getFL() {
        return FL;
    }

    public void setFL(int FL) {
        this.FL = FL;
    }

    public int getFC() {
        return FC;
    }

    public void setFC(int FC) {
        this.FC = FC;
    }

    public int getFR() {
        return FR;
    }

    public void setFR(int FR) {
        this.FR = FR;
    }

    public int getRT() {
        return RT;
    }

    public void setRT(int RT) {
        this.RT = RT;
    }

    public int getRB() {
        return RB;
    }

    public void setRB(int RB) {
        this.RB = RB;
    }

    public int getLM() {
        return LM;
    }

    public void setLM(int LM) {
        this.LM = LM;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }
}
