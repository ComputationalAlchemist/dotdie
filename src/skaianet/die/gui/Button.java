package skaianet.die.gui;

import skaianet.die.back.ATHcessible;
import skaianet.die.front.Color;

import java.awt.*;

public class Button extends Component {
    @ATHcessible
    public int width = 100;
    @ATHcessible
    public int height = 22;
    @ATHcessible
    public Color background = new Color(128, 128, 128);
    @ATHcessible
    public Color backgroundPressed = new Color(255, 255, 255);
    @ATHcessible
    public Color border = new Color(255, 255, 255);
    @ATHcessible
    public Color borderPressed = new Color(0, 0, 0);
    @ATHcessible
    public Color colorPressed = new Color(128, 128, 128);
    @ATHcessible
    public String text;
    @ATHcessible
    public int borderWidth = 0;
    @ATHcessible
    public int shiftX = 0;
    @ATHcessible
    public int shiftY = 0;
    @ATHcessible
    public Object signal; // Clicked as opposed to pressed or released.
    @ATHcessible
    public Object signalPress;
    @ATHcessible
    public Object signalRelease;
    @ATHcessible
    public boolean moveToTopWhenPressed;
    private boolean isPressed;

    public Button(String name) {
        text = name;
    }

    @Override
    protected void pressInternal(int x, int y, int btn) {
        if (x >= borderWidth && x < width - borderWidth && y >= borderWidth && y < height - borderWidth) {
            if (queue != null) {
                queue.post(signalPress);
            }
            isPressed = true;
            refresh();
        }
    }

    @Override
    protected void releaseInternal(int x, int y, int btn) {
        if (isPressed) {
            if (queue != null) {
                queue.post(signalRelease);
                if (x >= borderWidth && x < width - borderWidth && y >= borderWidth && y < height - borderWidth) {
                    queue.post(signal);
                }
            }
            isPressed = false;
            refresh();
        }
    }

    @Override
    protected void renderDispatch(Graphics2D g, boolean isTopPass) {
        if (isTopPass == (moveToTopWhenPressed && isPressed)) {
            g.setColor(isPressed ? borderPressed.toAWTColor() : border.toAWTColor());
            g.fillRect(0, 0, width, height);

            g.setColor(isPressed ? backgroundPressed.toAWTColor() : background.toAWTColor());
            g.fillRect(borderWidth, borderWidth, width - borderWidth * 2, height - borderWidth * 2);

            g.setColor(isPressed ? colorPressed.toAWTColor() : color.toAWTColor());
            FontMetrics metrics = g.getFontMetrics();
            int remainder = height - borderWidth - metrics.getHeight();
            renderText(g, text, shiftX + width / 2 - (metrics.stringWidth(text) - text.length()) / 2, shiftY + metrics.getAscent() + remainder);
        }
    }

    @Override
    protected void renderInternal(Graphics2D g) {
        throw new RuntimeException("Should not happen.");
    }
}
