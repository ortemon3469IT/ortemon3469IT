package nardgo;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;

public class GameRenderer extends JPanel implements GameConstants {
    private final GameEngine engine;
    private volatile int hoverPoint = NO_POINT;

    public GameRenderer(GameEngine engine) {
        this.engine = engine;
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setFocusable(true);
    }

    public void setHoverPoint(int hoverPoint) {
        this.hoverPoint = hoverPoint;
    }

    public int getHoverPoint() {
        return hoverPoint;
    }

    public int pointAt(Point location) {
        for (int point = 0; point < POINT_COUNT; point++) {
            int x = pointX(point);
            int top = isTopRow(point) ? BOARD_Y : BOTTOM_ROW_BASE_Y - POINT_HEIGHT;
            boolean insideX = location.x >= x && location.x < x + POINT_WIDTH;
            boolean insideY = location.y >= top && location.y < top + POINT_HEIGHT;
            if (insideX && insideY) {
                return point;
            }
        }
        return NO_POINT;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            drawBackground(g2);
            drawBoard(g2);
            drawPoints(g2);
            drawCheckers(g2);
            drawBar(g2);
            drawDice(g2);
            drawHud(g2);
            drawStatus(g2);
        } finally {
            g2.dispose();
        }
    }

    private void drawBackground(Graphics2D g2) {
        g2.setPaint(new GradientPaint(0, 0, BACKGROUND_TOP, 0, WINDOW_HEIGHT, BACKGROUND_BOTTOM));
        g2.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        g2.setFont(TITLE_FONT);
        g2.setColor(ACCENT_COLOR);
        g2.drawString(WINDOW_TITLE, TITLE_X, TITLE_Y);
    }

    private void drawBoard(Graphics2D g2) {
        g2.setColor(BOARD_FRAME_COLOR);
        g2.fillRoundRect(BOARD_OUTER_X, BOARD_OUTER_Y, BOARD_OUTER_WIDTH, BOARD_OUTER_HEIGHT, BOARD_ARC, BOARD_ARC);
        g2.setColor(BOARD_SURFACE_COLOR);
        g2.fillRect(BOARD_X, BOARD_Y, BOARD_WIDTH, BOARD_HEIGHT);
        g2.setColor(BAR_COLOR);
        g2.fillRect(BAR_X, BOARD_Y, BAR_WIDTH, BOARD_HEIGHT);
    }

    private void drawPoints(Graphics2D g2) {
        int selected = engine.getSelectedPoint();
        for (int point = 0; point < POINT_COUNT; point++) {
            Polygon shape = pointShape(point);
            g2.setColor(columnOf(point) % POINT_COLOR_CYCLE == 0 ? POINT_COLOR_A : POINT_COLOR_B);
            g2.fillPolygon(shape);
            if (point == selected) {
                drawOutline(g2, shape, SELECTION_COLOR, SELECTION_STROKE_WIDTH);
            } else if (point == hoverPoint) {
                drawOutline(g2, shape, HOVER_COLOR, HOVER_STROKE_WIDTH);
            }
        }
    }

    private void drawOutline(Graphics2D g2, Polygon shape, Color color, int width) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(width));
        g2.drawPolygon(shape);
        g2.setStroke(new BasicStroke());
    }

    private void drawCheckers(Graphics2D g2) {
        Board board = engine.getBoard();
        for (int point = 0; point < POINT_COUNT; point++) {
            int count = board.getCount(point);
            if (count == 0) {
                continue;
            }
            int spacing = stackSpacing(count);
            int x = pointX(point) + CHECKER_X_OFFSET;
            boolean top = isTopRow(point);
            for (int k = 0; k < count; k++) {
                int y = top
                        ? BOARD_Y + k * spacing
                        : BOTTOM_ROW_BASE_Y - CHECKER_DIAMETER - k * spacing;
                drawChecker(g2, board.getOwner(point), x, y);
            }
        }
    }

    private void drawBar(Graphics2D g2) {
        Board board = engine.getBoard();
        int x = BAR_X + BAR_CHECKER_OFFSET;
        for (Player player : Player.values()) {
            int count = board.getBarCount(player);
            int spacing = stackSpacing(count);
            for (int k = 0; k < count; k++) {
                int y = player == Player.WHITE
                        ? BOARD_CENTER_Y + k * spacing
                        : BOARD_CENTER_Y - CHECKER_DIAMETER - k * spacing;
                drawChecker(g2, player, x, y);
            }
        }
    }

    private void drawChecker(Graphics2D g2, Player player, int x, int y) {
        g2.setColor(player == Player.WHITE ? WHITE_CHECKER_FILL : BLACK_CHECKER_FILL);
        g2.fillOval(x, y, CHECKER_DIAMETER, CHECKER_DIAMETER);
        g2.setColor(player == Player.WHITE ? WHITE_CHECKER_BORDER : BLACK_CHECKER_BORDER);
        g2.setStroke(new BasicStroke(CHECKER_BORDER_WIDTH));
        g2.drawOval(x, y, CHECKER_DIAMETER, CHECKER_DIAMETER);
        g2.setStroke(new BasicStroke());
    }

    private void drawDice(Graphics2D g2) {
        for (int i = 0; i < DICE_COUNT; i++) {
            int x = DICE_AREA_X + i * (DICE_SIZE + DICE_GAP);
            g2.setColor(DICE_COLOR);
            g2.fillRoundRect(x, DICE_AREA_Y, DICE_SIZE, DICE_SIZE, DICE_ARC, DICE_ARC);
            g2.setColor(DICE_PIP_COLOR);
            int value = engine.getDieDisplayValue(i);
            for (int[] cell : DICE_PIPS[value - MIN_DIE_VALUE]) {
                int column = cell[0];
                int row = cell[1];
                int centerX = x + column * DICE_CELL + DICE_CELL_CENTER;
                int centerY = DICE_AREA_Y + row * DICE_CELL + DICE_CELL_CENTER;
                g2.fillOval(centerX - DICE_PIP_RADIUS, centerY - DICE_PIP_RADIUS,
                        DICE_PIP_DIAMETER, DICE_PIP_DIAMETER);
            }
        }
    }

    private void drawHud(Graphics2D g2) {
        int y = HUD_START_Y;
        for (Player player : Player.values()) {
            drawPlayerBlock(g2, player, y);
            y += HUD_BLOCK_STEP;
        }
        drawRemainingMoves(g2, y);
        drawHints(g2, y + HUD_LINE_HEIGHT + HINT_TOP_GAP);
    }

    private void drawPlayerBlock(Graphics2D g2, Player player, int y) {
        boolean active = player == engine.getCurrentPlayer();
        g2.setFont(HUD_FONT);
        g2.setColor(active ? ACCENT_COLOR : TEXT_COLOR);
        g2.drawString((active ? CURRENT_MARKER : NO_MARKER) + player.getLabel(), HUD_X, y);

        g2.setColor(TEXT_COLOR);
        int scoreY = y + HUD_LINE_HEIGHT;
        g2.drawString(LABEL_BORNE_OFF + engine.getBoard().getBorneOffCount(player)
                + "/" + CHECKERS_PER_PLAYER, HUD_X, scoreY);

        int energyY = scoreY + HUD_LINE_HEIGHT;
        int energy = engine.getEnergy(player);
        g2.drawString(LABEL_ENERGY + energy + "/" + ENERGY_MAX, HUD_X, energyY);
        drawEnergyBar(g2, energy, energyY + ENERGY_BAR_GAP);
    }

    private void drawEnergyBar(Graphics2D g2, int energy, int y) {
        g2.setColor(ENERGY_BACK_COLOR);
        g2.fillRoundRect(HUD_X, y, HUD_WIDTH, ENERGY_BAR_HEIGHT, ENERGY_BAR_ARC, ENERGY_BAR_ARC);
        g2.setColor(ENERGY_FILL_COLOR);
        int filled = HUD_WIDTH * energy / ENERGY_MAX;
        g2.fillRoundRect(HUD_X, y, filled, ENERGY_BAR_HEIGHT, ENERGY_BAR_ARC, ENERGY_BAR_ARC);
    }

    private void drawRemainingMoves(Graphics2D g2, int y) {
        StringBuilder text = new StringBuilder(LABEL_MOVES);
        for (int move : engine.getRemainingMoves()) {
            text.append(move).append(' ');
        }
        g2.setFont(HUD_FONT);
        g2.setColor(TEXT_COLOR);
        g2.drawString(text.toString(), HUD_X, y);
    }

    private void drawHints(Graphics2D g2, int y) {
        g2.setFont(HINT_FONT);
        for (int i = 0; i < CONTROL_HINTS.length; i++) {
            g2.setColor(i == 0 ? ACCENT_COLOR : TEXT_COLOR);
            g2.drawString(CONTROL_HINTS[i], HUD_X, y + i * HINT_LINE_HEIGHT);
        }
    }

    private void drawStatus(Graphics2D g2) {
        g2.setFont(STATUS_FONT);
        g2.setColor(ACCENT_COLOR);
        drawCenteredString(g2, engine.getStatusMessage(), BOARD_CENTER_X, STATUS_Y);
    }

    private void drawCenteredString(Graphics2D g2, String text, int centerX, int baselineY) {
        FontMetrics metrics = g2.getFontMetrics();
        g2.drawString(text, centerX - metrics.stringWidth(text) / CENTER_DIVISOR, baselineY);
    }

    private boolean isTopRow(int point) {
        return point >= POINTS_PER_ROW;
    }

    private int columnOf(int point) {
        return isTopRow(point) ? point - POINTS_PER_ROW : POINTS_PER_ROW - 1 - point;
    }

    private int pointX(int point) {
        int column = columnOf(point);
        int barShift = column >= POINTS_PER_QUADRANT ? BAR_WIDTH : 0;
        return BOARD_X + column * POINT_WIDTH + barShift;
    }

    private Polygon pointShape(int point) {
        int x = pointX(point);
        boolean top = isTopRow(point);
        int baseY = top ? BOARD_Y : BOTTOM_ROW_BASE_Y;
        int tipY = top ? BOARD_Y + POINT_HEIGHT : BOTTOM_ROW_BASE_Y - POINT_HEIGHT;
        Polygon shape = new Polygon();
        shape.addPoint(x, baseY);
        shape.addPoint(x + POINT_WIDTH, baseY);
        shape.addPoint(x + POINT_CENTER_OFFSET, tipY);
        return shape;
    }

    private int stackSpacing(int count) {
        if (count * CHECKER_DIAMETER <= POINT_HEIGHT) {
            return CHECKER_DIAMETER;
        }
        return (POINT_HEIGHT - CHECKER_DIAMETER) / (count - 1);
    }
}