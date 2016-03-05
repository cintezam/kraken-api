package net.mkcz.kraken.api.request;

import com.mashape.unirest.request.GetRequest;

import java.util.Optional;
import java.util.ResourceBundle;

import static com.mashape.unirest.http.Unirest.get;

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
        return getTypedSpec("time").map(this::toRequest);
    }

    public Optional<GetRequest> assets() {
        return getTypedSpec("assets").map(this::toRequest);
    }

    public Optional<String> getTypedSpec(final String key) {
        return getSpec("public." + key);
    }

    private Optional<String> getSpec(final String key) {
        if (apiSpecification.containsKey(key)) {
            return Optional.of(apiSpecification.getString(key));
        }
        return Optional.empty();
    }

    private GetRequest toRequest(final String path) {
        return get(baseUrl + "/{version}/{type}/{path}")
                .routeParam("version", version)
                .routeParam("type", "public")
                .routeParam("path", path);
    }
}
