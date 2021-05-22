package ch.zhaw.rpa.dogguruwebhookhandler.handler;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.zhaw.rpa.dogguruwebhookhandler.asynchandling.GoogleActionsSessionState;
import ch.zhaw.rpa.dogguruwebhookhandler.asynchandling.GoogleActionsSessionStateService;
import ch.zhaw.rpa.dogguruwebhookhandler.asynchandling.UiPathAsyncJobHandler;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsContentImage;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsImage;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsNextScene;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsPrompt;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsRequest;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsResponse;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsScene;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsSimple;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsSuggestion;

@Component
public class DogImageHandler {

    @Autowired
    private UiPathAsyncJobHandler uiPathAsyncJobHandler;

    @Autowired
    private GoogleActionsSessionStateService stateService;

    public GoogleActionsResponse handleDogImageRequest(GoogleActionsRequest request) {
        // Variablen initialisieren/auslesen
        GoogleActionsResponse response;
        String rasse = request.getSession().getParams().get("rasse").toString();

        // Session Id auslesen
        String sessionId = request.getSession().getId();

        // Prüfen, ob Session Id bereits verwaltet ist
        GoogleActionsSessionState sessionState = stateService.getSessionStateBySessionId(sessionId);

        // Wenn die Session Id noch nicht verwaltet ist (erster Request)
        if (sessionState == null) {
            // Neuen Session State erstellen
            sessionState = GoogleActionsSessionState.builder().googleActionsSessionId(sessionId)
                    .googleActionsFirstRequestReceived(new Date()).build();

                stateService.addSessionState(sessionState);

            // Async den Auftrag für den UiPath-Job erteilen
            uiPathAsyncJobHandler.asyncRunUiPathDogGuruHunderassenlexikonConnector(sessionState, rasse);
            System.out.println("Log: AsyncHandler aufgerufen für Session Id " + sessionId);

            // Etwas Zeit "schinten", aber so, dass Google Actions noch nicht abbricht und
            // Text für Benutzer festlegen
            response = getResponseOfTypePleaseWait(
                    "Es dauert normalerweise bis zu einer Minute, bis die Daten angezeigt werden. Wähle 'Weiter' sobald Du erneut prüfen willst, ob das Bild bereit ist.",
                    request);
        }
        // Wenn ein zweiter, dritter, usw. Request vorhanden ist
        else {
            // Wenn der UiPath Job noch am laufen ist
            if (sessionState.getUiPathJobState().equals("created")) {
                // Etwas Zeit "schinten", aber so, dass Google Actions noch nicht abbricht und
                // Text für Benutzer festlegen
                response = getResponseOfTypePleaseWait(
                        "Der Bot ist nach wie vor beschäftigt. Wähle 'Weiter' sobald Du erneut prüfen willst, ob das Bild bereit ist.",
                        request);
            }
            // Wenn der UiPath Job abgeschlossen wurde
            else if (sessionState.getUiPathJobState().equals("successfull")) {
                String dogImageUri = sessionState.getOutputArguments().getString("out_imageUri");

                response = GoogleActionsResponse.builder()
                        .prompt(GoogleActionsPrompt.builder()
                                .content(GoogleActionsContentImage.builder()
                                        .image(GoogleActionsImage.builder().url(dogImageUri).alt("Bild von " + rasse)
                                                .height(0).width(0).build())
                                        .build())
                                .build())
                        .session(request.getSession()).scene(request.getScene()).build();
            }
            // In allen anderen Fällen (UiPath Job nicht erstellt werden konnte oder
            // fehlgeschlagen)
            else {
                response = GoogleActionsResponse.builder()
                        .prompt(GoogleActionsPrompt.builder()
                                .firstSimple(GoogleActionsSimple.builder()
                                        .speech((sessionState.getUiPathExceptionMessage().isEmpty()
                                                ? "Es ist ein unbekannter Fehler aufgetreten."
                                                : "Folgender Fehler ist aufgetreten: "
                                                        + sessionState.getUiPathExceptionMessage()))
                                        .build())
                                .build())
                        .session(request.getSession()).scene(request.getScene()).build();
            }

        }

        return response;
    }

    private GoogleActionsResponse getResponseOfTypePleaseWait(String promptText, GoogleActionsRequest request) {
        GoogleActionsResponse response;

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            promptText = "Es kam zu folgendem Fehler: " + e.getLocalizedMessage()
                    + "Wählen Sie 'Weiter', wenn Sie es nochmals versuchen wollen.";
        }

        // Response zusammenbauen
        response = GoogleActionsResponse.builder()
                .prompt(GoogleActionsPrompt.builder()
                        .firstSimple(GoogleActionsSimple.builder().speech(promptText).build())
                        .suggestion(GoogleActionsSuggestion.builder().title("Weiter").build())
                        .suggestion(GoogleActionsSuggestion.builder().title("Abbrechen").build()).build())
                .session(request.getSession())
                .scene(GoogleActionsScene.builder().name(request.getScene().getName())
                        .slotFillingStatus(request.getScene().getSlotFillingStatus())
                        .slots(request.getScene().getSlots())
                        .next(GoogleActionsNextScene.builder().name("Hundeabbildung_anzeigen").build()).build())
                .build();

        return response;
    }
}
