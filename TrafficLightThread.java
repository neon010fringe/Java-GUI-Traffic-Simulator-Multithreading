/**
 James Deady
 CMSC 335
 Project 4
 Class Descriptions:

 TrafficLightThread is a Runnable class that cycles a traffic light through RED, GREEN, and YELLOW states.
 It updates both a small marker label on the track and a full text label in the info panel.
 Includes pause and resume support.

 TrafficLightColor:
 An enum that defines the three valid traffic light states: RED, GREEN, and YELLOW.
 */

import javax.swing.*;
import java.awt.*;

// Enum to represent the possible light colors

enum TrafficLightColor {
  RED, GREEN, YELLOW
}


// TrafficLightThread: Simulates the cycle of a traffic light and updates both the map marker and status label.

class TrafficLightThread implements Runnable {

  private TrafficLightColor tlc;             // Current traffic light color
  private boolean changed = false;           // Flag to track state change
  private final JLabel marker;               // Small map marker label
  private final JLabel fullStatusLabel;      // Full intersection status label
  private volatile boolean paused = false;   // Pause flag for thread control

  // Constructor: requires GUI labels to update
  public TrafficLightThread(JLabel marker, JLabel fullStatusLabel) {
    this.marker = marker;
    this.fullStatusLabel = fullStatusLabel;
    this.tlc = TrafficLightColor.RED; // Start at RED
  }

  // Pause the thread
  public synchronized void pause() {
    paused = true;
  }

  // Resume the thread
  public synchronized void resume() {
    paused = false;
    notifyAll(); // Wake up the thread if it's paused
  }

  // Main light cycle logic
  public void run() {
    while (true) {
      // Pause check loop
      synchronized (this) {
        while (paused) {
          try {
            wait(); // Wait until resume() is called
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
          }
        }
      }

      // Sleep based on light state
      try {
        switch (tlc) {
          case GREEN -> Thread.sleep(2000);  // Green for 2s
          case YELLOW -> Thread.sleep(4000); // Yellow for 4s
          case RED -> Thread.sleep(2000);    // Red for 2s
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      changeColor(); // Advance to next light color
    }
  }

  // Advance the traffic light color and update GUI
  synchronized void changeColor() {
    switch (tlc) {
      case RED -> tlc = TrafficLightColor.GREEN;
      case GREEN -> tlc = TrafficLightColor.YELLOW;
      case YELLOW -> tlc = TrafficLightColor.RED;
    }

    // Update the UI components safely from the EDT
    if (marker != null && fullStatusLabel != null) {
      SwingUtilities.invokeLater(() -> {
        // Change marker to a colored circle
        switch (tlc) {
          case RED -> marker.setIcon(new ColorCircleIcon(Color.RED, 12));
          case GREEN -> marker.setIcon(new ColorCircleIcon(Color.GREEN, 12));
          case YELLOW -> marker.setIcon(new ColorCircleIcon(Color.YELLOW, 12));
        }
        marker.setText(""); // ensure no text appears next to the icon

        // Keep fullStatusLabel text updates
        fullStatusLabel.setText(fullStatusLabel.getText().split(":")[0] + ": " + tlc.toString());
      });
    }


    changed = true; // Flag that change occurred
    notify();       // Notify any waiting thread
  }

  // External method can block until a state change occurs
  synchronized void waitForChange() {
    try {
      while (!changed)
        wait();
      changed = false;
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  // Safely get the current color
  synchronized TrafficLightColor getColor() {
    return tlc;
  }
}
