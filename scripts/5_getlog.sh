#!/bin/bash
rm -rf ../log.txt << EOF
scp -r ../target root@80.90.191.154:/root/bots/TGBotTest/
EOF