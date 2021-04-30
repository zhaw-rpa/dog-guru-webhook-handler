package ch.zhaw.rpa.dogguruwebhookhandler.googleactionsobjects;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class GoogleActionsImage {
    private String url;
    private String alt;
    private Integer height;
    private Integer width;
}
