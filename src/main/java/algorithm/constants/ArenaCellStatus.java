package algorithm.constants;

public enum ArenaCellStatus{
    EXPLORED(1), UNEXPLORED(0), UNSURE(2);

    private final int value;

    ArenaCellStatus(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
