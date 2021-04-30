package ch.zhaw.rpa.dogguruwebhookhandler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsPrompt;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsRequest;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsResponse;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsSimple;
import ch.zhaw.rpa.dogguruwebhookhandler.handler.DogImageHandler;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping(value = "api")
public class GoogleActionsDogGuruImageHandler {

    @Autowired
    private DogImageHandler dogImageHandler;

    @GetMapping(value = "/test")
    public String testApi() {
        System.out.println("!!!!!!!!! Test Request received");
        return "Yes, it works";
    }

    @PostMapping(value = "/main-handler")
    public GoogleActionsResponse handleGoogleActionsRequest(@RequestBody GoogleActionsRequest body) {
        // Handler ermitteln
        String handlerName = body.getHandler().getName();

        GoogleActionsResponse response;

        if (handlerName.equals("getDogImageHandler")) {
            response = dogImageHandler.handleDogImageRequest(body);
        } else if (handlerName.equals("getDogDescriptionHandler")) {
            // Wikipedia API mit RestTemplate: https://en.wikipedia.org/w/api.php?action=query&prop=extracts&exchars=500&titles=Yorkshire%20Terrier
            response = null;
        } else {
            // Response no handler found zusammenstellen
            response = GoogleActionsResponse.builder()
                .prompt(GoogleActionsPrompt.builder()
                        .firstSimple(GoogleActionsSimple.builder()
                                .speech("Es gibt keinen Handler für '" + handlerName + "'").build())
                        .build())
                .session(body.getSession())
                .scene(body.getScene())
                .build();
        }

        return response;
    }

}
