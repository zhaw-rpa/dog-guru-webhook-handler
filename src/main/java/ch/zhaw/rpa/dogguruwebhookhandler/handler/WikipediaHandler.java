package ch.zhaw.rpa.dogguruwebhookhandler.handler;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2IntentMessage;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;
import ch.zhaw.rpa.dogguruwebhookhandler.restclients.WikipediaRestClient;

@Component
public class WikipediaHandler {

    private static final Integer EXTRACT_LENGTH = 500;

    @Autowired
    private WikipediaRestClient wikipediaRestClient;

    public GoogleCloudDialogflowV2IntentMessage handleDogDescriptionRequest(
            GoogleCloudDialogflowV2WebhookRequest request, GoogleCloudDialogflowV2IntentMessage msg, String format) {
        // Gesuchte Rasse auslesen
        String breed = request.getQueryResult().getOutputContexts().get(0).getParameters().get("breed").toString();

        // Beschreibung zu dieser Rasse erhalten
        String dogDescription = wikipediaRestClient.getWikipediaExtract(EXTRACT_LENGTH, breed);

        if (dogDescription.isEmpty()) {
            dogDescription = "No Wikipedia article found for this breed.";
        }

        // Wenn plaintext gewünscht, dann diesen aus dem HTML extrahieren
        if (format.equals("plaintext")) {
            dogDescription = Jsoup.parseBodyFragment(dogDescription).text();
        }

        // Rich-Content-Payload in Form von verschachtelten HashMaps aufbereiten
        // basierend auf https://cloud.google.com/dialogflow/es/docs/integrations/dialogflow-messenger?hl=en#rich
        Map<String, Object> accordionMap = new HashMap<>();
        accordionMap.put("type", "accordion");
        accordionMap.put("title", "Wikipedia-Description of " + breed);
        accordionMap.put("text", dogDescription);

        Map<String, Object> iconMap = new HashMap<>();
        iconMap.put("type", "chevron_right");
        iconMap.put("color", "#FF9800");

        Map<String, Object> linkMap = new HashMap<>();
        linkMap.put("type", "button");
        linkMap.put("text", "Wikipedia article");
        linkMap.put("link", wikipediaRestClient.getWikipediaEntryUrl(breed));
        linkMap.put("icon", iconMap);

        Object richContentInnerArray[] = new Object[] {accordionMap, linkMap}; 

        Object richContentOuterArray[] = new Object[] {richContentInnerArray};

        Map<String, Object> richContentMap = new HashMap<>();
        richContentMap.put("richContent", richContentOuterArray);
        msg.setPayload(richContentMap);

        return msg;
    }
}
