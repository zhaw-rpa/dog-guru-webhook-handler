package ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GoogleActionsRequest {
    private Handler handler;
    private Intent intent;
    private GoogleActionsSession session;
    private GoogleActionsScene scene;

    @Getter
    @Setter
    @ToString
    public class Handler {
        private String name;
    }

    @Getter
    @Setter
    @ToString
    public class Intent {
        private String name;
        private Map<String, GoogleActionsIntentParameterValue> params;
        private String query;
    }
}
