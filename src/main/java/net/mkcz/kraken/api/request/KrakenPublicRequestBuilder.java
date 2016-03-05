package net.mkcz.kraken.api.request;

import com.mashape.unirest.request.HttpRequest;
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

    public Optional<HttpRequest> time() {
        return getTypedSpec("time").map(this::toRequest);
    }

    public Optional<HttpRequest> assets(final AssetInfo info) {
        return assets(info, Collections.singletonList("all"));
    }

    public Optional<HttpRequest> assets(final List<String> pairs) {
        return assets(AssetInfo.ALL, pairs);
    }

    public Optional<HttpRequest> assets(final AssetInfo info, final List<String> pairs) {
        final Map<String, Object> params = new HashMap<>();
        params.put("info", info.name().toLowerCase());
        params.put("pair", pairs.stream().collect(Collectors.joining(",")));
        return getTypedSpec("assets").map(typedSpec -> toRequest(typedSpec, params));
    }

    public Optional<HttpRequest> assetPairs(final AssetInfo info) {
        return assetPairs(info, Collections.singletonList("all"));
    }

    public Optional<HttpRequest> assetPairs(final List<String> pairs) {
        return assetPairs(AssetInfo.ALL, pairs);
    }

    public Optional<HttpRequest> assetPairs(final AssetInfo info, final List<String> pairs) {
        final Map<String, Object> params = new HashMap<>();
        params.put("info", info.name().toLowerCase());
        params.put("pair", pairs.stream().collect(Collectors.joining(",")));
        return getTypedSpec("assetPairs").map(typedSpec -> toRequest(typedSpec, params));
    }

    public Optional<HttpRequest> ticker(final List<String> pairs) {
        final Map<String, Object> params = new HashMap<>();
        params.put("pair", pairs.stream().collect(Collectors.joining(",")));
        return getTypedSpec("ticker").map(typedSpec -> toRequest(typedSpec, params));
    }

    public Optional<HttpRequest> ohlc(final String pair) {
        return ohlc(pair, OHLCInterval.MINUTE, Optional.empty());
    }

    public Optional<HttpRequest> ohlc(final String pair, final OHLCInterval interval) {
        return ohlc(pair, interval, Optional.empty());
    }

    public Optional<HttpRequest> ohlc(final String pair, final long since) {
        return ohlc(pair, OHLCInterval.MINUTE, Optional.of(since));
    }

    public Optional<HttpRequest> ohlc(final String pair, final OHLCInterval interval, final long since) {
        return ohlc(pair, interval, Optional.of(since));
    }

    private Optional<HttpRequest> ohlc(final String pair, final OHLCInterval interval, final Optional<Long> since) {
        final Map<String, Object> params = new HashMap<>();
        params.put("pair", pair);
        params.put("interval", interval.getDuration());
        since.ifPresent(val -> params.put("since", val));
        return getTypedSpec("ohlc").map(typedSpec -> toRequest(typedSpec, params));
    }

    public Optional<HttpRequest> orderBook(final String pair) {
        return orderBook(pair, Optional.empty());
    }

    public Optional<HttpRequest> orderBook(final String pair, final int count) {
        return orderBook(pair, Optional.of(count));
    }

    private Optional<HttpRequest> orderBook(final String pair, final Optional<Integer> count) {
        final Map<String, Object> params = new HashMap<>();
        params.put("pair", pair);
        count.ifPresent(val -> params.put("count", val));
        return getTypedSpec("orderbook").map(typedSpec -> toRequest(typedSpec, params));
    }

    private Optional<String> getTypedSpec(final String key) {
        return getSpec("public." + key);
    }

    private Optional<String> getSpec(final String key) {
        if (apiSpecification.containsKey(key)) {
            return Optional.of(apiSpecification.getString(key));
        }
        return Optional.empty();
    }

    private HttpRequest toRequest(final String path) {
        return toRequest(path, Collections.emptyMap());
    }

    private HttpRequest toRequest(final String path, final Map<String, Object> params) {
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
