package ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class GoogleActionsPrompt {
    @Builder.Default
    private Boolean override = false;
    private GoogleActionsSimple firstSimple;
    private GoogleActionsContent content;
}
