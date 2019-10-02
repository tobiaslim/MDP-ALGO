package algorithm.constants;

import algorithm.models.SensorValueMapping;

import java.util.HashMap;

public enum Zone {
    A(1),B(2),C(3),D(4),E(5),F(6);
    private final int value;

    private static HashMap<Integer, Zone> values = new HashMap<>();

    static{
        for (Zone rd : values()){
            values.put(rd.getValue(), rd);
        }
    }

    public int getValue() { return value; }

    Zone(int value){
        this.value = value;
    }

    public static Zone value(int  i){
        return values.get(i);
    }

}
