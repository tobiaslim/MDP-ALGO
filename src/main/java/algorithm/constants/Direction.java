package algorithm.constants;

import java.util.HashMap;

public enum Direction {
    NORTH(0), EAST(1), SOUTH(2), WEST(3);


    private final int value;
    private static HashMap<Integer, Direction> values = new HashMap<>();

    static{
        for (Direction rd : values()){
            values.put(rd.getValue(), rd);
        }
    }

    Direction(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }

    public static Direction value(int  i){
        return values.get(i);
    }

}
