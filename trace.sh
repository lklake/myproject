#!/bin/sh
export PATH=$PATH:/opt/openjdk-bin-11.0.12_p7/bin/
javac -encoding UTF-8 -cp . ticketingsystem/Trace.java
java -cp . ticketingsystem/Trace
