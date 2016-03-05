package net.mkcz.kraken.api.request;

import java.util.ResourceBundle;

/**
 * Created by cintezam on 05/03/16.
 */
public class KrakenPrivateRequestBuilder {
    private static KrakenPrivateRequestBuilder INSTANCE;

    public static KrakenPrivateRequestBuilder releaseTheKraken(final String baseUrl, final String version, final ResourceBundle apiSpecification) {
        if (null == INSTANCE) {
            INSTANCE = new KrakenPrivateRequestBuilder(baseUrl, version, apiSpecification);
        }
        return INSTANCE;
    }

    private final String baseUrl;
    private final String version;
    private final ResourceBundle apiSpecification;

    private KrakenPrivateRequestBuilder(final String baseUrl, final String version, final ResourceBundle apiSpecification) {
        this.baseUrl = baseUrl;
        this.version = version;
        this.apiSpecification = apiSpecification;
    }
}
