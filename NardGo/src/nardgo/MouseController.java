package nardgo;

import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseController extends MouseAdapter implements GameConstants {
    private final GameEngine engine;
    private final GameRenderer renderer;

    public MouseController(GameEngine engine, GameRenderer renderer) {
        this.engine = engine;
        this.renderer = renderer;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }
        int point = renderer.pointAt(e.getPoint());
        if (point != NO_POINT) {
            engine.handlePointClick(point);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        renderer.setHoverPoint(renderer.pointAt(e.getPoint()));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        renderer.setHoverPoint(NO_POINT);
    }
}