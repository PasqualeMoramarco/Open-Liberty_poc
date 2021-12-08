#!/bin/bash

docker stop system inventory kafka zookeeper postgres

docker network rm reactive-app