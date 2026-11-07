package nardgo;

import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class NardGoFrame extends JFrame implements GameConstants {
    private final GameEngine engine;
    private final GameRenderer renderer;
    private final GameLoop loop;

    public NardGoFrame() {
        super(WINDOW_TITLE);
        engine = new GameEngine(new Board());
        renderer = new GameRenderer(engine);
        loop = new GameLoop(engine, renderer);
        configureWindow();
        attachInput();
        engine.newGame();
    }

    public void launch() {
        setVisible(true);
        renderer.requestFocusInWindow();
        loop.start();
    }

    private void configureWindow() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdown();
            }
        });
        setContentPane(renderer);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    private void attachInput() {
        MouseController mouse = new MouseController(engine, renderer);
        renderer.addMouseListener(mouse);
        renderer.addMouseMotionListener(mouse);
        renderer.addKeyListener(new KeyController(engine, this::shutdown));
    }

    private void shutdown() {
        loop.stop();
        dispose();
        System.exit(0);
    }
}