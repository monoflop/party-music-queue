#!/bin/bash
clear
cd $PWD
echo "----------------------------------------------------------------------"
echo "---------------------PartyServer by Philipp Kutsch--------------------"
echo "  /~\\   _______  _____  __   _  _____  _______         _____   _____  "
echo " C oo   |  |  | |     | | \\  | |     | |______ |      |     | |_____] "
echo " _( ^)  |  |  | |_____| |  \\_| |_____| |       |_____ |_____| |       "
echo "/   ~\\"
echo "----------------------------------------------------------------------"

cd party

mvn package

cd target

if [ ! -f "party-1.0-SNAPSHOT.jar" ]; then
	echo "[FAIL] Build failed."
	exit
fi

cp -f "party-1.0-SNAPSHOT.jar" "../../build/"

docker build --tag=partyserver ../../build/.
