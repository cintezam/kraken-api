package net.mkcz.kraken.api.request;

/**
 * Created by cintezam on 05/03/16.
 */
public enum KrakenRequestType {
    PUBLIC("public"),
    PRIVATE("private");

    private String typeString;

    KrakenRequestType(final String typeString) {

        this.typeString = typeString;
    }
}
