package ch.zhaw.rpa.dogguruwebhookhandler.asynchandling;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GoogleActionsSessionStateService {

    private List<GoogleActionsSessionState> sessionStates = new ArrayList<>();
    

    public void addSessionState(GoogleActionsSessionState sessionState) {
        sessionStates.add(sessionState);
    }

    public GoogleActionsSessionState getSessionStateBySessionId(String sessionId) {
        return sessionStates.stream()
            .filter(sessionState -> sessionId.equals(sessionState.getGoogleActionsSessionId()))
            .findFirst()
            .orElse(null);
    }

    public void removeSessionState(GoogleActionsSessionState sessionState) {
        sessionStates.remove(sessionState);
    }
    

    // Alle 10 Minuten prüfen, ob es States gibt, welche älter als 1 Stunde sind und diese löschen
    @Scheduled(fixedRate = 600000)
    public void removeOldStates() {
        Instant now = Instant.now();
        String sessionId;

        for (GoogleActionsSessionState googleActionsSessionState : sessionStates) {
            if(googleActionsSessionState.getGoogleActionsFirstRequestReceived().toInstant().isBefore(now.minus(1, ChronoUnit.HOURS))){
                sessionId = googleActionsSessionState.getGoogleActionsSessionId();
                sessionStates.remove(googleActionsSessionState);
                System.out.println("Log: Auto-removed " + sessionId);
            }
        }
    }
}
