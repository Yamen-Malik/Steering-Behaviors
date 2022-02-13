Steering Behaviors
====================
About the project
-----------------
In this project I followed the nature of code [course](https://www.youtube.com/playlist?list=PLRqwX-V7Uu6ZV4yEcW3uDwOgGXKUUsPOM) / [book](https://natureofcode.com) from [the coding train](https://www.youtube.com/c/TheCodingTrain) channel.

Requirements
------------
This code uses:
1. Java
2. Swing library

About The App
--------------
Start the app by running app.java, the app will open the menu frame first, then you can start the simulation by clickig the Start button.

## Menu Frame
In the menu frame you can edit the following vehicles properties:
* Mass
* Maximum Velocity
* Maximum Force
* Size
* Behavior   (Wander, Seek, Flee, Pursue, Evade)
* Target     (the target that the vehicle will Seek/Flee/Pursue/Evade)
* Edge Mode  (how the vehicle will react when it reaches the edge of the screen)
* Color
* Path length (the length of the line that will represent the path)
* Path Mode (how the path will be drawn on the screen)


![Menu Preview](https://user-images.githubusercontent.com/60931606/153755395-38f2f5d5-bf15-440a-9414-4027de8ea6d5.png)

## Simulation Frame
In this frame you should see the vehicles moving around.

### Exampels: (Github reduces the frame rate)

#### <pre><center> Behavior: Wander<br> Edge Modes:    Blue: Wrap, Green: Bounce</pre>
![Wander](https://user-images.githubusercontent.com/60931606/153756323-ed5a3294-54bb-4ab7-b643-d5d544d12b44.gif)

#### <pre><center> Behavior:    Green: Pursue, White: Wander</pre>

![Pursue   Wander](https://user-images.githubusercontent.com/60931606/153756421-682e0058-9bc3-4272-8c59-4882589a5747.gif)

