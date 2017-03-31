#!/bin/bash

SERVICE_NAME=$1

if [ -z "$SERVICE_NAME" ]; then
	echo "$0 <service-name>"
	exit
fi

mvn install -pl $SERVICE_NAME -am && docker-compose stop $SERVICE_NAME && docker-compose rm -f $SERVICE_NAME && docker-compose up -d $SERVICE_NAME
