package net.mkcz.kraken.api.request;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;

import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by cintezam on 05/03/16.
 */
public class KrakenPublicRequestBuilder {
    private static KrakenPublicRequestBuilder INSTANCE;

    public static KrakenPublicRequestBuilder releaseTheKraken(final String baseUrl, final String version, final ResourceBundle apiSpecification) {
        if (null == INSTANCE) {
            INSTANCE = new KrakenPublicRequestBuilder(baseUrl, version, apiSpecification);
        }
        return INSTANCE;
    }

    private final String baseUrl;
    private final String version;

    private final ResourceBundle apiSpecification;


    private KrakenPublicRequestBuilder(final String baseUrl, final String version, final ResourceBundle apiSpecification) {
        this.baseUrl = baseUrl;
        this.version = version;
        this.apiSpecification = apiSpecification;
    }

    public Optional<GetRequest> time() {
        return keyedUrl("time").map(Unirest::get);
    }

    private Optional<String> keyedUrl(final String resourceKey) {
        return url("public." + resourceKey);
    }

    private Optional<String> url(final String resourceKey) {
        if (apiSpecification.containsKey(resourceKey)) {
            final String urlBuilder = baseUrl + "/" +
                    version +
                    "/public/" +
                    apiSpecification.getString(resourceKey);
            return Optional.of(urlBuilder);
        }
        return Optional.empty();

    }
}
