package nardgo;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyController extends KeyAdapter implements GameConstants {
    private final GameEngine engine;
    private final Runnable exitAction;

    public KeyController(GameEngine engine, Runnable exitAction) {
        this.engine = engine;
        this.exitAction = exitAction;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KEY_NEW_GAME -> engine.newGame();
            case KEY_ROLL_DICE -> engine.rollDice();
            case KEY_RUSH -> engine.activateRush();
            case KEY_EXIT -> exitAction.run();
            default -> { }
        }
    }
}