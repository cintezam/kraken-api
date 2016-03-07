package net.mkcz.kraken.api.request;

import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.mashape.unirest.http.Unirest.post;

/**
 * Created by cintezam on 05/03/16.
 */
public class KrakenPrivateRequestBuilder {
    // todo: sort out API-Sign generation
    private static KrakenPrivateRequestBuilder INSTANCE;

    public static KrakenPrivateRequestBuilder releaseTheKraken(final String baseUrl,
                                                               final String version,
                                                               final ResourceBundle apiSpecification,
                                                               final String apiKey,
                                                               final Supplier<Long> nonceSupplier,
                                                               final Function<String, String> apiSignSupplier) {
        if (null == INSTANCE) {
            INSTANCE = new KrakenPrivateRequestBuilder(baseUrl, version, apiSpecification, apiKey, nonceSupplier, apiSignSupplier);
        }
        return INSTANCE;
    }

    private final String baseUrl;
    private final String version;
    private final ResourceBundle apiSpecification;
    private final String apiKey;
    private final Supplier<Long> nonceSupplier;
    private final Function<String, String> apiSignSupplier;

    private KrakenPrivateRequestBuilder(final String baseUrl,
                                        final String version,
                                        final ResourceBundle apiSpecification,
                                        final String apiKey,
                                        final Supplier<Long> nonceSupplier,
                                        final Function<String, String> apiSignSupplier) {
        this.baseUrl = baseUrl;
        this.version = version;
        this.apiSpecification = apiSpecification;
        this.apiKey = apiKey;
        this.nonceSupplier = nonceSupplier;
        this.apiSignSupplier = apiSignSupplier;
    }

    public Optional<HttpRequest> balance(final Optional<Long> otp) {
        return getTypedSpec("balance").map(typeSpec -> toRequest(typeSpec, otp));
    }

    public Optional<HttpRequest> tradeBalance(final Optional<Long> otp, final String asset) {
        final Map<String, Object> params = new HashMap<>();
        params.put("asset", asset);
        params.put("aclass", "currency");
        return getTypedSpec("tradeBalance").map(typeSpec -> toRequest(typeSpec, otp, params));
    }

    public Optional<HttpRequest> openOrders(final Optional<Long> otp) {
        return openOrders(otp, false, Optional.empty());
    }

    public Optional<HttpRequest> openOrders(final Optional<Long> otp, final boolean includeTrades) {
        return openOrders(otp, includeTrades, Optional.empty());
    }

    public Optional<HttpRequest> openOrders(final Optional<Long> otp, final String userRef) {
        return openOrders(otp, false, Optional.of(userRef));
    }

    public Optional<HttpRequest> openOrders(final Optional<Long> otp, final boolean includeTrades, final String userRef) {
        return openOrders(otp, includeTrades, Optional.of(userRef));
    }

    private Optional<HttpRequest> openOrders(final Optional<Long> otp, final boolean includeTrades, final Optional<String> userRef) {
        final Map<String, Object> params = new HashMap<>();
        params.put("trades", includeTrades);
        userRef.ifPresent(val -> params.put("userref", val));
        return getTypedSpec("openOrders").map(typedSpec -> toRequest(typedSpec, otp, params));
    }

    private Optional<String> getTypedSpec(final String key) {
        return getSpec("private." + key);
    }

    private Optional<String> getSpec(final String key) {
        if (apiSpecification.containsKey(key)) {
            return Optional.of(apiSpecification.getString(key));
        }
        return Optional.empty();
    }

    private HttpRequest toRequest(final String path, final Optional<Long> otp) {
        return toRequest(path, otp, Collections.emptyMap());
    }

    private HttpRequest toRequest(final String path, final Optional<Long> otp, final Map<String, Object> otherParams) {
        final HttpRequestWithBody request = post(baseUrl + "/{version}/{type}/{path}")
                .routeParam("version", version)
                .routeParam("type", "private")
                .routeParam("path", path)
                .header("API-Key", apiKey)
                // todo: the below is not correct, just a placeholder
                .header("API-Sign", apiSignSupplier.apply(apiKey));
        final Map<String, Object> params = new HashMap<>(otherParams);
        params.put("nonce", nonceSupplier.get());
        otp.ifPresent(val -> params.put("otp", val));
        if (!params.isEmpty())
        {
            request.fields(params);
        }
        return request;
    }
}
