package ch.zhaw.rpa.dogguruwebhookhandler.handler;

import java.util.concurrent.CompletableFuture;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsContentImage;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsImage;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsPrompt;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsRequest;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsResponse;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsSimple;
import ch.zhaw.rpa.dogguruwebhookhandler.uipath.UiPathOrchestratorRestClient;

@Component
public class DogImageHandler {

    @Autowired
    private UiPathOrchestratorRestClient client;
    
    @Async
    public CompletableFuture<GoogleActionsResponse> handleDogImageRequest(GoogleActionsRequest request){
        String rasse = request.getSession().getParams().get("rasse").toString();

        String releaseKey = client.getReleaseKeyByProcessKey("dog-guru-hunderassenlexikon-connector");

        JSONObject inputArguments = new JSONObject();
        inputArguments.put("rasse", rasse);

        Integer id = client.startJobAndGetId(releaseKey, inputArguments);

        JSONObject outputArguments = client.getJobById(id, 1000, 60);

        GoogleActionsResponse response;

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
    
        }

        return CompletableFuture.completedFuture(response);
        //return response;
    }
}
