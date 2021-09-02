#!/bin/bash
clear
docker run \
  --net="host" \
  --mount type=bind,source=/usr/local/partyserver,target=/app/data \
  partyserver
