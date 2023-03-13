Three projects made for the course "Distributed and Concurrent Programming" (MSc, Computer Science @Unibo).
The focus of these projects is about concurrency, so other aspects of software development (Testing, code quality, OOP principles, etc..) were left out.

## 1st Assignment ##
Professor Ricci gave us a program which simulate the behaviour of N bodies inside a field. 
These bodies are moving because of the repulsive force between them.
The program needs to do a lot of computation, so the simulation is slow.
Our goal is to provide a solution to speed-up the simulation using different threads.
For more information you can check the assignment report (only available in italian).


## 2nd Assignment ##
This assignment is divided in three points:
- Implement a task-based version of the first assignment.
- Event-driven Asynchronous Programming.
- Reactive Programming.

## 3rd Assignment ##
Implement an application to monitor rain level in a city.
The city is divided in a NxM grid: in each zone there are multiple sensors and an Emergency Station.
Each sensor monitor the rain level: if it exceed a treshold the sensor change its state to 'Alarm'. if the majority of sensors in a zone are in alarm, the local Emergency station will be notified. A sensor can also randomly fail.
Once the emergency is handled by the station, the zone return in a normal state.

The application is developed using Akka and Scala.

