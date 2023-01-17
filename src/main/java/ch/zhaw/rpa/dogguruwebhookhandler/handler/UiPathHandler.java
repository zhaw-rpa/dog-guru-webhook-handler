package ch.zhaw.rpa.dogguruwebhookhandler.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2IntentMessage;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2IntentMessageText;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;

import ch.zhaw.rpa.dogguruwebhookhandler.asynchandling.DialogFlowSessionState;
import ch.zhaw.rpa.dogguruwebhookhandler.asynchandling.DialogFlowSessionStateService;
import ch.zhaw.rpa.dogguruwebhookhandler.asynchandling.UiPathAsyncJobHandler;

@Component
public class UiPathHandler {

        @Autowired
        private UiPathAsyncJobHandler uiPathAsyncJobHandler;

        @Autowired
        private DialogFlowSessionStateService stateService;

        public GoogleCloudDialogflowV2IntentMessage handleUiPathRequest(GoogleCloudDialogflowV2WebhookRequest request,
                        String intent, GoogleCloudDialogflowV2IntentMessage msg) {

                // breed auslesen
                String breed = request.getQueryResult().getOutputContexts().get(0).getParameters().get("breed")
                                .toString();

                // Session Id auslesen
                String sessionId = request.getSession();

                // Prüfen, ob Session Id bereits verwaltet ist
                DialogFlowSessionState sessionState = stateService.getSessionStateBySessionId(sessionId);

                // Wenn die Session Id noch nicht verwaltet ist (erster Request)
                if (sessionState == null) {
                        // Neuen Session State erstellen
                        sessionState = DialogFlowSessionState.builder().DialogFlowSessionId(sessionId)
                                        .DialogFlowFirstRequestReceived(new Date()).uiPathExceptionMessage("").build();

                        stateService.addSessionState(sessionState);

                        // Async den Auftrag für den UiPath-Job erteilen
                        uiPathAsyncJobHandler.asyncRunUiPathDogGuruHunderassenlexikonConnector(sessionState, breed);
                        System.out.println("!!!!!!!!! AsyncHandler aufgerufen für Session Id " + sessionId);

                        // Etwas Zeit "schinten", aber so, dass DialogFlow noch nicht abbricht und
                        // Text für Benutzer festlegen
                        msg = getResponseOfTypePleaseWait(
                                        "It can take a minute to get your information from the original source. Click on 'Continue' as soon you want to see, if the information is already there.",
                                        request, intent, msg);
                }
                // Wenn ein zweiter, dritter, usw. Request vorhanden ist
                else {
                        // Wenn der UiPath Job noch am laufen ist
                        if (sessionState.getUiPathJobState().equals("created")) {
                                // Etwas Zeit "schinten", aber so, dass Google Actions noch nicht abbricht und
                                // Text für Benutzer festlegen
                                msg = getResponseOfTypePleaseWait(
                                                "I am still getting your desired information. Click on 'Continue' if you want to poll again.",
                                                request, intent, msg);
                        }
                        // Wenn der UiPath Job abgeschlossen wurde
                        else if (sessionState.getUiPathJobState().equals("successfull")) {
                                String dogDetailsUri = sessionState.getOutputArguments()
                                                .getString("out_uriDetailsPage");

                                // Wenn ein Bild angefragt wurde
                                if (intent.equals("ImageIntent") || intent.equals("ContinueGetImageIntent")) {
                                        String dogImageUri = sessionState.getOutputArguments()
                                                        .getString("out_imageUri");

                                        // Rich-Content-Payload in Form von verschachtelten HashMaps aufbereiten
                                        // basierend auf
                                        // https://cloud.google.com/dialogflow/es/docs/integrations/dialogflow-messenger?hl=en#rich
                                        Map<String, Object> imageMap = new HashMap<>();
                                        imageMap.put("type", "image");
                                        imageMap.put("rawUrl", dogImageUri);
                                        imageMap.put("accessibilityText", "Image of " + breed);

                                        Map<String, Object> iconMap = new HashMap<>();
                                        iconMap.put("type", "chevron_right");
                                        iconMap.put("color", "#FF9800");

                                        Map<String, Object> linkMap = new HashMap<>();
                                        linkMap.put("type", "button");
                                        linkMap.put("text", "Show Hundeo article");
                                        linkMap.put("link", dogDetailsUri);
                                        linkMap.put("icon", iconMap);

                                        Object richContentInnerArray[] = new Object[] { imageMap, linkMap };

                                        Object richContentOuterArray[] = new Object[] { richContentInnerArray };

                                        Map<String, Object> richContentMap = new HashMap<>();
                                        richContentMap.put("richContent", richContentOuterArray);
                                        msg.setPayload(richContentMap);
                                }
                                // Wenn eine Hundeo-Beschreibung angefragt wurde
                                else {
                                        String dogDescription = sessionState.getOutputArguments()
                                                        .getString("out_dogDescription");

                                        // Rich-Content-Payload in Form von verschachtelten HashMaps aufbereiten
                                        // basierend auf
                                        // https://cloud.google.com/dialogflow/es/docs/integrations/dialogflow-messenger?hl=en#rich
                                        Map<String, Object> accordionMap = new HashMap<>();
                                        accordionMap.put("type", "accordion");
                                        accordionMap.put("title", "Hundeo-Description (German) of " + breed);
                                        accordionMap.put("text", dogDescription);

                                        Map<String, Object> iconMap = new HashMap<>();
                                        iconMap.put("type", "chevron_right");
                                        iconMap.put("color", "#FF9800");

                                        Map<String, Object> linkMap = new HashMap<>();
                                        linkMap.put("type", "button");
                                        linkMap.put("text", "Original Hundeo-article");
                                        linkMap.put("link", dogDetailsUri);
                                        linkMap.put("icon", iconMap);

                                        Object richContentInnerArray[] = new Object[] { accordionMap, linkMap };

                                        Object richContentOuterArray[] = new Object[] { richContentInnerArray };

                                        Map<String, Object> richContentMap = new HashMap<>();
                                        richContentMap.put("richContent", richContentOuterArray);
                                        msg.setPayload(richContentMap);
                                }

                                stateService.removeSessionState(sessionState);
                        }
                        // In allen anderen Fällen (UiPath Job nicht erstellt werden konnte oder
                        // fehlgeschlagen)
                        else {
                                GoogleCloudDialogflowV2IntentMessageText text = new GoogleCloudDialogflowV2IntentMessageText();
                                text.setText(List.of((sessionState.getUiPathExceptionMessage().isEmpty()
                                                ? "An unexpected error occured."
                                                : "The following error occured: "
                                                                + sessionState.getUiPathExceptionMessage())));
                                msg.setText(text);
                                stateService.removeSessionState(sessionState);
                        }

                }

                return msg;
        }

        private GoogleCloudDialogflowV2IntentMessage getResponseOfTypePleaseWait(String promptText,
                        GoogleCloudDialogflowV2WebhookRequest request,
                        String intent, GoogleCloudDialogflowV2IntentMessage msg) {
                try {
                        Thread.sleep(4000);
                } catch (InterruptedException e) {
                        promptText = "The following error occured: " + e.getLocalizedMessage()
                                        + "Click on 'Continue' if you want to try it again.";
                }

                // Rich-Content-Payload in Form von verschachtelten HashMaps aufbereiten
                // basierend auf
                // https://cloud.google.com/dialogflow/es/docs/integrations/dialogflow-messenger?hl=en#rich
                String textArray[] = new String[] { promptText };

                Map<String, Object> descriptionMap = new HashMap<>();
                descriptionMap.put("type", "description");
                descriptionMap.put("title", "Please wait ...");
                descriptionMap.put("text", textArray);

                Map<String, Object> parametersMap = new HashMap<>();

                Map<String, Object> eventMap = new HashMap<>();
                eventMap.put("name",
                                (intent.equals("ContinueGetImageIntent") || intent.equals("ImageIntent")
                                                ? "ContinueImageEvent"
                                                : "ContinueDescriptionEvent"));
                eventMap.put("languageCode", "en");
                eventMap.put("parameters", parametersMap);

                Map<String, Object> iconMap = new HashMap<>();
                iconMap.put("type", "chevron_right");
                iconMap.put("color", "#FF9800");

                Map<String, Object> linkMap = new HashMap<>();
                linkMap.put("type", "button");
                linkMap.put("text", "Continue");
                linkMap.put("event", eventMap);
                linkMap.put("icon", iconMap);

                Object richContentInnerArray[] = new Object[] { descriptionMap, linkMap };

                Object richContentOuterArray[] = new Object[] { richContentInnerArray };

                Map<String, Object> richContentMap = new HashMap<>();
                richContentMap.put("richContent", richContentOuterArray);
                msg.setPayload(richContentMap);

                return msg;
        }
}
