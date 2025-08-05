/**
 James Deady
 CMSC 335
 Project 4
 Class Descriptions:
 TrafficMainFrame:
 The main GUI class that constructs the entire traffic simulator window. It creates and organizes panels for the clock,
 sliders, data labels, and control buttons. It also initializes and controls car threads, traffic light threads, and
 simulation logic including add/pause/resume/stop functionality.

 CustomSliderUI:
 A utility class that customizes the look of each car's slider thumb (the knob) by painting it with a specified color.
 This enhances the visual distinction between cars.
 */

import javax.swing.*; // GUI components
import java.awt.*; // Layouts, Colors
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicSliderUI;

// Main GUI class for the traffic simulation
public class TrafficMainFrame extends JFrame implements ActionListener {

    // GUI control buttons
    private JButton startButton, pauseButton, resumeButton, stopButton;
    private JButton addCarButton, addLightButton;

    // Threads for clock, cars, and lights
    private ClockThread clockThread;
    private CarThread carThread1, carThread2, carThread3, carThread4;
    private TrafficLightThread light1, light2, light3, light4;

    // Labels for data display
    private JLabel car1Data, car2Data, car3Data, car4Data;
    private JLabel intersection4Label, clockLabel, shutdownLabel;

    // Flags for additional elements
    private boolean car4Added = false;
    private boolean light4Added = false;

    // Slider tracks for cars
    private JSlider car1Slider, car2Slider, car3Slider;

    // Map for traffic light positions and their threads
    private Map<Integer, TrafficLightThread> trafficLights;

    // Constructor initializes and lays out all components
    public TrafficMainFrame() {
        setTitle("Traffic Simulator"); // Window title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit app on close
        setLayout(new BorderLayout()); // Use BorderLayout for major regions

        // Light blue for all panels
        Color lightBlue = new Color(220, 235, 245);


        // TOP PANEL    // Current time, Slider with car and traffic light operating like a traffic simulation
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setPreferredSize(new Dimension(1000, 275)); // +50 px taller
        topPanel.setBackground(lightBlue); // light blue background

        topPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(90, 90, 90), 2, true),  // softer dark gray, rounded
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        topPanel.setBackground(new Color(210, 225, 240));

        // Add digital clock label
        clockLabel = new JLabel("Clock: 00:00:00");
        clockLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        clockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(clockLabel);
        topPanel.add(Box.createVerticalStrut(50)); // Spacer

        // Layered pane to hold car sliders and light markers
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1000, 150));

        // Create sliders for the first 3 cars
        car1Slider = createSlider(10, Color.DARK_GRAY);
        car2Slider = createSlider(40, Color.BLUE);
        car3Slider = createSlider(70, Color.RED);

        // Add sliders to the layered pane
        layeredPane.add(car1Slider);
        layeredPane.add(car2Slider);
        layeredPane.add(car3Slider);

        // Create and add markers for intersections
        JLabel marker1 = createMarker(1250);
        JLabel marker2 = createMarker(2500);
        JLabel marker3 = createMarker(3750);
        layeredPane.add(marker1, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(marker2, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(marker3, JLayeredPane.PALETTE_LAYER);

        topPanel.add(layeredPane);

        // ==== TOP PANEL WRAPPER ====
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // outside spacing
        topWrapper.add(topPanel);
        add(topWrapper, BorderLayout.NORTH);


        // MIDDLE PANEL     // Live updates of traffic light, cars' speed and position
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));

        middlePanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(90, 90, 90), 2, true),  // softer dark gray, rounded
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        middlePanel.setBackground(new Color(210, 225, 240));

        middlePanel.setPreferredSize(new Dimension(1000, 180)); // adjust height as you like


        // Create labels for intersection statuses
        JLabel intersection1Label = new JLabel("Intersection 1: RED");
        JLabel intersection2Label = new JLabel("Intersection 2: YELLOW");
        JLabel intersection3Label = new JLabel("Intersection 3: GREEN");
        intersection4Label = new JLabel("Intersection 4: —");

        // Create labels for car data
        car1Data = new JLabel("Car 1 — Pos: 0 | Speed: 15 m/s");
        car2Data = new JLabel("Car 2 — Pos: 0 | Speed: 10 m/s");
        car3Data = new JLabel("Car 3 — Pos: 0 | Speed: 7 m/s");
        car4Data = new JLabel("Car 4 — Available");

        // Add intersection and car labels to sub-panels
        JPanel intersectionPanel = new JPanel();
        intersectionPanel.setLayout(new BoxLayout(intersectionPanel, BoxLayout.Y_AXIS));
        intersectionPanel.add(intersection1Label);
        intersectionPanel.add(intersection2Label);
        intersectionPanel.add(intersection3Label);
        intersectionPanel.add(intersection4Label);

        JPanel carPanel = new JPanel();
        carPanel.setLayout(new BoxLayout(carPanel, BoxLayout.Y_AXIS));
        carPanel.add(car1Data);
        carPanel.add(car2Data);
        carPanel.add(car3Data);
        carPanel.add(car4Data);

        middlePanel.add(intersectionPanel);
        middlePanel.add(Box.createVerticalStrut(10));
        middlePanel.add(carPanel);


        // ==== MIDDLE PANEL WRAPPER ====
        JPanel middleWrapper = new JPanel();
        middleWrapper.setLayout(new BoxLayout(middleWrapper, BoxLayout.Y_AXIS));
        middleWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // outside spacing
        middleWrapper.add(middlePanel);

        add(middleWrapper, BorderLayout.CENTER);

        // BOTTOM PANEL     // Buttons: Start, Pause, Continue, Add Car, Add Traffic Light, Stop / Exit message
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setPreferredSize(new Dimension(1000, 60));

        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(90, 90, 90), 2, true),  // softer dark gray, rounded
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        buttonPanel.setBackground(new Color(210, 225, 240));

        // Create and add control buttons
        startButton = new JButton("Start");
        startButton.addActionListener(this);
        buttonPanel.add(startButton);

        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(this);
        buttonPanel.add(pauseButton);

        resumeButton = new JButton("Resume");
        resumeButton.addActionListener(this);
        buttonPanel.add(resumeButton);

        addCarButton = new JButton("Add Car");
        addCarButton.addActionListener(this);
        buttonPanel.add(addCarButton);
        addCarButton.setEnabled(false);


        addLightButton = new JButton("Add Traffic Light");
        addLightButton.addActionListener(this);
        buttonPanel.add(addLightButton);
        addLightButton.setEnabled(false);

        stopButton = new JButton("Stop");
        stopButton.addActionListener(this);
        buttonPanel.add(stopButton);

        // Label for shutdown countdown
        shutdownLabel = new JLabel(" ", SwingConstants.CENTER);
        shutdownLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        shutdownLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        shutdownLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        // Combine buttons and shutdown label into a single container
        JPanel bottomWrapper = new JPanel();
        bottomWrapper.setLayout(new BoxLayout(bottomWrapper, BoxLayout.Y_AXIS));
        bottomWrapper.add(buttonPanel);
        bottomWrapper.add(shutdownLabel);

        // add(bottomWrapper, BorderLayout.SOUTH); // Add entire section to SOUTH

        // ==== BOTTOM PANEL WRAPPER ====
        JPanel bottomWrapperOuter = new JPanel(new BorderLayout());
        bottomWrapperOuter.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // outside spacing
        bottomWrapperOuter.add(bottomWrapper);

        add(bottomWrapperOuter, BorderLayout.SOUTH);


        // INITIALIZE TRAFFIC LIGHTS
        light1 = new TrafficLightThread(marker1, intersection1Label);
        light2 = new TrafficLightThread(marker2, intersection2Label);
        light3 = new TrafficLightThread(marker3, intersection3Label);

        trafficLights = new HashMap<>();
        trafficLights.put(1250, light1);
        trafficLights.put(2500, light2);
        trafficLights.put(3750, light3);

        // Final window setup
        setSize(1100, 600);
        setVisible(true);
    }

    // START SIMULATION LOGIC
    private void startSimulation() {
        clockThread = new ClockThread(clockLabel);
        clockThread.start();

        new Thread(light1).start();
        new Thread(light2).start();
        new Thread(light3).start();

        carThread1 = new CarThread(car1Slider, 15, trafficLights, car1Data);
        carThread2 = new CarThread(car2Slider, 5, trafficLights, car2Data);
        carThread3 = new CarThread(car3Slider, 9, trafficLights, car3Data);

        new Thread(carThread1).start();
        new Thread(carThread2).start();
        new Thread(carThread3).start();
    }

    // PAUSE SIMULATION
    private void pauseSimulation() {
        light1.pause();
        light2.pause();
        light3.pause();
        // Add car pause logic if needed
    }

    // HANDLE BUTTON EVENTS
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            startSimulation();
            startButton.setEnabled(false);
            addCarButton.setEnabled(true);
            addLightButton.setEnabled(true);

        } else if (e.getSource() == pauseButton) {
            clockThread.pauseClock();
            light1.pause(); light2.pause(); light3.pause();
            carThread1.pause(); carThread2.pause(); carThread3.pause();
            if (car4Added && carThread4 != null) carThread4.pause();
            if (light4Added && light4 != null) light4.pause();
            addCarButton.setEnabled(false);
            addLightButton.setEnabled(false);

        } else if (e.getSource() == resumeButton) {
            clockThread.resumeClock();
            light1.resume(); light2.resume(); light3.resume();
            carThread1.resume(); carThread2.resume(); carThread3.resume();
            if (car4Added && carThread4 != null) carThread4.resume();
            if (light4Added && light4 != null) light4.resume();
            addCarButton.setEnabled(!car4Added);
            addLightButton.setEnabled(!light4Added);

        } else if (e.getSource() == addCarButton && !car4Added) {
            JSlider car4Slider = createSlider(100, Color.MAGENTA);
            ((JLayeredPane) car1Slider.getParent()).add(car4Slider);
            ((JLayeredPane) car1Slider.getParent()).repaint();
            car4Data.setText("Car 4 — Pos: 0 | Speed: 6 m/s");
            carThread4 = new CarThread(car4Slider, 6, trafficLights, car4Data);
            new Thread(carThread4).start();
            car4Added = true;
            addCarButton.setEnabled(false);
        } else if (e.getSource() == addLightButton && !light4Added) {
            JLabel marker4 = createMarker(4000);
            ((JLayeredPane) car1Slider.getParent()).add(marker4, JLayeredPane.PALETTE_LAYER);
            ((JLayeredPane) car1Slider.getParent()).repaint();
            intersection4Label.setText("Intersection 4: RED");
            light4 = new TrafficLightThread(marker4, intersection4Label);
            trafficLights.put(4000, light4);
            new Thread(light4).start();
            light4Added = true;
            addLightButton.setEnabled(false);
        } else if (e.getSource() == stopButton) {
            shutdownLabel.setText("This program will be shutting down in a few seconds...");
            stopButton.setEnabled(false);
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);
            }).start();
        }
    }

    // HELPER: CREATE SLIDER
    private JSlider createSlider(int yPos, Color knobColor) {
        JSlider slider = new JSlider(0, 5000, 0);
        slider.setBounds(0, yPos, 1000, 20);
        slider.setUI(new CustomSliderUI(slider, knobColor));
        slider.setFocusable(false);
        return slider;
    }

    // HELPER: CREATE TRAFFIC LIGHT MARKER
    private JLabel createMarker(int meterPosition) {
        JLabel marker = new JLabel();
        marker.setIcon(new ColorCircleIcon(Color.RED, 12)); // small red circle
        int x = meterPosition * 1000 / 5000 - 10;
        marker.setBounds(x, 0, 20, 20);
        return marker;
    }


    // MAIN METHOD
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TrafficMainFrame());
    }

}

// CUSTOM SLIDER UI CLASS FOR COLORED KNOBS
class CustomSliderUI extends BasicSliderUI {
    private final Color knobColor;

    public CustomSliderUI(JSlider slider, Color knobColor) {
        super(slider);
        this.knobColor = knobColor;
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(knobColor);
        Rectangle knobBounds = thumbRect;
        int diameter = Math.min(knobBounds.width, knobBounds.height);
        int x = knobBounds.x + (knobBounds.width - diameter) / 2;
        int y = knobBounds.y + (knobBounds.height - diameter) / 2;
        g2.fillOval(x, y, diameter, diameter);
        g2.dispose();
    }
}

class ColorCircleIcon implements Icon {
    private final Color color;
    private final int size;

    public ColorCircleIcon(Color color, int size) {
        this.color = color;
        this.size = size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(color);
        g.fillOval(x, y, size, size);
        g.setColor(Color.BLACK); // optional border
        g.drawOval(x, y, size, size);
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}


