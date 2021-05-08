package ch.zhaw.rpa.dogguruwebhookhandler.handler;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsCard;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsContentCard;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsLink;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsOpenUrl;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsPrompt;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsRequest;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsResponse;
import ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects.GoogleActionsSimple;
import ch.zhaw.rpa.dogguruwebhookhandler.wikipedia.WikipediaRestClient;

@Component
public class DogDescriptionHandler {

    private static final Integer EXTRACT_LENGTH = 500;

    @Autowired
    private WikipediaRestClient wikipediaRestClient;
    
    public GoogleActionsResponse handleDogDescriptionAsHtmlRequest(GoogleActionsRequest request){
        String dogDescription = this.getDogDescription(request);

        GoogleActionsResponse response = GoogleActionsResponse.builder()
            .prompt(GoogleActionsPrompt.builder()
                .content(GoogleActionsContentCard.builder()
                    .card(GoogleActionsCard.builder()
                        .text(dogDescription)
                        .button(GoogleActionsLink.builder()
                            .name("Gehe zu Wikipedia-Eintrag")
                            .open(GoogleActionsOpenUrl.builder()
                                .url(wikipediaRestClient.getWikipediaEntryUrl(this.getSearchTerm(request)))
                                .build())
                            .build())
                        .build())
                    .build())
                .build())
        .session(request.getSession())
        .scene(request.getScene())
        .build();

        return response;
    }

    public GoogleActionsResponse handleDogDescriptionAsPlainTextRequest(GoogleActionsRequest request){
        String dogDescription = this.getDogDescription(request);
        dogDescription = Jsoup.parseBodyFragment(dogDescription).text();

        GoogleActionsResponse response = GoogleActionsResponse.builder()
            .prompt(GoogleActionsPrompt.builder()
                .firstSimple(GoogleActionsSimple.builder()
                    .text(dogDescription)
                    .speech(dogDescription)
                    .build())
                .build())
        .session(request.getSession())
        .scene(request.getScene())
        .build();

        return response;
    }

    private String getDogDescription(GoogleActionsRequest request) {
        String searchTerm = this.getSearchTerm(request);
        String dogDescription = wikipediaRestClient.getWikipediaExtract(EXTRACT_LENGTH, searchTerm);

        if(dogDescription.isEmpty()){
            dogDescription = "Es wurde kein Wikipedia-Eintrag zu dieser Hunderasse gefunden.";
        }

        return dogDescription;
    }

    private String getSearchTerm(GoogleActionsRequest request) {
        String searchTerm = request.getSession().getParams().get("rasse").toString();
        return searchTerm;
    }
}
