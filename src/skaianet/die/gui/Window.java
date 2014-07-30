package skaianet.die.gui;

import skaianet.die.back.ATHAlive;
import skaianet.die.back.ATHcessible;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Window extends Panel implements ATHAlive {

    private final JFrame frame;
    @ATHcessible
    public boolean decorated = true;

    public Window(String name) {
        this.frame = new JFrame(name);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.add(new JPanel() {
            public Dimension getPreferredSize() {
                return new Dimension(width, height);
            }

            public void paintComponent(Graphics go) {
                super.paintComponent(go);

                renderInternal((Graphics2D) go);
            }
        });
        frame.setResizable(false);
    }

    @ATHcessible
    public void start() throws InvocationTargetException, InterruptedException {
        EventQueue.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setUndecorated(!decorated);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    @Override
    public boolean isAlive() {
        return frame.isVisible();
    }

    @Override
    public double getEnergy() {
        return 0;
    }
}
