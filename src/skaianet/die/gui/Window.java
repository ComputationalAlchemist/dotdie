package skaianet.die.gui;

import skaianet.die.back.ATHAlive;
import skaianet.die.back.ATHcessible;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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

            {
                addMouseListener(new MouseProcessor());
                addMouseMotionListener(new MouseMotionProcessor());
            }

            public void paintComponent(Graphics go) {
                super.paintComponent(go);

                renderDispatch((Graphics2D) go, false);
                renderDispatch((Graphics2D) go, true);
            }
        });
        frame.setResizable(false);
    }

    public void refresh() {
        super.refresh();
        frame.repaint();
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

    private class MouseProcessor implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            mousePressed(mouseEvent);
            mouseReleased(mouseEvent);
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            press(mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getButton());
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            release(mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getButton());
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
        }
    }

    private class MouseMotionProcessor implements MouseMotionListener {
        @Override
        public void mouseDragged(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {
        }
    }
}
