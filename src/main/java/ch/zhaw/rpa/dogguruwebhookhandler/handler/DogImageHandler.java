package ch.zhaw.rpa.dogguruwebhookhandler.handler;

import org.springframework.stereotype.Component;

import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsContentImage;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsImage;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsPrompt;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsRequest;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsResponse;

@Component
public class DogImageHandler {
    
    public GoogleActionsResponse handleDogImageRequest(GoogleActionsRequest request){
        String dogImageUri = "https://images.dog.ceo/breeds/terrier-wheaten/n02098105_50.jpg";

        GoogleActionsResponse response = GoogleActionsResponse.builder()
        .prompt(GoogleActionsPrompt.builder()
                .content(GoogleActionsContentImage.builder()
                    .image(GoogleActionsImage.builder()
                        .url(dogImageUri)
                        .alt("Cooles Hundebild")
                        .height(0)
                        .width(0)
                        .build())
                    .build())
                .build())
        .session(request.getSession())
        .scene(request.getScene())
        .build();

        return response;
    }
}
