CMSC 335 Project 3

Overview

In this project you will construct a Java GUI that uses event handlers, listeners and incorporates Java’s
concurrency functionality and the use of threads. The following Oracle Tutorials may be useful to help
you become comfortable with Thread processes.

• https://docs.oracle.com/javase/tutorial/essential/concurrency/

In addition, a zip file is included that includes several Oracle Java files that use different types of GUI
components as well as threads. I recommend going through the reading above and GUI below and the
examples to become familiar before attempting the final project.

Java FX:

• https://docs.oracle.com/javafx/2/get_started/jfxpub-get_started.htm

• https://docs.oracle.com/javafx/2/ui_controls/jfxpub-ui_controls.htm

• https://docs.oracle.com/javase/8/javase-clienttechnologies.htm

• https://docs.oracle.com/javafx/2/
Java Swing (Legacy):

• https://docs.oracle.com/javase/tutorial/uiswing/index.html

Assignment Details

As a new engineer for a traffic congestion mitigation company, you have been tasked with developing a
Java GUI that displays time, traffic signals and other information for traffic analysts. The final GUI design
is up to you but should include viewing ports/panels to display the following components of the
simulation:

1. Current time stamps in 1 second intervals
2. Real-time Traffic light display for three major intersections
3. X, Y positions and speed of up to 3 cars as they traverse each of the 3 intersections


Some of the details of the simulation are up to you but the following guidelines will set the guardrails:

1. The components listed above should run in separate threads.
2. Loop through the simulation with button(s) providing the ability to start, pause, stop and
continue the simulation.
3. You will need to use basic distance formulas such as distance = Speed * time. Be sure to be
consistent and define your units of measure (e.g. mile/hour, versus km/hour)
4. Assume a straight distance between each traffic light of 1000 meters.
5. Since you are traveling a straight line, you can assume Y = 0 for your X,Y positions.
6. Provide the ability to add more cars and intersections to the simulation through the GUI.
7. Don’t worry about physics. Assume cars will stop on a dime for red lights, and continue through
yellow lights and green lights.
8. Document all assumptions and limitations of your simulation.


# Java GUI Traffic Simulator (Multithreading)

An updated and improved version of my original Java Swing-based traffic simulator project.
This version includes:
- Redesigned UI with light blue panels and increased top panel size
- Colored traffic light markers that cycle between red, yellow, and green
- Improved button behavior (cannot add cars/lights during pause)
- Better spacing and aesthetic refinements for a sleeker look
