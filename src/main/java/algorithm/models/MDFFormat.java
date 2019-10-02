package algorithm.models;

import algorithm.contracts.MDFFormattable;
import utility.MDFFormatterUtility;

public class MDFFormat {
    private String explorationStatusString;
    private String obstacleString;

    public MDFFormat(String rawExplorationStatusString, String rawObstacleString){
        this.explorationStatusString = MDFFormatterUtility.convertHexStringToBinString(rawExplorationStatusString);
        this.obstacleString = MDFFormatterUtility.convertHexStringToBinString(rawObstacleString);
    }

    public MDFFormat(MDFFormattable formattable){
        this.explorationStatusString = formattable.getExploredStatusMdfString();
        this.obstacleString = formattable.getObstacleStatusMdfString();
    }

    public String getExplorationStatusString() {
        return this.explorationStatusString;
    }

    public String getObstacleString() {
        return this.obstacleString;
    }
}
