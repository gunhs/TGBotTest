#!/bin/bash
sh ./mvn.sh
sh ./delete.sh
sleep 6
sh ./copy.sh
sh ./put