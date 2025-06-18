/**
 CMSC 335
 Project 4
 Class Descriptions:
 ClockThread updates the clock label every second with the current system time. Includes pause and resume functionality
 for synchronization with the simulation.
 */

import javax.swing.*;  // For JLabel and SwingUtilities

class ClockThread extends Thread {
    private final JLabel clockLabel;   // Label used to display the clock
    private volatile boolean paused = false;  // Tracks whether the clock is paused

    // Constructor assigns the label that will show the time
    public ClockThread(JLabel clockLabel) {
        this.clockLabel = clockLabel;
    }

    // Method to pause the clock (sets the paused flag to true)
    public synchronized void pauseClock() {
        paused = true;
    }

    // Method to resume the clock (clears the paused flag and wakes thread)
    public synchronized void resumeClock() {
        paused = false;
        notifyAll(); // Wake up the thread if it's waiting
    }

    // The main execution logic for the clock thread
    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                // If paused, wait until resumeClock is called
                while (paused) {
                    try {
                        wait(); // Releases lock and waits
                    } catch (InterruptedException e) {
                        return; // Exit if thread is interrupted
                    }
                }
            }

            try {
                // Get current system time (hh:mm:ss format)
                String currentTime = java.time.LocalTime.now().withNano(0).toString();

                // Update the JLabel on the Swing GUI thread
                SwingUtilities.invokeLater(() -> clockLabel.setText("Clock: " + currentTime));

                Thread.sleep(1000); // Update every second

            } catch (InterruptedException e) {
                return; // Exit thread if interrupted
            }
        }
    }
}
