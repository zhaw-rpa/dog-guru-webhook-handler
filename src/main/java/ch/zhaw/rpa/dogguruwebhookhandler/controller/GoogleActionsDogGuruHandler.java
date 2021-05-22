package ch.zhaw.rpa.dogguruwebhookhandler.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsPrompt;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsRequest;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsResponse;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsSimple;
import ch.zhaw.rpa.dogguruwebhookhandler.handler.DogDescriptionWikipediaHandler;
import ch.zhaw.rpa.dogguruwebhookhandler.handler.UiPathHandler;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping(value = "api")
public class GoogleActionsDogGuruHandler {

    @Autowired
    private UiPathHandler uiPathHandler;

    @Autowired
    private DogDescriptionWikipediaHandler dogDescriptionWikipediaHandler;

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

        if (handlerName.equals("getDogImageHandler") || handlerName.equals("getDogDescriptionFromHundeoHandler")) {
            response = uiPathHandler.handleUiPathRequest(body, handlerName);
        } else if (handlerName.equals("getDogDescriptionAsPlainTextHandler")) {
            response = dogDescriptionWikipediaHandler.handleDogDescriptionAsPlainTextRequest(body);
        } else if (handlerName.equals("getDogDescriptionAsHtmlHandler")) {
            response = dogDescriptionWikipediaHandler.handleDogDescriptionAsHtmlRequest(body);
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
