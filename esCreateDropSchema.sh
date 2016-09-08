#!/bin/sh
API_KEY=
GACCOUNT=


curl -X DELETE "https://analytics.api.appdynamics.com/events/schema/devops2" -H"X-Events-API-AccountName:customer1_dd34e97c-2906-4a86-a005-8c3efd1daa08" -H"X-Events-API-Key:0d91579c-3266-4645-9348-4bf268f11a4f"

echo $?

curl -v -X POST "https://analytics.api.appdynamics.com/events/schema/devops2" -H"X-Events-API-AccountName:customer1_dd34e97c-2906-4a86-a005-8c3efd1daa08" -H"X-Events-API-Key:0d91579c-3266-4645-9348-4bf268f11a4f" -H"Content-type:application/vnd.appd.events+json;v=1" -d '{"schema" : { "RackTime": "string", "Dc": "string", "IPv4": "string", "IPv6": "string", "Account": "string", "EventID": "string", "Host": "string", "InstanceID": "string", "Process": "string", "Region": "string", "Status": "string", "Version": "string" } }'

echo $?
