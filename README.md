
# Palaemon-conductor

---

The "palaemon-conductor" is a microservice is part of the cluster of microservices that constitute 
the PaMEAS Smart Evacuation Management and Rules system (PaMEAS-A). Specifically, this microservice 
is responsible for the orchestration  (via a series of REST API calls) of the functionality exposed by the rest of the
microservice in the PaMEAS cluster and implement the necessary flows 
to support the evacuation process. The "palaemon-conductor" microservice is build using the [Netflix Conductor](asd) sdk 
abd defines and implements the following flows:
- Re-route the passengers in case of evacuation paths becoming unavailable and additionally notify the crew for any deviation of the primary evacuation plan.
- Instruct the crew to assume emergency positions (and confirm their arrival at their designated posts).
- Notify and Alert the passengers about emergency situations and instruct them on the preparatory steps they must undertake.
- Assign and guide the passengers to the Muster stations (as those are defined in the vessel's evacuation plan).
- Detect (automatically or manually) generated passenger related issues and incidents.
- Assign crew member teams to the passenger issues/incidents and monitor their status and completion.

# Further Reading and Documentation

---
If you want to learn more about the "palaemon-conductor" microservice please read our 
[working document](https://docs.google.com/document/d/1ljmMZdKuIWhcCmA4jlquAxP8VplmNn1SXZUCWVLKp2o/edit?usp=sharing). 
The aforementioned document contains a brief overview of the functional requirements the PaMEAS-A 
functional requirements, architecture and defines in details the flows that implement them. 

To gain a better understanding of the overall functionality of PaMEAS the following presentations are
helpful:
- [PaMEAS (and PALAEMON Pilots) ICT Integration](https://docs.google.com/presentation/d/1ni99nXpgV1XGvfo6XNaR3cbe4MRncCj3/edit?usp=sharing&ouid=101096721707031783382&rtpof=true&sd=true)
- [PaMEAS Evacuation Messaging Policy](https://docs.google.com/presentation/d/1uxZ4Hoah89qz3MuUqt1RmGY8Dxf0upC6/edit?usp=sharing&ouid=101096721707031783382&rtpof=true&sd=true)
- [PaMEAS-A integration](https://docs.google.com/presentation/d/1cRt34HpJzM55kundaGE65re5CHmTzsvp/edit?usp=sharing&ouid=101096721707031783382&rtpof=true&sd=true)
- [PaMEAS-N and PaMEAS-Cell](https://docs.google.com/presentation/d/1xnB5cOLFCL9GC1_jkzBss-vrYs6-Vv5h/edit?usp=sharing&ouid=101096721707031783382&rtpof=true&sd=true)
- [PaMEAS-A Testing Scenarios](https://docs.google.com/presentation/d/178G2WV1pbgP8KswFuqrGacF0mGM67ERetdLD67w74MU/edit?usp=sharing)
- [PALAEMON People Management System and Storage Layer: Demo](https://docs.google.com/presentation/d/16W8H_h-qz2HTbRwcXpGJ9RnrYqZxCAZ8/edit?usp=sharing&ouid=101096721707031783382&rtpof=true&sd=true)

# Code

---

*Disclaimer: Although we tested the code extensively, the "palaemon-conductor" is a research 
prototype that may contain bugs. We take no responsibility for and give no warranties in respect of using the code.*

## Layout 

The "palaemon-conductor" microservice is implemented 
via a Spring boot application.  As a result it adheres to the typical Spring boot structure:
 - `src/main` contains the main logic of the application
 - `src/main/resources/flows` contains the json definition of the necessary conductor task and workflows
 - `src/test` contains the executed unit tests


# Deployment

---
The "palaemon-conductor" microservice is implemented via Spring Boot and is Dockerized in order to
facilitate its deployment. As a result this microservice can be easily deployed using:
```
docker run --name endimion13:palaemon-conductor -p 6379:6379 -d 
```
Additionally, a typical Docker-compose file for its deployment would look as follows:
```
 
version: '2'
services:
  palaemon-db-proxy:
    image:  endimion13/palaemon-db-proxy:0.0.1a
    environment:
      - DFB_URI=dfb.palaemon.itml.gr
      - DFB_PORT=443
      - ES_USER=esuser
      - ES_PASS=kyroCMA2081!
      - KAFKA_URI_WITH_PORT=dfb.palaemon.itml.gr:30093
      - KAFKA_TRUST_STORE_LOCATION=/store/truststore.jks
      - KAFKA_TRUST_STORE_PASSWORD=****
      - KAFKA_KEYSTORE_LOCATION=/store/keystore.jks
      - KAFKA_KEY_STORE_PASSWORD=***
      - SSL_KEYSTORE_PASS=***
      - SSL_ROOT_CERTIFICATE=/store/dfb.palaemon.itml.crt
      - OAUTH_ISSUER_URI=https://dss1.aegean.gr/auth/realms/palaemon
      - PUBLIC_ENCRYPTION_KEY_PATH=/store/public.key
      - PRIVATE_ENCRYPTION_KEY_PATH=/store/private.key
    ports:
      - 8090:8080
    volumes:
      - /home/ni/code/java/palaemon-db-proxy/:/store
```




# Flows

---

## Flow 1. Detect Geofence Unavailability & Reroute
During  the execution of the emergency evacuations process 
part of  evacuation paths may become unavailable (or highly congested constituting them effectively
unavailable). In such a cases PaMEAS-A becomes notified about the event and the "palaemon-conductor"
triggers an appropriate flow to handle the situation. Specifically, this orchestrates via this flow
the assignment of newer evacuation paths to the passengers whose originally designated path 
is no longer available, notifies the passengers and ensures that the crew members are up-to-date with 
the newer assignments (to ensure no conflicting instructions are passed to the passengers)
```
curl --request POST \
  --url 'http://localhost:8080/api/workflow/detect_blocked_geofence?priority=0' \
  --header 'Content-Type: application/json' \
  --cookie 'JSESSIONID=87C4552F1C9A9AFB7D68CD506C4372DB; connect.sid=s%253AaB3g_-fPeWTgaRTWaTsLJdRfTDdfOrOz.WCVPfIJ0KgDsHKa%252B%252BAKZegUjj4bdtMRnDgCbiWtAtVk' \
  --data '{
  "geofence": "geo1",
	"status":"blocked"
}'
```

## Flow 2. Instruct Crew to move to positions
During emergency evacuations the crew needs to become quickly alerted in order to assume their predefined 
emergency position based on their training, emergency role and ships emergency evacuation plan. This flow initiates the alerting of the crew members to instruct them to assume
the aforementioned positions. 
This flow can be triggered by a REST call as follows:
```
curl --request POST \
  --url 'http://localhost:8080/api/workflow/test_work_flow_22?priority=0' \
  --header 'Content-Type: application/json' \
  --cookie 'JSESSIONID=87C4552F1C9A9AFB7D68CD506C4372DB; connect.sid=s%253AaB3g_-fPeWTgaRTWaTsLJdRfTDdfOrOz.WCVPfIJ0KgDsHKa%252B%252BAKZegUjj4bdtMRnDgCbiWtAtVk' \
  --data '{
  "phase": "4",
	"task_id":"4.1"
}'
```
## Flow 3. Confirm Crew positions
After reaching their emergency posts the crew members fire notifications to the PaMEAS-A IT event bus. 
The "paemas-conductor" microservice receives these notifications and verifies if all crew members have assumed
their emergency roles and if this holds notifies the PaMEAS-A IT ecosystem.  
This flow can be triggered by a REST call as follows:
```
curl --request POST \
  --url 'http://localhost:8080/api/workflow/confirm_crew_positions?priority=0' \
  --header 'Content-Type: application/json' \
  --cookie 'JSESSIONID=87C4552F1C9A9AFB7D68CD506C4372DB; connect.sid=s%253AaB3g_-fPeWTgaRTWaTsLJdRfTDdfOrOz.WCVPfIJ0KgDsHKa%252B%252BAKZegUjj4bdtMRnDgCbiWtAtVk' \
  --data '{
  "hashMacAddress": "b356d0ea840b0550a2acb26acea468a80b895607d55db030f0b12abf5e8ce759",
	"in_position":"true"
}'
```
## Flow 4. Alerting passengers 
This flow is triggered after receiving the confirmation from the event bus that all crew members have 
assumed their designated emergency positions. Specifically, this flow proceeds to alert the passengers
via personalized messaging about the emergency situation and preparing the about subsequent actions
This flow can be triggered by a REST call as follows:
```
curl --request POST \
  --url 'http://localhost:8080/api/workflow/alert_passengers?priority=0' \
  --header 'Content-Type: application/json' \
  --cookie 'JSESSIONID=87C4552F1C9A9AFB7D68CD506C4372DB; connect.sid=s%253AaB3g_-fPeWTgaRTWaTsLJdRfTDdfOrOz.WCVPfIJ0KgDsHKa%252B%252BAKZegUjj4bdtMRnDgCbiWtAtVk' \
  --data '{
   "phase": "5",
	"task_id":"5.1"}'
```
## Flow 5. Mustering Instructions to passengers
This flow is triggered after the end of Flow 4. Via the actions implemented by this flow
the passengers will receive personalized messages containing their assigned Muster Stations and
the evacuation path they are to follow. Passengers with mobility/health/pregnancy issues are instead 
notified that assistance is on the way to guide them with safety to the assigned Muster Stations.
This flow can be triggered by a REST call as follows:
```
curl --request POST \
  --url 'http://localhost:8080/api/workflow/test_workflow_42?priority=0' \
  --header 'Content-Type: application/json' \
  --cookie 'JSESSIONID=87C4552F1C9A9AFB7D68CD506C4372DB; connect.sid=s%253AaB3g_-fPeWTgaRTWaTsLJdRfTDdfOrOz.WCVPfIJ0KgDsHKa%252B%252BAKZegUjj4bdtMRnDgCbiWtAtVk' \
  --data '{
   "phase": "6",
	"task_id":"6.2"
}'
```

## Flow 6. Re-route Passengers Blockage
This flow implements the necessary functionality to re-route the passengers through alternative paths in 
case a section of their designated evacuation path becomes blocked. Additionally, via this flow
the crew members are notified about the route assignment changes so as not to create confusion and
contradicting messages. This flow is contained in Flow 1 (Handle Blocked Geofences).

## Flow 7. Re-route Passengers Congestion
This flow implements the necessary functionality to re-route the passengers through alternative paths in
case a section of their designated evacuation path becomes congested. Additionally, via this flow
the crew members are notified about the route assignment changes so as not to create confusion and
contradicting messages. This flow is contained in Flow 1 (Handle Unavailable Geofence).

## Flow 8. Generate Passenger Initiated Issue
This flow implements the necessary functionality to handle the request for generation of a Passenger Issue
initiated by the Passengers mobile device. Specifically, after submitting via their mobile app such a 
request this microservice process it links it to a specific user health, location, mobility and pregnancy 
profile and submits it to the PaMEAS system again for assignment and resolution
This flow can be triggered by a REST call as follows:
```
curl --request POST \
  --url 'http://localhost:8080/api/workflow/test_workflow_42?priority=0' \
  --header 'Content-Type: application/json' \
  --cookie 'JSESSIONID=87C4552F1C9A9AFB7D68CD506C4372DB; connect.sid=s%253AaB3g_-fPeWTgaRTWaTsLJdRfTDdfOrOz.WCVPfIJ0KgDsHKa%252B%252BAKZegUjj4bdtMRnDgCbiWtAtVk' \
  --data '{
   "phase": "6",
	"task_id":"6.2"
}'
```


