
# Palaemon-conductor

---

The "palaemon-conductor" is a microservice is part of the cluster of microservices that constitute 
the PaMEAS Smart Evacuation Management and Rules system (PaMEAS-A). Specifically, this microservice 
is responsible for the orchestration  (via a series of REST API calls) of the functionality exposed by the rest of the
microservice in the PaMEAS cluster and implement the necessary flows 
to support the evacuation process. Specifically, the "palaemon-conductor" microservice defines and
implements the following flows:
- Re-route the passengers in case of evacuation paths becoming unavailable and additionally notify the crew for any deviation of the primary evacuation plan.
- Instruct the crew to assume emergency positions (and confirm their arrival at their designated posts).
- Notify and Alert the passengers about emergency situations and instruct them on the preparatory steps they must undertake.
- Assign and guide the passengers to the Muster stations (as those are defined in the vessel's evacuation plan).
- Detect (automatically or manually) generated passenger related issues and incidents.
- Assign crew member teams to the passenger issues/incidents and monitor their status and completion.

# Flows

---

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
	"task_id":"5.1"}
'
```
## Flow 5. Alerting passengers 