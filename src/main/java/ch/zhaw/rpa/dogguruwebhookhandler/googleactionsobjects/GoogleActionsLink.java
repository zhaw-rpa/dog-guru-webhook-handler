package ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class GoogleActionsLink {
    private String name;
    private GoogleActionsOpenUrl open;
}
