package ch.zhaw.rpa.dogguruwebhookhandler.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsNextScene;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsPrompt;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsRequest;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsResponse;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsScene;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsSimple;
import ch.zhaw.rpa.dogguruwebhookhandler.uipath.UiPathOrchestratorRestClient;

@Component
public class DogImageHandler {

    @Autowired
    private UiPathOrchestratorRestClient client;
    
    public GoogleActionsResponse handleDogImageRequest(GoogleActionsRequest request){
        String rasse = request.getSession().getParams().get("rasse").toString();

        

        GoogleActionsResponse response;

        try {
            Thread.sleep(5000);
            response = GoogleActionsResponse.builder()
            .prompt(GoogleActionsPrompt.builder()
                    .firstSimple(GoogleActionsSimple.builder()
                            .speech("Here we go").build())
                    .build())
            .session(request.getSession())
            .scene(GoogleActionsScene.builder()
                .name(request.getScene().getName())
                .slotFillingStatus(request.getScene().getSlotFillingStatus())
                .slots(request.getScene().getSlots())
                .next(GoogleActionsNextScene.builder()
                    .name("Hundeinformationen_anzeigen")
                    .build())
                .build())
            .build();
        } catch (InterruptedException e) {
            response = GoogleActionsResponse.builder()
            .prompt(GoogleActionsPrompt.builder()
                    .firstSimple(GoogleActionsSimple.builder()
                            .speech("Es kam zu folgendem Fehler: " + e.getLocalizedMessage()).build())
                    .build())
            .session(request.getSession())
            .scene(request.getScene())
            .build();
        }

        /** Diesen Teil auslagern in Async-Funktion
        String releaseKey = client.getReleaseKeyByProcessKey("dog-guru-hunderassenlexikon-connector");

        JSONObject inputArguments = new JSONObject();
        inputArguments.put("rasse", rasse);

        Integer id = client.startJobAndGetId(releaseKey, inputArguments);

        JSONObject outputArguments = client.getJobById(id, 1000, 60);

        if(outputArguments == null || !outputArguments.getString("out_exceptionDescription").isEmpty()) {
            String exceptionMessage = (outputArguments == null ? "Das Suchen nach einem Bild ist fehlgeschlagen." : outputArguments.getString("out_exceptionDescription"));
            response = GoogleActionsResponse.builder()
            .prompt(GoogleActionsPrompt.builder()
                    .firstSimple(GoogleActionsSimple.builder()
                            .speech(exceptionMessage).build())
                    .build())
            .session(request.getSession())
            .scene(request.getScene())
            .build();
        } else {
            String dogImageUri = outputArguments.getString("out_imageUri");

            response = GoogleActionsResponse.builder()
            .prompt(GoogleActionsPrompt.builder()
                    .content(GoogleActionsContentImage.builder()
                        .image(GoogleActionsImage.builder()
                            .url(dogImageUri)
                            .alt("Bild von " + rasse)
                            .height(0)
                            .width(0)
                            .build())
                        .build())
                    .build())
            .session(request.getSession())
            .scene(request.getScene())
            .build();
    
        }  */

        return response;
    }
}
