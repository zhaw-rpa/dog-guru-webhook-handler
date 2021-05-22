package ch.zhaw.rpa.dogguruwebhookhandler.asynchandling;

import java.util.Date;

import org.json.JSONObject;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GoogleActionsSessionState {
    private String googleActionsSessionId;
    private Date googleActionsFirstRequestReceived;
    private String uiPathJobState;
    private JSONObject outputArguments;
    private String uiPathExceptionMessage;
}
