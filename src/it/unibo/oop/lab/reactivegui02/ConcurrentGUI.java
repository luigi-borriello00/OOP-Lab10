package it.unibo.oop.lab.reactivegui02;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ConcurrentGUI extends JFrame{

    /**
     * 
     */
    private static final long serialVersionUID = -8630968055862320453L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");

    public ConcurrentGUI() {
        super();
        final Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * ConcurrentGUI.WIDTH_PERC), ((int) (screenSize.getHeight() * ConcurrentGUI.HEIGHT_PERC)));
        final JPanel myPan = new JPanel();
        myPan.add(display);
        myPan.add(down);
        myPan.add(up);
        myPan.add(stop);
        this.getContentPane().add(myPan);
        this.setVisible(true);

        final Agent agent = new Agent();
        new Thread(agent).start();

//      Adding the Listeners        
        stop.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                // Agent should be final
                agent.stopCounting();
                up.setEnabled(false);
                down.setEnabled(false);
            }
        });

        down.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                agent.setDown();

            }
        });

        up.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                // Agent should be final
                agent.setUp();
            }
        });
    }
    private class Agent implements Runnable {
        private volatile boolean stop;
        private volatile int counter;
        private volatile boolean direction = true; //True -> up    &   False -> down

        public void run() {
            while (!this.stop) {
                try {
                    /*
                     * All the operations on the GUI must be performed by the
                     * Event-Dispatch Thread (EDT)!
                     */
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(Integer.toString(Agent.this.counter)));
                    if (this.direction) {
                        this.counter++;
                    }
                    else {
                        this.counter--;
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    /*
                     * This is just a stack trace print, in a real program there
                     * should be some logging and decent error reporting
                     */
                    ex.printStackTrace();
                }
            }
            
        }

        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stop = true;
        }

        public void setDown() {
            this.direction = false;
        }

        public void setUp() {
            this.direction = true;
        }
    }
}
