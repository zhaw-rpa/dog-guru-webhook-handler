package ch.zhaw.rpa.dogguruwebhookhandler.controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsPrompt;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsRequest;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsResponse;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsSimple;
import ch.zhaw.rpa.dogguruwebhookhandler.handler.DogDescriptionHandler;
import ch.zhaw.rpa.dogguruwebhookhandler.handler.DogImageHandler;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping(value = "api")
public class GoogleActionsDogGuruHandler {

    @Autowired
    private DogImageHandler dogImageHandler;

    @Autowired
    private DogDescriptionHandler dogDescriptionHandler;

    @GetMapping(value = "/test")
    public String testApi() {
        System.out.println("!!!!!!!!! Test Request received");
        return "Yes, it works";
    }

    @PostMapping(value = "/main-handler")
    public GoogleActionsResponse handleGoogleActionsRequest(@RequestBody GoogleActionsRequest body) throws InterruptedException, ExecutionException {
        // Handler ermitteln
        String handlerName = body.getHandler().getName();

        GoogleActionsResponse response;

        if (handlerName.equals("getDogImageHandler")) {
            CompletableFuture<GoogleActionsResponse> responseFuture = dogImageHandler.handleDogImageRequest(body);
            response = responseFuture.get();
        } else if (handlerName.equals("getDogDescriptionAsPlainTextHandler")) {
            response = dogDescriptionHandler.handleDogDescriptionAsPlainTextRequest(body);
        } else if (handlerName.equals("getDogDescriptionAsHtmlHandler")) {
            response = dogDescriptionHandler.handleDogDescriptionAsHtmlRequest(body);
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
