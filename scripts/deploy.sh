#!/usr/bin/env bash

#mvn clean package

ssh -i ~/.ssh/id_ed25519 root@80.90.191.154 <<EOF

#pgrep java  | xargs kill -9

nohup java -jar bots/TGBotTest/target/SearchForMessagesBot-0.0.1-SNAPSHOT.jar > log.txt &
nohup java -jar bots/TGBotTest/out/TGBotTest_jar/TGBotTest.jar
EOF