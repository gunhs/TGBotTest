#!/bin/bash
ssh -i ~/.ssh/id_ed25519 root@80.90.191.154 << EOF
sudo reboot
EOF