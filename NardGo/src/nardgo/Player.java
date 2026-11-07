package nardgo;

public enum Player implements GameConstants {
    WHITE("Белые"),
    BLACK("Чёрные");

    private final String label;

    Player(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Player opposite() {
        return this == WHITE ? BLACK : WHITE;
    }

    public int mirror(int whitePoint) {
        return this == WHITE ? whitePoint : POINT_COUNT - 1 - whitePoint;
    }

    public int entryPoint(int die) {
        return mirror(die - MIN_DIE_VALUE);
    }

    public boolean isHome(int point) {
        return mirror(point) >= POINT_COUNT - POINTS_PER_QUADRANT;
    }
}
