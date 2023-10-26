#!/bin/bash
ssh -i ~/.ssh/id_ed25519 root@80.90.191.154 << EOF
pgrep java  | xargs kill -9
nohup java -jar bots/TGBotTest/target/SearchForMessagesBot-0.0.1-SNAPSHOT.jar > log.txt &
exit
EOF