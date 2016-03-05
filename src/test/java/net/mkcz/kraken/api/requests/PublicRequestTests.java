package net.mkcz.kraken.api.requests;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import net.mkcz.kraken.api.request.AssetInfo;
import net.mkcz.kraken.api.request.KrakenPublicRequestBuilder;
import net.mkcz.kraken.api.request.KrakenRequestBuilder;
import net.mkcz.kraken.api.request.OHLCInterval;
import net.mkcz.testsupport.kraken.api.requests.RequestMocks;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpStatusCode;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.mkcz.kraken.api.request.KrakenRequestBuilder.releaseTheKraken;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by cintezam on 05/03/16.
 */
public class PublicRequestTests {
    private static final int PORT = 8080;
    private static final String BASE_URL = "http://localhost";
    private static final String VERSION = "0";
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, PORT);

    private MockServerClient mockServerClient;

    private RequestMocks requestMocks;
    private KrakenPublicRequestBuilder krakenPublicRequestBuilder;

    @Before
    public void setUp() throws Exception {
        requestMocks = new RequestMocks(mockServerClient, VERSION);
        final KrakenRequestBuilder krakenRequestBuilder = releaseTheKraken(BASE_URL + ":" + PORT, VERSION, ResourceBundle.getBundle("api_0"));
        krakenPublicRequestBuilder = krakenRequestBuilder.publicRequest();
    }

    @Test
    public void shouldCreateTimeRequest() throws Exception {
        validatePublicRequest(krakenPublicRequestBuilder::time, "public", "Time");
    }

    @Test
    public void shouldCreateAssertsRequest() throws Exception {
        final AssetInfo info = AssetInfo.LEVERAGE;
        final List<String> pairs = Arrays.asList("XBTCEUR", "XBTCUSD");
        final Map<String, String> params = new HashMap<>();
        params.put("info", info.name().toLowerCase());
        params.put("pair", pairs.stream().collect(Collectors.joining(",")));
        validatePublicRequest(() -> krakenPublicRequestBuilder.assets(info, pairs), "public", "Assets", params);
    }

    @Test
    public void shouldCreateAssetPairsRequest() throws Exception {
        final AssetInfo info = AssetInfo.FEES;
        final List<String> pairs = Arrays.asList("XBTCEUR", "LITEUSD");
        final Map<String, String> params = new HashMap<>();
        params.put("info", info.name().toLowerCase());
        params.put("pair", pairs.stream().collect(Collectors.joining(",")));
        validatePublicRequest(() -> krakenPublicRequestBuilder.assetPairs(info, pairs), "public", "AssetPairs", params);
    }

    @Test
    public void shouldCreateTickerRequest() throws Exception {
        final List<String> pairs = Arrays.asList("LITEEUR", "XBTCUSD");
        final Map<String, String> params = new HashMap<>();
        params.put("pair", pairs.stream().collect(Collectors.joining(",")));
        validatePublicRequest(() -> krakenPublicRequestBuilder.ticker(pairs), "public", "Ticker", params);
    }

    @Test
    public void shouldCreateOHLRRequest() throws Exception {
        final String pair = "LITEXBTC";
        final OHLCInterval interval = OHLCInterval.FOUR_HOURS;
        final long since = System.currentTimeMillis();
        final Map<String, String> params = new HashMap<>();
        params.put("pair", pair);
        params.put("interval", String.valueOf(interval.getDuration()));
        params.put("since", String.valueOf(since));
        validatePublicRequest(() -> krakenPublicRequestBuilder.ohlc(pair, interval, since), "public", "OHLC", params);
    }

    @Test
    public void shouldCreateOrderBookRequest() throws Exception {
        final String pair = "EURUSD";
        final int count = 15;
        final Map<String, String> params = new HashMap<>();
        params.put("pair", pair);
        params.put("count", String.valueOf(count));
        validatePublicRequest(() -> krakenPublicRequestBuilder.orderBook(pair, count), "public", "Depth", params);
    }

    private <T extends HttpRequest> void validatePublicRequest(final Supplier<Optional<T>> requestSupplier,
                                                               final String type,
                                                               final String expectedPath) throws UnirestException {
        validatePublicRequest(requestSupplier, type, expectedPath, Collections.emptyMap());
    }


    private <T extends HttpRequest> void validatePublicRequest(final Supplier<Optional<T>> requestSupplier,
                                                               final String type,
                                                               final String expectedPath,
                                                               final Map<String, String> params) throws UnirestException {
        // setup request handling
        requestMocks.handlePublicConnection(expectedPath, params);

        //build request
        final Optional<T> optionalRequest = requestSupplier.get();
        assertThat(optionalRequest).isPresent();
        T request = optionalRequest.get();
        validateUrl(request.getUrl(), type, expectedPath);

        //run request
        final HttpResponse<JsonNode> response = request.asJson();
        assertThat(response.getStatus()).isEqualTo(HttpStatusCode.OK_200.code());
    }

    private void validateUrl(final String actualUrl, final String type, final String expectedPath) {
        assertThat(actualUrl).isEqualTo(BASE_URL + ":" + PORT + "/" + VERSION + "/" + type + "/" + expectedPath);
    }

}
