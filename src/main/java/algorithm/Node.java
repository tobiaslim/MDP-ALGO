package algorithm;

public class Node {

    private int g;
    private int f;
    private int h;
    private int y;
    private int x;
    private boolean isBlock;
    private Node parent;

    public Node(int y, int x) {
        super();
        this.y = y;
        this.x = x;
    }
    // Heuristic can be changed.
    public void calculateHeuristic(Node finalNode) {
        this.h = Math.abs(finalNode.getY() - getY()) + Math.abs(finalNode.getX() - getX());
    }

    public void setNodeData(Node currentNode, int cost) {
        int gCost = currentNode.getG() + cost;
        setParent(currentNode);
        setG(gCost);
        calculateFinalCost();
    }

    public boolean checkBetterPath(Node currentNode, int cost) {
        int gCost = currentNode.getG() + cost;
        if (gCost < getG()) {
            setNodeData(currentNode, cost);
            return true;
        }
        return false;
    }

    private void calculateFinalCost() {
        int finalCost = getG() + getH();
        setF(finalCost);
    }

    @Override
    public boolean equals(Object obj) {
        Node node2 = (Node) obj;
        return this.getY() == node2.getY() && this.getX() == node2.getX();
    }

    @Override
    public String toString() {
        return "{Node x:" + x + ", y:" + y + "}";
    }


    // Getter and Setter

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
}
