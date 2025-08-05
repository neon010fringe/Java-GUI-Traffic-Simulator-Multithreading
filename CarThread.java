/**
 James Deady
 CMSC 335
 Project 4
 Class Descriptions:
 CarThread is a Runnable class that simulates a car traveling along a JSlider path. It reads traffic light status near
 intersections and adjusts speed or stops accordingly. Updates the car’s position and speed on the GUI in real time.
 */

import javax.swing.*;  // For JSlider, JLabel, SwingUtilities
import java.util.Map;   // For Map to hold traffic light positions and objects

public class CarThread implements Runnable {

    // GUI slider that visually represents car's position
    private final JSlider slider;

    // Map linking intersection positions (in meters) to their traffic light thread objects
    private final Map<Integer, TrafficLightThread> trafficLights;

    // Label for live car data (position and speed)
    private final JLabel carDataLabel;

    // The default movement speed of the car (in meters per tick)
    private final int baseSpeed;

    // Indicates whether the thread is currently paused
    private volatile boolean paused = false;

    // Constructor initializes all required fields
    public CarThread(JSlider slider, int speed, Map<Integer, TrafficLightThread> trafficLights, JLabel carDataLabel) {
        this.slider = slider;
        this.baseSpeed = speed;
        this.trafficLights = trafficLights;
        this.carDataLabel = carDataLabel;
    }

    // Pause the car's movement
    public void pause() {
        paused = true;
    }

    // Resume the car's movement
    public void resume() {
        paused = false;
    }

    // The main logic executed when the thread starts
    @Override
    public void run() {
        // Run loop until the car reaches the end of the track
        while (slider.getValue() < slider.getMaximum()) {

            // If paused, sleep briefly and continue waiting
            while (paused) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return;  // Exit thread if interrupted
                }
            }

            try {
                int current = slider.getValue();  // Current car position
                boolean shouldMove = true;        // Determines whether the car should advance
                boolean isYellow = false;         // Tracks yellow light state

                // Check all intersections
                for (Map.Entry<Integer, TrafficLightThread> entry : trafficLights.entrySet()) {
                    int position = entry.getKey();                     // Intersection position
                    TrafficLightThread light = entry.getValue();      // Associated light

                    // If car is within ~100m of the intersection
                    if (current >= position - 100 && current <= position + 10) {
                        TrafficLightColor color = light.getColor();   // Get current light color

                        if (color == TrafficLightColor.RED) {
                            System.out.println("Car stopped at RED (" + position + ")");
                            shouldMove = false;

                            // Update car label with stopped status
                            SwingUtilities.invokeLater(() -> {
                                int pausedPos = slider.getValue();
                                carDataLabel.setText("Car — Pos: " + pausedPos + " | Speed: 0 m/s");
                            });

                            // Wait until light turns green or yellow
                            while (light.getColor() == TrafficLightColor.RED) {
                                Thread.sleep(100);
                            }

                            System.out.println("Light turned GREEN or YELLOW at " + position);

                        } else if (color == TrafficLightColor.YELLOW) {
                            System.out.println("Car slowing down at YELLOW (" + position + ")");
                            isYellow = true;
                        }
                    }
                }

                if (shouldMove) {
                    // If yellow, slow down; otherwise move at normal speed
                    int actualSpeed = isYellow ? baseSpeed / 2 : baseSpeed;
                    int newCurrent = slider.getValue();
                    int next = Math.min(newCurrent + actualSpeed, slider.getMaximum());

                    // If reaching end, display speed as 0
                    int displaySpeed = (next >= slider.getMaximum()) ? 0 : actualSpeed;

                    // Update slider and label on GUI thread
                    SwingUtilities.invokeLater(() -> {
                        slider.setValue(next);
                        carDataLabel.setText("Car — Pos: x = " + next + ", y = 0 | Speed: " + displaySpeed + " m/s");
                    });
                }

                Thread.sleep(50);  // Wait before next movement tick

            } catch (InterruptedException e) {
                return;  // Exit thread on interruption
            }
        }
    }
}


