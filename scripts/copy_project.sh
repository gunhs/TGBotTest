#!/bin/bash
ssh -i ~/.ssh/id_ed25519 root@80.90.191.154 << EOF
rm -rf bots/TGBotTest/target
exit
scp -r ../target root@80.90.191.154:/root/bots/TGBotTest/
ssh -i ~/.ssh/id_ed25519 root@80.90.191.154
pgrep java  | xargs kill -9
nohup java -jar bots/TGBotTest/target/SearchForMessagesBot-0.0.1-SNAPSHOT.jar > log.txt &
EOF