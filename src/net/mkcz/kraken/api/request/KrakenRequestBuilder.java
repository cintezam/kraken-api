package net.mkcz.kraken.api.request;

import java.util.ResourceBundle;

/**
 * Created by cintezam on 05/03/16.
 */
public class KrakenRequestBuilder {
    private static KrakenRequestBuilder INSTANCE;

    public static KrakenRequestBuilder releaseTheKraken(final String baseUrl, final String version, final ResourceBundle apiSpecification) {
        if (null == INSTANCE) {
            INSTANCE = new KrakenRequestBuilder(baseUrl, version, apiSpecification);
        }
        return INSTANCE;
    }

    private final String baseUrl;
    private final String version;
    private final ResourceBundle apiSpecification;

    private KrakenRequestBuilder(final String baseUrl, final String version, final ResourceBundle apiSpecification) {

        this.baseUrl = baseUrl;
        this.version = version;
        this.apiSpecification = apiSpecification;
    }

    public KrakenPublicRequestBuilder publicRequest() {
        return KrakenPublicRequestBuilder.releaseTheKraken(baseUrl, version, apiSpecification);
    }

    public KrakenPrivateRequestBuilder privateRequest() {
        return KrakenPrivateRequestBuilder.releaseTheKraken(baseUrl, version, apiSpecification);
    }
}
