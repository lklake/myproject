#!/bin/sh
export PATH=$PATH:/opt/openjdk-bin-11.0.12_p7/bin/
javac -encoding UTF-8 -cp . ticketingsystem/GenerateHistory.java
java -cp . ticketingsystem/GenerateHistory 64 10000 1 0 0 > trace
java -jar VeriLinS.jar 64 trace 1 history
