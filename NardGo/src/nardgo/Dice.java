package nardgo;

import java.util.Arrays;
import java.util.Random;

public class Dice implements GameConstants {
    private final Random random = new Random();
    private final int[] values = new int[DICE_COUNT];
    private final int[] shownValues = new int[DICE_COUNT];
    private int framesLeft;

    public Dice() {
        reset();
    }

    public void reset() {
        Arrays.fill(values, MIN_DIE_VALUE);
        Arrays.fill(shownValues, MIN_DIE_VALUE);
        framesLeft = 0;
    }

    public void roll() {
        for (int i = 0; i < DICE_COUNT; i++) {
            values[i] = rollOne();
            shownValues[i] = rollOne();
        }
        framesLeft = DICE_ROLL_FRAMES;
    }

    public void tick() {
        if (framesLeft <= 0) {
            return;
        }
        framesLeft--;
        if (framesLeft % DICE_FACE_CHANGE_FRAMES == 0) {
            for (int i = 0; i < DICE_COUNT; i++) {
                shownValues[i] = rollOne();
            }
        }
    }

    public boolean isRolling() {
        return framesLeft > 0;
    }

    public boolean isDouble() {
        return values[FIRST_DIE] == values[SECOND_DIE];
    }

    public int getValue(int index) {
        return values[index];
    }

    public int getDisplayValue(int index) {
        return isRolling() ? shownValues[index] : values[index];
    }

    private int rollOne() {
        return random.nextInt(DICE_SIDES) + MIN_DIE_VALUE;
    }
}