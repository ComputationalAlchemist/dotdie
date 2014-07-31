package skaianet.die.gui;

import skaianet.die.back.ATHcessible;
import skaianet.die.front.Color;

import java.awt.*;
import java.util.ArrayList;

public class Panel extends Component {
    private final ArrayList<Component> components = new ArrayList<>();
    @ATHcessible
    public int width = 300;
    @ATHcessible
    public int height = 200;
    @ATHcessible
    public Color background = new Color(0, 0, 0);
    @ATHcessible
    public Color border = new Color(255, 255, 255);
    @ATHcessible
    public int borderWidth = 0;

    @ATHcessible
    public void add(Component component) {
        component.refreshMaster = this;
        if (!components.contains(component)) {
            components.add(component);
        }
    }

    @Override
    protected void pressInternal(int x, int y, int btn) {
        for (Component comp : components) {
            comp.press(x, y, btn);
        }
    }

    @Override
    protected void releaseInternal(int x, int y, int btn) {
        for (Component comp : components) {
            comp.release(x, y, btn);
        }
    }

    @Override
    protected void renderInternal(Graphics2D g) {
        g.setColor(border.toAWTColor());
        g.fillRect(0, 0, width, height);

        g.setColor(background.toAWTColor());
        g.fillRect(borderWidth, borderWidth, width - borderWidth * 2, height - borderWidth * 2);

        for (Component component : components) {
            component.render(g, width, height);
        }
    }
}
