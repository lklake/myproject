#!/bin/sh

/opt/openjdk-bin-11.0.12_p7/bin/javac -encoding UTF-8 -cp . ticketingsystem/GenerateHistory.java
/opt/openjdk-bin-11.0.12_p7/bin/java -cp . ticketingsystem/GenerateHistory 16 10000 1 0 0 > trace
/opt/openjdk-bin-11.0.12_p7/bin/java -jar VeriLinS.jar 16 trace 1 history
