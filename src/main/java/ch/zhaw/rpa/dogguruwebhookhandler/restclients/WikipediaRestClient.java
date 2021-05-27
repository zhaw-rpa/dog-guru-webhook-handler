package ch.zhaw.rpa.dogguruwebhookhandler.restclients;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WikipediaRestClient {

    private static final String WIKIPEDIA_EXTRACT_URL = "https://de.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exchars=";
    private static final String WIKIPEDIA_ENTRY_URL = "https://de.wikipedia.org/?curid=";
    
    private RestTemplate restTemplate;

    private String wikipediaEntryUrl;
    private String resolvedWikipediaExtractUrl;

    @PostConstruct
    public void postConstruct() {
        restTemplate = new RestTemplate();
    }

    public String getWikipediaExtract(Integer extractLength, String searchTerm) {
        try {
            // Falls man hier noch etwas mehr Logik möchte (z.B. jeden ersten Buchstaben gross, dann z.B. https://stackoverflow.com/questions/1892765/how-to-capitalize-the-first-character-of-each-word-in-a-string)
            // Denn im Moment findet er z.B. deutscher Jagdterrier, nicht aber deutscher jagdterrier...
            resolvedWikipediaExtractUrl = WIKIPEDIA_EXTRACT_URL + extractLength.toString() + "&titles=" + URLEncoder.encode(searchTerm, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return "";
        }

        try {
            String extract;
            ResponseEntity<String> response = restTemplate.exchange(resolvedWikipediaExtractUrl, HttpMethod.GET, null, String.class); 

            // Weil das Element im "pages" jedes Mal anders heisst, habe ich verzichtet, ein Java-Objekt zu machen oder JSON selbst zu nutzen.
            // Es ist deutlich einfacher, wenn auch hacky hier einfach REGEX zu nutzen
            Pattern pattern = Pattern.compile(".*,\"extract\":\"(.*)\"\\}.*");
            Matcher matcher = pattern.matcher(response.getBody());

            if(matcher.matches()){
                extract = matcher.group(1);
            } else {
                extract = "";
            }

            // Da man später auch den Link zur Wikipedia-Seite benötigt, deren Id auslesen
            pattern = Pattern.compile(".*\"pageid\":(.*?),.*");
            matcher = pattern.matcher(response.getBody());

            if(matcher.matches()){
                this.wikipediaEntryUrl = WIKIPEDIA_ENTRY_URL + matcher.group(1);
            } else {
                this.wikipediaEntryUrl = "";
            }

            return extract;
        } catch (Exception e) {
            return "";
        }
    }

    public String getWikipediaEntryUrl(String searchTerm){
        return this.wikipediaEntryUrl;
    }
}
