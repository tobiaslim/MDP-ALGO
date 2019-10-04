package algorithm.models;

import exception.OutOfGridException;
import simulator.Arena;

public class ArenaCellCoordinate {
    private int x;
    private int y;

    public ArenaCellCoordinate(int x, int y) throws OutOfGridException {
        this.x = x;
        this.y = y;

        if(y > Arena.NO_OF_ROWS-1 || x > Arena.NO_OF_COLUMNS-1 || x < 0 || y < 0 ){
            throw new OutOfGridException("Out of grid", this);
        }
    }

    public ArenaCellCoordinate(ArenaCellCoordinate oldArenaCellCoordinate, Matrix matrix){
        this.x = oldArenaCellCoordinate.getX();
        this.y = oldArenaCellCoordinate.getY();
        this.addMatrixValue(matrix);
    }

    public int calculateIndex(){
        return (y*15) + x;
    }

    public void setX(int x) {
        this.x = x;

        if(x > Arena.NO_OF_COLUMNS-1 || x < 0){
            throw new OutOfGridException("Out of grid", this);
        }
    }

    public void setY(int y) {
        this.y = y;

        if(y > Arena.NO_OF_ROWS-1 || y < 0){
            throw new OutOfGridException("Out of grid", this);
        }
    }

    public int getX() {
        return x;
    }

    public int getY(){
        return y;
    }

    public ArenaCellCoordinate addMatrixValue(Matrix matrix){
        this.setX(x + matrix.getI());
        this.setY(y + matrix.getJ());
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ArenaCellCoordinate){
            ArenaCellCoordinate compare = (ArenaCellCoordinate) obj;
            return this.x == compare.x && this.y == compare.y;
        }
        return false;
    }
}