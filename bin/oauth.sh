#!/bin/bash

HOST=$(docker-machine ip)

CLIENT_ID=acme
CLIENT_SECRET=acmesecret
USERNAME=dummy
PASSWORD=dummy

function ok {
	printf " [  \033[92mok\033[0m  ]\n"
}

function failed {
	printf " [\033[91mfailed\033[0m]\n"
}

function log {
	LINE_WIDTH=$(stty size | cut -d' ' -f2)
	PADDING=$(($LINE_WIDTH - 9))
	printf "${1}$(printf '.%.0s' $(eval echo "{1..$PADDING}"))" | head -c $PADDING
}

log "Calling /user/oauth/token"
ACCESS_TOKEN=$(curl -s -X POST -d "grant_type=password&username=$USERNAME&password=$PASSWORD" http://$CLIENT_ID:$CLIENT_SECRET@$HOST:9999/user/oauth/token | \
	python -c "import sys, json; print(json.load(sys.stdin)['access_token'])" 2>/dev/null)
[[ "$ACCESS_TOKEN" != ""  && "$ACCESS_TOKEN" == eyJhb* ]] && ok || failed

log "Calling /user/oauth/token w/o correct client credentials"
HTTP_STATUS_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST -d "grant_type=password&username=$USERNAME&password=$PASSWORD" http://wrongClient:wrongSecret@$HOST:9999/user/oauth/token 2>/dev/null)
[ "$HTTP_STATUS_CODE" == "401" ] && ok || failed

log "Calling /user/v1/me"
RESPONSE=$(curl -s -H "Authorization: Bearer $ACCESS_TOKEN" http://$HOST:9999/user/v1/me | \
	python -c "import sys, json; print(json.load(sys.stdin)['username'])" 2>/dev/null)
[ "$RESPONSE" == "$USERNAME" ] && ok || failed

log "Calling /user/v1/me w/o authentication"
HTTP_STATUS_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://$HOST:9999/user/v1/me 2>/dev/null)
[ "$HTTP_STATUS_CODE" == "401" ] && ok || failed

log "Calling broken-service directly"
RESPONSE=$(curl -s -H "Authorization: Bearer $ACCESS_TOKEN" http://$HOST:8282/test)
[ "$RESPONSE" == "1" ] && ok || failed

log "Calling broken-service directly w/o authentication"
HTTP_STATUS_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://$HOST:8282/test 2>/dev/null)
[ "$HTTP_STATUS_CODE" == "401" ] && ok || failed

log "Calling broken-service via edge-service"
RESPONSE=$(curl -s -H "Authorization: Bearer $ACCESS_TOKEN" http://$HOST:9999/brokenservice/test)
[ "$RESPONSE" == "1" ] && ok || failed

log "Calling broken-service via edge-service w/o authentication"
HTTP_STATUS_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://$HOST:9999/brokenservice/test 2>/dev/null)
[ "$HTTP_STATUS_CODE" == "401" ] && ok || failed

log "Calling working-service directly"
RESPONSE=$(curl -s -H "Authorization: Bearer $ACCESS_TOKEN" http://$HOST:8383/test)
[ "$RESPONSE" == "0" ] && ok || failed

log "Calling working-service directly w/o authentication"
HTTP_STATUS_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://$HOST:8383/test 2>/dev/null)
[ "$HTTP_STATUS_CODE" == "401" ] && ok || failed

log "Calling working-service via edge-service"
RESPONSE=$(curl -s -H "Authorization: Bearer $ACCESS_TOKEN" http://$HOST:9999/workingservice/test)
[ "$RESPONSE" == "0" ] && ok || failed

log "Calling working-service via edge-service w/o authentication"
HTTP_STATUS_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://$HOST:9999/workingservice/test 2>/dev/null)
[ "$HTTP_STATUS_CODE" == "401" ] && ok || failed
