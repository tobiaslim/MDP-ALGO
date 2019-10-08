package algorithm.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum  RobotAction {
    START('S'), TURN_LEFT('L'), TURN_RIGHT('R'), MOVE_STRAIGHT('F'), SENSE('S');

    private final char value;


    RobotAction(char value) {
        this.value = value;
    }

    @JsonValue
    public char getValue() {
        return value;
    }
}
