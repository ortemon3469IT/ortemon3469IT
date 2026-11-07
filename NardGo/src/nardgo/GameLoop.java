package nardgo;

public class GameLoop implements Runnable, GameConstants {
    private final GameEngine engine;
    private final GameRenderer renderer;
    private volatile boolean running;
    private Thread thread;

    public GameLoop(GameEngine engine, GameRenderer renderer) {
        this.engine = engine;
        this.renderer = renderer;
    }

    public synchronized void start() {
        if (running) {
            return;
        }
        running = true;
        thread = new Thread(this, "game-loop");
        thread.setDaemon(true);
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    public void run() {
        while (running) {
            engine.update();
            renderer.repaint();
            try {
                Thread.sleep(FRAME_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}