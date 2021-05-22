package ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
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
    @Singular
    private List<GoogleActionsSuggestion> suggestions;
}
