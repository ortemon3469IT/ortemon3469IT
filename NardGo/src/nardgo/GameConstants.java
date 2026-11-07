package nardgo;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

public interface GameConstants {
    int CENTER_DIVISOR = 2;

    String WINDOW_TITLE = "NARD-GO";
    int WINDOW_WIDTH = 1130;
    int WINDOW_HEIGHT = 720;

    int POINT_COUNT = 24;
    int POINTS_PER_ROW = 12;
    int POINTS_PER_QUADRANT = 6;
    int BAR_POINT = -2;
    int HOME_START = POINT_COUNT - POINTS_PER_QUADRANT;
    String STATUS_PICK_ENTRY = "Верните шашку с бара: выберите пункт входа";
    String STATUS_MUST_ENTER = "Сначала верните шашку с бара";
    String STATUS_HIT = "Удар! ";
    String STATUS_NO_MOVES = "Нет допустимых ходов. ";
    String STATUS_RUSH_ROLL = "Nard Rush: бросьте кубики ещё раз (Space)";
    String STATUS_RUSH_ALREADY = "Nard Rush уже активирован";
    String STATUS_WIN_SUFFIX = " победили!  N: новая игра";
    int CHECKERS_PER_PLAYER = 15;
    int DICE_COUNT = 2;
    int FIRST_DIE = 0;
    int SECOND_DIE = 1;
    int MIN_DIE_VALUE = 1;
    int DICE_SIDES = 6;
    int DOUBLE_MOVES = 4;
    int NO_POINT = -1;
    int[] INITIAL_POINTS = {0, 11, 16, 18};
    int[] INITIAL_COUNTS = {2, 5, 3, 5};

    int ENERGY_MAX = 100;
    int ENERGY_PER_HIT = 50;
    int ENERGY_RUSH_COST = 100;

    int FRAME_DELAY_MS = 16;
    int DICE_ROLL_FRAMES = 36;
    int DICE_FACE_CHANGE_FRAMES = 4;

    int BOARD_X = 40;
    int BOARD_Y = 70;
    int POINT_WIDTH = 65;
    int POINT_HEIGHT = 240;
    int POINT_CENTER_OFFSET = POINT_WIDTH / CENTER_DIVISOR;
    int BAR_WIDTH = 60;
    int MIDDLE_GAP = 60;
    int BOARD_FRAME = 12;
    int BOARD_ARC = 24;
    int POINT_COLOR_CYCLE = 2;
    int QUADRANT_WIDTH = POINTS_PER_QUADRANT * POINT_WIDTH;
    int BOARD_WIDTH = POINTS_PER_ROW * POINT_WIDTH + BAR_WIDTH;
    int BOARD_HEIGHT = 2 * POINT_HEIGHT + MIDDLE_GAP;
    int BOARD_OUTER_X = BOARD_X - BOARD_FRAME;
    int BOARD_OUTER_Y = BOARD_Y - BOARD_FRAME;
    int BOARD_OUTER_WIDTH = BOARD_WIDTH + 2 * BOARD_FRAME;
    int BOARD_OUTER_HEIGHT = BOARD_HEIGHT + 2 * BOARD_FRAME;
    int BOARD_CENTER_X = BOARD_X + BOARD_WIDTH / CENTER_DIVISOR;
    int BOARD_CENTER_Y = BOARD_Y + BOARD_HEIGHT / CENTER_DIVISOR;
    int BAR_X = BOARD_X + QUADRANT_WIDTH;
    int BOTTOM_ROW_BASE_Y = BOARD_Y + BOARD_HEIGHT;

    int CHECKER_DIAMETER = 56;
    int CHECKER_BORDER_WIDTH = 3;
    int CHECKER_X_OFFSET = (POINT_WIDTH - CHECKER_DIAMETER) / CENTER_DIVISOR;
    int BAR_CHECKER_OFFSET = (BAR_WIDTH - CHECKER_DIAMETER) / CENTER_DIVISOR;
    int SELECTION_STROKE_WIDTH = 4;
    int HOVER_STROKE_WIDTH = 2;

    int DICE_SIZE = 48;
    int DICE_GAP = 16;
    int DICE_ARC = 12;
    int DICE_GRID = 3;
    int DICE_CELL = DICE_SIZE / DICE_GRID;
    int DICE_CELL_CENTER = DICE_CELL / CENTER_DIVISOR;
    int DICE_PIP_DIAMETER = 10;
    int DICE_PIP_RADIUS = DICE_PIP_DIAMETER / CENTER_DIVISOR;
    int DICE_ROW_WIDTH = DICE_COUNT * DICE_SIZE + (DICE_COUNT - 1) * DICE_GAP;
    int DICE_AREA_X = BAR_X + BAR_WIDTH + (QUADRANT_WIDTH - DICE_ROW_WIDTH) / CENTER_DIVISOR;
    int DICE_AREA_Y = BOARD_Y + POINT_HEIGHT + (MIDDLE_GAP - DICE_SIZE) / CENTER_DIVISOR;
    int[][][] DICE_PIPS = {
            {{1, 1}},
            {{0, 0}, {2, 2}},
            {{0, 0}, {1, 1}, {2, 2}},
            {{0, 0}, {2, 0}, {0, 2}, {2, 2}},
            {{0, 0}, {2, 0}, {1, 1}, {0, 2}, {2, 2}},
            {{0, 0}, {2, 0}, {0, 1}, {2, 1}, {0, 2}, {2, 2}}
    };

    int HUD_MARGIN = 30;
    int HUD_X = BOARD_X + BOARD_WIDTH + HUD_MARGIN;
    int HUD_WIDTH = 180;
    int HUD_START_Y = BOARD_Y;
    int HUD_LINE_HEIGHT = 26;
    int HUD_BLOCK_STEP = 130;
    int ENERGY_BAR_HEIGHT = 14;
    int ENERGY_BAR_GAP = 8;
    int ENERGY_BAR_ARC = 8;
    int HINT_LINE_HEIGHT = 22;
    int HINT_TOP_GAP = 30;
    int TITLE_X = BOARD_X;
    int TITLE_Y = 48;
    int STATUS_OFFSET = 56;
    int STATUS_Y = BOARD_Y + BOARD_HEIGHT + STATUS_OFFSET;

    Color BACKGROUND_TOP = new Color(0x14183A);
    Color BACKGROUND_BOTTOM = new Color(0x07081A);
    Color BOARD_FRAME_COLOR = new Color(0x2B2160);
    Color BOARD_SURFACE_COLOR = new Color(0x181B3F);
    Color BAR_COLOR = new Color(0x2B2160);
    Color POINT_COLOR_A = new Color(0x1F8A99);
    Color POINT_COLOR_B = new Color(0x8E2F6B);
    Color WHITE_CHECKER_FILL = new Color(0xF4EBD0);
    Color WHITE_CHECKER_BORDER = new Color(0xBFB38A);
    Color BLACK_CHECKER_FILL = new Color(0x15152A);
    Color BLACK_CHECKER_BORDER = new Color(0x7CF2FF);
    Color SELECTION_COLOR = new Color(0xFFD54F);
    Color HOVER_COLOR = new Color(0x9FE8F0);
    Color DICE_COLOR = new Color(0xF4EBD0);
    Color DICE_PIP_COLOR = new Color(0x15152A);
    Color TEXT_COLOR = new Color(0xE6E9FF);
    Color ACCENT_COLOR = new Color(0xFFD54F);
    Color ENERGY_BACK_COLOR = new Color(0x2B2160);
    Color ENERGY_FILL_COLOR = new Color(0x7CF2FF);

    Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 30);
    Font HUD_FONT = new Font("SansSerif", Font.BOLD, 16);
    Font STATUS_FONT = new Font("SansSerif", Font.BOLD, 20);
    Font HINT_FONT = new Font("SansSerif", Font.PLAIN, 14);

    int KEY_NEW_GAME = KeyEvent.VK_N;
    int KEY_ROLL_DICE = KeyEvent.VK_SPACE;
    int KEY_EXIT = KeyEvent.VK_ESCAPE;
    int KEY_RUSH = KeyEvent.VK_E;

    String LABEL_BORNE_OFF = "Выброшено: ";
    String LABEL_ENERGY = "Энергия: ";
    String LABEL_MOVES = "Ходы: ";
    String CURRENT_MARKER = "> ";
    String NO_MARKER = "";
    String[] CONTROL_HINTS = {
            "Управление",
            "Мышь: выбор и ход",
            "Повторный клик: выброс",
            "Space: бросок кубиков",
            "N: новая игра",
            "E: Nard Rush",
            "Esc: выход"
    };

    String STATUS_ROLL_PROMPT = "Бросьте кубики (Space)";
    String STATUS_ROLLING = "Кубики катятся...";
    String STATUS_PICK_CHECKER = "Выберите свою шашку";
    String STATUS_PICK_TARGET = "Выберите пункт назначения";
    String STATUS_NOT_YOUR_CHECKER = "Это не ваша шашка";
    String STATUS_ILLEGAL_MOVE = "Недопустимый ход";
    String STATUS_ENERGY_LOW = "Недостаточно энергии";
    String STATUS_RUSH_ACTIVATED = "Nard Rush: дополнительный ход";
}