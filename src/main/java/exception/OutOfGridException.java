package exception;

import algorithm.models.ArenaCellCoordinate;

public class OutOfGridException extends RuntimeException{
    private String detailedMessage;
    public OutOfGridException(String message, ArenaCellCoordinate coordinate){
        detailedMessage = String.format("Coordinates X: %d, Y: %d is out of grid!", coordinate.getX(), coordinate.getY());
    }

    public String getDetailedMessage() {
        return detailedMessage;
    }
}
