package gr.aegean.palaemon.conductor;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aegean.palaemon.conductor.model.TO.SrapTO;
import org.junit.Test;

public class TestSrapParsing {


    @Test
    public void testSrapMessageState1() {

        try {
            String receivedMessage = " {\n" +
                    "\"messageId\": \"uuid.uuid1().urn\",\n" +
                    "\"timestamp\": \"datetime.datetime.now().\",\n" +
                    "\"sender\": \"'SRAP'\",\n" +
                    "\"SRAP model\": \"Situation Assessment\",\n" +
                    "\"Effectiveness of mitigation measures\": \"Effective/ Not effective\",\n" +
                    "\"Passengers proximity to hazards\": \"Low/Medium/High\",\n" +
                    "\"Status of Passive containment\": \"Effective/Not effective\",\n" +
                    "\"Spreading\": \"Contained/ Not contained\",\n" +
                    "\"Structural Integrity\": \"Not compromised/ Compromised\",\n" +
                    "\"Stability\": \"Sufficient/ Not sufficient\",\n" +
                    "\"Hull status\": \"Safe/ Unsafe\",\n" +
                    "\"Ability to communicate\": \"Fully operational/ Degraded, Not operational\",\n" +
                    "\"Critical system status\": \"Fully operational/ Degraded, Not operational\",\n" +
                    "\"Vessel Status\": \"Safe/ Unsafe\",\n" +
                    "\"Pax vulnerability onboard\": \"High/Moderate/Low\",\n" +
                    "\"Situation Assessment\": \"Sound GA/No sound GA\"}";
            ObjectMapper mapper = new ObjectMapper();
            SrapTO result = mapper.readValue(receivedMessage, SrapTO.class);
            System.out.println(result.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }


    @Test
    public void testSrapMessageState2() {
        try {
            String receivedMessage = "{\n" +
                    "\"messageId\": \"uuid.uuid1().urn\",\n" +
                    "\"timestamp\": \"datetime.datetime.now().strftime\",\n" +
                    "\"sender\": \"SRAP\",\n" +
                    "\"SRAP model\": \"Mustering Assessment\",\n" +
                    "\"Individual status\": {\n" +
                    "\"2549\": \"'Assistance required'\",\n" +
                    "\"2552\": \"'Assistance required'\",\n" +
                    "\"2553\": \"'Movement delayed'\",\n" +
                    "\"2554\": \"'Assistance required'\",\n" +
                    "\"2555\": \"'Movement delayed'\"\n" +
                    "},\n" +
                    "\"Escape routes\": {\n" +
                    "\"Z1D9\": \"'Open'\",\n" +
                    "\"Z2D9\": \"'Distrupted'\",\n" +
                    "\"Z3D9\": \"'Open'\",\n" +
                    "\"Z4D9\": \"'Open'\",\n" +
                    "\"Z1D8\": \"'Closed'\",\n" +
                    "\"Z2D8\": \"'Closed'\",\n" +
                    "\"Z3D8\": \"'Opened'\",\n" +
                    "\"Z4D8\": \"'Disrupted'\",\n" +
                    "\"MSA\": \"'Opened'\",\n" +
                    "\"MSB\": \"'Closed'\",\n" +
                    "\"MSC\": \"'Opened'\",\n" +
                    "\"MSD\": \"'Opened'\"\n" +
                    "},\n" +
                    "\"Group performance\": {\n" +
                    "\"Z1D9\": \"'Low'\"\n" +
                    "},\n" +
                    "\"Risk of delay\": {\n" +
                    "\"Z1D9\": \"ow\",\n" +
                    "\"MSD\": \"Zero\"\n" +
                    "}\n" +
                    "}";
            ObjectMapper mapper = new ObjectMapper();
            SrapTO result = mapper.readValue(receivedMessage, SrapTO.class);
            System.out.println(result.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }


    @Test
    public void srapDataParseState2B() {
        try {
            String receivedMessage = "{\n" +
                    "\"messageId\": \"uuid.uuid1().urn\",\n" +
                    "\"timestamp\": \"datetime.datetime.now().strftime\",\n" +
                    "\"sender\": \"SRAP\",\n" +
                    "\"SRAP model\": \"Pre-Abandonment Assessment\",\n" +
                    "\"Status\": \"Stay/abandon\",\n" +
                    "\"Urgency for abandonment\":\"High/Medium/Low\"\n" +
                    "}";
            ObjectMapper mapper = new ObjectMapper();
            SrapTO result = mapper.readValue(receivedMessage, SrapTO.class);
            System.out.println(result.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }



    @Test
    public void testSrapPassengerIncident() {
        try {
            String receivedMessage =  "{\n" +
                    "  \"messageId\": \"\",\n" +
                    "  \"timestamp\": \"\",\n" +
                    "  \"sender\": \"SRAP\",\n" +
                    "  \"SRAP model\": \"Mustering Assessment\",\n" +
                    "  \"Individual status\": {\n" +
                    "    \"2549\": \"Assistance required\",\n" +
                    "    \"2552\": \"Assistance required\",\n" +
                    "    \"2554\": \"Assistance required\"\n" +
                    "  },\n" +
                    "  \"Escape routes\": {\n" +
                    "    \"Z1D9\": \"Open\",\n" +
                    "    \"Z2D9\": \"Distrupted\",\n" +
                    "    \"Z3D9\": \"Open\"\n" +
                    "  },\n" +
                    "  \"Group performance\": {\n" +
                    "    \"Z1D9\": \"Low\",\n" +
                    "    \"Z2D9\": \"Medium\",\n" +
                    "    \"Z3D9\": \"Low\",\n" +
                    "    \"Z4D9\": \"High\"\n" +
                    "  },\n" +
                    "  \"Risk of delay\": {\n" +
                    "    \"Z1D9\": \"Low\",\n" +
                    "    \"Z2D9\": \"Medium\"\n" +
                    "  }\n" +
                    "}\n" +
                    "\n" +
                    "\n";
            ObjectMapper mapper = new ObjectMapper();
            SrapTO result = mapper.readValue(receivedMessage, SrapTO.class);
            System.out.println(result.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }


}
