package algorithm.constants;

public enum RobotSensorPlacement {
    FRONT_LEFT("FL"),
    FRONT_RIGHT("FR"),
    FRONT_CENTER("FC"),
    RIGHT_TOP("RT"),
    RIGHT_BOTTOM("RB"),
    LEFT_MIDDLE("LM"),

    //VIRTUAL
    RIGHT_MIDDLE("RM");

    private final String value;

    RobotSensorPlacement(String value){
        this.value = value;
    }
}
