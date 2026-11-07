package nardgo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class GameEngine implements GameConstants {
    private static final String MSG_BACKWARD = "Ходить можно только вперёд";
    private static final String MSG_NO_DIE = "Нет кубика на такое расстояние";
    private static final String MSG_BLOCKED = "Пункт занят шашками соперника";

    private final Board board;
    private final Dice dice = new Dice();
    private final List<Integer> remainingMoves = new ArrayList<>();
    private final int[] energy = new int[Player.values().length];
    private Player currentPlayer = Player.WHITE;
    private GameState state = GameState.WAITING_ROLL;
    private int selectedPoint = NO_POINT;
    private int bonusTurns;
    private String statusMessage = STATUS_ROLL_PROMPT;

    public GameEngine(Board board) {
        this.board = board;
    }

    public synchronized void newGame() {
        board.reset();
        dice.reset();
        remainingMoves.clear();
        Arrays.fill(energy, 0);
        currentPlayer = Player.WHITE;
        state = GameState.WAITING_ROLL;
        selectedPoint = NO_POINT;
        bonusTurns = 0;
        statusMessage = STATUS_ROLL_PROMPT;
    }

    public synchronized void update() {
        boolean wasRolling = dice.isRolling();
        dice.tick();
        if (wasRolling && !dice.isRolling() && state == GameState.WAITING_MOVE) {
            if (hasAnyMove()) {
                statusMessage = promptStatus();
            } else {
                endTurn(STATUS_NO_MOVES);
            }
        }
    }

    public synchronized void rollDice() {
        if (state != GameState.WAITING_ROLL) {
            return;
        }
        dice.roll();
        remainingMoves.clear();
        int moves = dice.isDouble() ? DOUBLE_MOVES : DICE_COUNT;
        for (int i = 0; i < moves; i++) {
            remainingMoves.add(dice.getValue(i % DICE_COUNT));
        }
        selectedPoint = NO_POINT;
        state = GameState.WAITING_MOVE;
        statusMessage = STATUS_ROLLING;
    }

    public synchronized void handlePointClick(int point) {
        if (state != GameState.WAITING_MOVE || dice.isRolling()) {
            return;
        }
        // с бара можно только входить, остальные шашки трогать нельзя
        if (board.getBarCount(currentPlayer) > 0) {
            selectedPoint = NO_POINT;
            if (!tryEnter(point)) {
                statusMessage = STATUS_MUST_ENTER;
            }
            return;
        }
        if (selectedPoint == NO_POINT) {
            selectChecker(point);
            return;
        }
        int from = selectedPoint;
        selectedPoint = NO_POINT;
        if (point == from) {
            // второй клик по той же шашке - пробуем выбросить
            if (!tryBearOff(from)) {
                statusMessage = STATUS_PICK_CHECKER;
            }
            return;
        }
        String error = tryMove(from, point);
        if (error == null) {
            return;
        }
        // не вышло, но кликнули по своей шашке - значит просто передумали
        if (board.getOwner(point) == currentPlayer) {
            selectChecker(point);
        } else {
            statusMessage = error;
        }
    }

    public synchronized void activateRush() {
        if (state == GameState.GAME_OVER) {
            return;
        }
        if (bonusTurns > 0) {
            statusMessage = STATUS_RUSH_ALREADY;
            return;
        }
        int index = currentPlayer.ordinal();
        if (energy[index] < ENERGY_RUSH_COST) {
            statusMessage = STATUS_ENERGY_LOW;
            return;
        }
        energy[index] -= ENERGY_RUSH_COST;
        bonusTurns++;
        statusMessage = STATUS_RUSH_ACTIVATED;
    }

    public Board getBoard() {
        return board;
    }

    public synchronized Player getCurrentPlayer() {
        return currentPlayer;
    }

    public synchronized GameState getState() {
        return state;
    }

    public synchronized int getSelectedPoint() {
        return selectedPoint;
    }

    public synchronized int getEnergy(Player player) {
        return energy[player.ordinal()];
    }

    public synchronized int getBonusTurns() {
        return bonusTurns;
    }

    public synchronized String getStatusMessage() {
        return statusMessage;
    }

    public synchronized List<Integer> getRemainingMoves() {
        return new ArrayList<>(remainingMoves);
    }

    public synchronized int getDieDisplayValue(int index) {
        return dice.getDisplayValue(index);
    }

    private void selectChecker(int point) {
        if (board.getOwner(point) == currentPlayer) {
            selectedPoint = point;
            statusMessage = STATUS_PICK_TARGET;
        } else {
            statusMessage = STATUS_NOT_YOUR_CHECKER;
        }
    }

    private String promptStatus() {
        return board.getBarCount(currentPlayer) > 0 ? STATUS_PICK_ENTRY : STATUS_PICK_CHECKER;
    }

    // возвращает null, если ход сделан, иначе текст причины
    private String tryMove(int from, int to) {
        int start = currentPlayer.mirror(from);
        int distance = currentPlayer.mirror(to) - start;
        if (distance <= 0) {
            return MSG_BACKWARD;
        }
        List<Integer> path = new ArrayList<>();
        if (!findPath(start, start + distance, new ArrayList<>(remainingMoves), path, true)) {
            boolean reachable = findPath(start, start + distance,
                    new ArrayList<>(remainingMoves), new ArrayList<>(), false);
            return reachable ? MSG_BLOCKED : MSG_NO_DIE;
        }
        boolean hit = false;
        int current = from;
        for (int die : path) {
            board.removeChecker(current);
            int next = currentPlayer.mirror(currentPlayer.mirror(current) + die);
            hit |= land(next);
            current = next;
        }
        finishMove(path, hit);
        return null;
    }

    // ищет набор кубиков, который в сумме даёт нужное расстояние
    private boolean findPath(int progress, int target, List<Integer> left,
                             List<Integer> path, boolean checkBlocks) {
        if (progress == target) {
            return true;
        }
        for (int die : new TreeSet<>(left)) {
            int next = progress + die;
            if (next > target) {
                continue;
            }
            if (checkBlocks && !isOpen(currentPlayer.mirror(next))) {
                continue;
            }
            left.remove(Integer.valueOf(die));
            path.add(die);
            if (findPath(next, target, left, path, checkBlocks)) {
                return true;
            }
            path.remove(path.size() - 1);
            left.add(die);
        }
        return false;
    }

    private boolean tryEnter(int point) {
        int index = currentPlayer.mirror(point);
        int die = index + MIN_DIE_VALUE;
        if (index >= POINTS_PER_QUADRANT || !remainingMoves.contains(die) || !isOpen(point)) {
            return false;
        }
        board.removeFromBar(currentPlayer);
        boolean hit = land(point);
        finishMove(List.of(die), hit);
        return true;
    }

    private boolean tryBearOff(int from) {
        int die = bearOffDie(from);
        if (die < 0) {
            return false;
        }
        board.removeChecker(from);
        board.addBorneOff(currentPlayer);
        finishMove(List.of(die), false);
        return true;
    }

    // ставит шашку на пункт, одинокую шашку соперника выбивает на бар
    private boolean land(int to) {
        boolean hit = false;
        Player opponent = currentPlayer.opposite();
        if (board.getOwner(to) == opponent) {
            board.removeChecker(to);
            board.addToBar(opponent);
            int index = currentPlayer.ordinal();
            energy[index] = Math.min(ENERGY_MAX, energy[index] + ENERGY_PER_HIT);
            hit = true;
        }
        board.addChecker(to, currentPlayer);
        return hit;
    }

    private void finishMove(List<Integer> usedDice, boolean hit) {
        for (int die : usedDice) {
            remainingMoves.remove(Integer.valueOf(die));
        }
        selectedPoint = NO_POINT;
        String prefix = hit ? STATUS_HIT : NO_MARKER;
        if (board.getBorneOffCount(currentPlayer) >= CHECKERS_PER_PLAYER) {
            state = GameState.GAME_OVER;
            remainingMoves.clear();
            statusMessage = currentPlayer.getLabel() + STATUS_WIN_SUFFIX;
        } else if (remainingMoves.isEmpty()) {
            endTurn(prefix);
        } else if (!hasAnyMove()) {
            endTurn(prefix + STATUS_NO_MOVES);
        } else {
            statusMessage = prefix + promptStatus();
        }
    }

    private void endTurn(String prefix) {
        selectedPoint = NO_POINT;
        remainingMoves.clear();
        state = GameState.WAITING_ROLL;
        if (bonusTurns > 0) {
            bonusTurns--;
            statusMessage = prefix + STATUS_RUSH_ROLL;
        } else {
            currentPlayer = currentPlayer.opposite();
            statusMessage = prefix + STATUS_ROLL_PROMPT;
        }
    }

    // пункт свободен, свой или там одна шашка соперника
    private boolean isOpen(int point) {
        Player owner = board.getOwner(point);
        return owner == null || owner == currentPlayer || board.getCount(point) == 1;
    }

    private boolean canMove(int from, int die) {
        if (board.getOwner(from) != currentPlayer) {
            return false;
        }
        int progress = currentPlayer.mirror(from) + die;
        if (progress < POINT_COUNT) {
            return isOpen(currentPlayer.mirror(progress));
        }
        return allInHome() && (progress == POINT_COUNT || noCheckerBehind(currentPlayer.mirror(from)));
    }

    // точный кубик для выброса, а если его нет - наименьший из больших
    private int bearOffDie(int from) {
        if (board.getOwner(from) != currentPlayer) {
            return -1;
        }
        for (int die : new TreeSet<>(remainingMoves)) {
            if (currentPlayer.mirror(from) + die >= POINT_COUNT && canMove(from, die)) {
                return die;
            }
        }
        return -1;
    }

    private boolean allInHome() {
        if (board.getBarCount(currentPlayer) > 0) {
            return false;
        }
        for (int point = 0; point < POINT_COUNT; point++) {
            if (board.getOwner(point) == currentPlayer && !currentPlayer.isHome(point)) {
                return false;
            }
        }
        return true;
    }

    private boolean noCheckerBehind(int fromProgress) {
        for (int progress = HOME_START; progress < fromProgress; progress++) {
            if (board.getOwner(currentPlayer.mirror(progress)) == currentPlayer) {
                return false;
            }
        }
        return true;
    }

    private boolean hasAnyMove() {
        for (int die : new TreeSet<>(remainingMoves)) {
            if (board.getBarCount(currentPlayer) > 0) {
                if (isOpen(currentPlayer.entryPoint(die))) {
                    return true;
                }
                continue;
            }
            for (int point = 0; point < POINT_COUNT; point++) {
                if (canMove(point, die)) {
                    return true;
                }
            }
        }
        return false;
    }
}