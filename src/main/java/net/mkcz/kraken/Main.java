package net.mkcz.kraken;

import net.mkcz.kraken.api.request.KrakenRequestBuilder;

import java.util.ResourceBundle;

/**
 * Created by cintezam on 05/03/16.
 */
public class Main {
    public static void main(String[] args) {
        final String baseUrl = args.length > 0 ? args[0] : "https://api.kraken.com";
        final String version = args.length > 1 ? args[1] : "0";

        ResourceBundle apiSpecification = ResourceBundle.getBundle("api_" + version);

        KrakenRequestBuilder krakenRequestBuilder = KrakenRequestBuilder.releaseTheKraken(baseUrl, version, apiSpecification);
        krakenRequestBuilder.publicRequest();
    }
}
