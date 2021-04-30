package ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GoogleActionsContentCard extends GoogleActionsContent {
    private GoogleActionsCard card;
}
