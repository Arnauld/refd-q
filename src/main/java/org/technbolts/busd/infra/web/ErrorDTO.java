package org.technbolts.busd.infra.web;

import javax.json.bind.annotation.JsonbProperty;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class ErrorDTO {


    public static ErrorDTO errorDTO(Throwable throwable) {
        return new ErrorDTO(throwable.getMessage());
    }

    @JsonbProperty("message")
    public final String message;

    public ErrorDTO(String message) {

        this.message = message;
    }
}
