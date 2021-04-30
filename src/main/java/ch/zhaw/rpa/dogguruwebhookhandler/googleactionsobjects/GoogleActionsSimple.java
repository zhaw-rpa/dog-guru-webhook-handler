package ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class GoogleActionsSimple {
    private String speech;
    private String text;
}
