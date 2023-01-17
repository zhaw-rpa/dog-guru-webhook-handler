package ch.zhaw.rpa.dogguruwebhookhandler.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2IntentMessage;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2IntentMessageText;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2QueryResult;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookResponse;

import ch.zhaw.rpa.dogguruwebhookhandler.handler.UiPathHandler;
import ch.zhaw.rpa.dogguruwebhookhandler.handler.WikipediaHandler;

@RestController
@RequestMapping(value = "api")
public class DialogFlowWebhookController {

    @Autowired
    private GsonFactory gsonFactory;

    @Autowired
    private UiPathHandler uiPathHandler;

    @Autowired
    private WikipediaHandler wikipediaHandler;

    @GetMapping(value = "/test")
    public String testApi() {
        System.out.println("!!!!!!!!! Test Request received");
        return "Yes, it works";
    }

    @PostMapping(value = "/dialogflow-main-handler", produces = { MediaType.APPLICATION_JSON_VALUE })
    public String webhook(@RequestBody String rawData) throws IOException {
        // Response instanzieren
        GoogleCloudDialogflowV2WebhookResponse response = new GoogleCloudDialogflowV2WebhookResponse();

        // Antwort-Message instanzieren
        GoogleCloudDialogflowV2IntentMessage msg = new GoogleCloudDialogflowV2IntentMessage();

        // RequestBody parsen
        GoogleCloudDialogflowV2WebhookRequest request = gsonFactory
                .createJsonParser(rawData)
                .parse(GoogleCloudDialogflowV2WebhookRequest.class);

        // Query Result erhalten
        GoogleCloudDialogflowV2QueryResult queryResult = request.getQueryResult();

        // Intent auslesen
        String intent = queryResult.getIntent().getDisplayName();

        // Je nach Intent anderen Handler aufrufen oder Response zusammenbauen
        if (intent.equals("ImageIntent") || intent.equals("ContinueGetImageIntent")) {
            // Bild via UiPath erhalten
            msg = uiPathHandler.handleUiPathRequest(request, intent, msg);
        } else if (intent.equals("DescriptionIntent") || intent.equals("ContinueGetDescriptionIntent")) {
            // Informationstyp auslesen
            String informationstyp = request.getQueryResult().getOutputContexts().get(0).getParameters()
                    .get("InformationType").toString();

            switch (informationstyp.toLowerCase()) {
                case "hundeo":
                    // Beschreibung von Hundeo via UiPath erhalten
                    msg = uiPathHandler.handleUiPathRequest(request, intent, msg);
                    break;
                case "html":
                    // Beschreibung von Wikipedia im HTML-Format erhalten
                    msg = wikipediaHandler.handleDogDescriptionRequest(request, msg, "html");
                    break;
                case "plain":
                    // Beschreibung von Wikipedia in Plain Text erhalten
                    msg = wikipediaHandler.handleDogDescriptionRequest(request, msg, "plaintext");
                    break;
                default:
                    break;
            }
        } else {
            // Response no handler found zusammenstellen
            GoogleCloudDialogflowV2IntentMessageText text = new GoogleCloudDialogflowV2IntentMessageText();
            text.setText(List.of("There's no handler for '" + intent + "'"));
            msg.setText(text);
        }

        // Webhook-Response für Dialogflow aufbereiten und zurückgeben
        response.setFulfillmentMessages(List.of(msg));
        StringWriter stringWriter = new StringWriter();
        JsonGenerator jsonGenerator = gsonFactory.createJsonGenerator(stringWriter);
        jsonGenerator.enablePrettyPrint();
        jsonGenerator.serialize(response);
        jsonGenerator.flush();
        return stringWriter.toString();
    }
}