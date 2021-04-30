package ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GoogleActionsScene {
    private String name;
    private String slotFillingStatus;
    private Map<String, Object> slots;
    private NextScene next;

    @Getter
    @Setter
    @ToString
    public class NextScene {
        private String name;
    }
}
