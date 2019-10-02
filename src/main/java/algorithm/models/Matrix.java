package algorithm.models;

public class Matrix {
    private int i;
    private int j;

    public Matrix(int i, int j){
        this.i = i;
        this.j = j;
    }

    public Matrix(Matrix oldMatrix, int scaleFactor){
        this.i = oldMatrix.getI();
        this.j = oldMatrix.getJ();
        this.scaleXandY(scaleFactor);
    }

    public void scaleXandY(int scaleFactor){
        this.i = i + (i * scaleFactor);
        this.j = j + (j * scaleFactor);
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }
}
