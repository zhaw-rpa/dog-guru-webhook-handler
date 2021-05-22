package ch.zhaw.rpa.dogguruwebhookhandler.asynchandling;

import java.util.ArrayList;
import java.util.List;

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
    
}
