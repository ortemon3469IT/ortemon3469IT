package nardgo;

import java.util.Arrays;

public class Board implements GameConstants {
    private final Player[] owners = new Player[POINT_COUNT];
    private final int[] counts = new int[POINT_COUNT];
    private final int[] bar = new int[Player.values().length];
    private final int[] borneOff = new int[Player.values().length];

    public Board() {
        reset();
    }

    public void reset() {
        Arrays.fill(owners, null);
        Arrays.fill(counts, 0);
        Arrays.fill(bar, 0);
        Arrays.fill(borneOff, 0);
        for (Player player : Player.values()) {
            for (int i = 0; i < INITIAL_POINTS.length; i++) {
                for (int k = 0; k < INITIAL_COUNTS[i]; k++) {
                    addChecker(player.mirror(INITIAL_POINTS[i]), player);
                }
            }
        }
    }

    public boolean isValidPoint(int point) {
        return point >= 0 && point < POINT_COUNT;
    }

    public Player getOwner(int point) {
        return isValidPoint(point) ? owners[point] : null;
    }

    public int getCount(int point) {
        return isValidPoint(point) ? counts[point] : 0;
    }

    public void addChecker(int point, Player player) {
        requirePoint(point);
        if (counts[point] > 0 && owners[point] != player) {
            throw new IllegalStateException("Point " + point + " is held by " + owners[point]);
        }
        owners[point] = player;
        counts[point]++;
    }

    public void removeChecker(int point) {
        requirePoint(point);
        if (counts[point] == 0) {
            throw new IllegalStateException("Point " + point + " is empty");
        }
        counts[point]--;
        if (counts[point] == 0) {
            owners[point] = null;
        }
    }

    public int getBarCount(Player player) {
        return bar[player.ordinal()];
    }

    public void addToBar(Player player) {
        bar[player.ordinal()]++;
    }

    public void removeFromBar(Player player) {
        if (bar[player.ordinal()] == 0) {
            throw new IllegalStateException("Bar is empty for " + player);
        }
        bar[player.ordinal()]--;
    }

    public int getBorneOffCount(Player player) {
        return borneOff[player.ordinal()];
    }

    public void addBorneOff(Player player) {
        borneOff[player.ordinal()]++;
    }

    private void requirePoint(int point) {
        if (!isValidPoint(point)) {
            throw new IllegalArgumentException("Point out of range: " + point);
        }
    }
}