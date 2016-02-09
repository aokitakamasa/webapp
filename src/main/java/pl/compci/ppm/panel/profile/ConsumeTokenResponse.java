package pl.compci.ppm.panel.profile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by TKrolikowski on 2015-12-01.
 */
@ApiModel("ConsumeTokenResponse")
public class ConsumeTokenResponse {

    @ApiModelProperty("User's login that can be used to provide faster login after password reset")
    private String login;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
