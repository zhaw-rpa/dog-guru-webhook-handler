package ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects;

import java.util.Map;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class GoogleActionsSession {
    private String id;
    private Map<String, Object> params;
}
