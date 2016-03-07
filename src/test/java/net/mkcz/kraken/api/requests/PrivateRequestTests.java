package net.mkcz.kraken.api.requests;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import net.mkcz.kraken.api.request.KrakenPrivateRequestBuilder;
import net.mkcz.kraken.api.request.KrakenRequestBuilder;
import net.mkcz.testsupport.kraken.api.requests.RequestMocks;

import org.junit.Before;
import org.junit.Rule;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpStatusCode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static net.mkcz.kraken.api.request.KrakenRequestBuilder.releaseTheKraken;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by cintezam on 05/03/16.
 */
public class PrivateRequestTests {
    private static final int PORT = 8080;
    private static final String BASE_URL = "http://localhost";
    private static final String VERSION = "0";
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, PORT);

    private MockServerClient mockServerClient;

    private RequestMocks requestMocks;
    private KrakenPrivateRequestBuilder krakenPrivateRequestBuilder;

    @Before
    public void setUp() throws Exception {
        requestMocks = new RequestMocks(mockServerClient, VERSION);
        final KrakenRequestBuilder krakenRequestBuilder = releaseTheKraken(BASE_URL + ":" + PORT, VERSION);
        krakenPrivateRequestBuilder = krakenRequestBuilder.privateRequest();
    }


    private <T extends HttpRequest> void validatePrivateRequest(final Supplier<Optional<T>> requestSupplier,
                                                                final String type,
                                                                final String expectedPath,
                                                                final String apiKey,
                                                                final String apiSign,
                                                                final long nonce,
                                                                final long otp) throws UnirestException {
        validatePrivateRequest(requestSupplier, type, expectedPath, apiKey, apiSign, nonce, otp, Collections.emptyMap());
    }


    private <T extends HttpRequest> void validatePrivateRequest(final Supplier<Optional<T>> requestSupplier,
                                                                final String type,
                                                                final String expectedPath,
                                                                final String apiKey,
                                                                final String apiSign,
                                                                final long nonce,
                                                                final long otp,
                                                                final Map<String, String> otherParams) throws UnirestException {
        // setup request handling
        final Map<String, String> params = new HashMap<>(otherParams);
        params.put("nonce", String.valueOf(nonce));
        params.put("opt", String.valueOf(otp));
        final Map<String, String> headers = new HashMap<>();
        headers.put("API-Key", apiKey);
        headers.put("API-Sign", apiSign);
        requestMocks.handlePrivateConnection(expectedPath, headers, params);

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
