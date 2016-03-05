package net.mkcz.kraken.api.request;

import com.mashape.unirest.request.HttpRequestWithBody;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.mashape.unirest.http.Unirest.post;

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

    public Optional<HttpRequestWithBody> time() {
        return getTypedSpec("time").map(this::toRequest);
    }

    public Optional<HttpRequestWithBody> assets(final AssetInfo info, final List<String> pairs) {
        final Map<String, Object> params = new HashMap<>();
        params.put("info", info.name().toLowerCase());
        params.put("pair", pairs.stream().collect(Collectors.joining(",")));
        return getTypedSpec("assets").map(typedSpec -> toRequest(typedSpec, params));
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

    private HttpRequestWithBody toRequest(final String path) {
        return toRequest(path, Collections.emptyMap());
    }

    private HttpRequestWithBody toRequest(final String path, final Map<String, Object> params) {
        final HttpRequestWithBody request = post(baseUrl + "/{version}/{type}/{path}")
                .routeParam("version", version)
                .routeParam("type", "public")
                .routeParam("path", path);
        if (!params.isEmpty())
        {
            request.fields(params);
        }
        return request;
    }
}
