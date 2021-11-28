#!/bin/sh

/opt/openjdk-bin-11.0.12_p7/bin/javac -encoding UTF-8 -cp . ticketingsystem/GenerateHistory.java
/opt/openjdk-bin-11.0.12_p7/bin/javac -encoding UTF-8 -cp . ticketingsystem/Replay.java
/opt/openjdk-bin-11.0.12_p7/bin/java -cp . ticketingsystem/GenerateHistory 4 1000 1 0 0 > trace
/opt/openjdk-bin-11.0.12_p7/bin/java -cp . ticketingsystem/Replay 4 trace 1 history
