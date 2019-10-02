package algorithm.models;

import algorithm.constants.Zone;

public class SensorValueMapping {

    private Zone zone;
    private int startRange;
    private int endRange;

    public SensorValueMapping(Zone zone, int startRange, int endRange){
        this.zone = zone;
        this.startRange = startRange;
        this.endRange = endRange;
    }

    public boolean isValueWithinRange(int sensorValue){
        return (sensorValue >= startRange) && (sensorValue <= endRange);
    }

    public Zone getZone() {
        return zone;
    }

    public int getStartRange() {
        return startRange;
    }

    public int getEndRange() {
        return endRange;
    }
}
