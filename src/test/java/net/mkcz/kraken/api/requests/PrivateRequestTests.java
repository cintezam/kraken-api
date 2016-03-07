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
import org.junit.Test;
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
    private static final String API_KEY = "someKey";
    private static final String API_SIGN = "someSign";
    private static final long NONCE = 0;
    private static final Optional<Long> OTP = Optional.of(1L);
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, PORT);

    private MockServerClient mockServerClient;

    private RequestMocks requestMocks;
    private KrakenPrivateRequestBuilder krakenPrivateRequestBuilder;

    @Before
    public void setUp() throws Exception {
        requestMocks = new RequestMocks(mockServerClient, VERSION);
        final KrakenRequestBuilder krakenRequestBuilder = releaseTheKraken(BASE_URL + ":" + PORT, VERSION);
        krakenPrivateRequestBuilder = krakenRequestBuilder.privateRequest(API_KEY, () -> NONCE, data -> API_SIGN);
    }

    @Test
    public void shouldCreateBalanceRequest() throws Exception {
        validatePrivateRequest(() -> krakenPrivateRequestBuilder.balance(OTP), "Balance", API_SIGN, NONCE, OTP);
    }

    private <T extends HttpRequest> void validatePrivateRequest(final Supplier<Optional<T>> requestSupplier,
                                                                final String expectedPath,
                                                                final String apiSign,
                                                                final long nonce,
                                                                final Optional<Long> otp) throws UnirestException {
        validatePrivateRequest(requestSupplier, expectedPath, apiSign, nonce, otp, Collections.emptyMap());
    }

    private <T extends HttpRequest> void validatePrivateRequest(final Supplier<Optional<T>> requestSupplier,
                                                                final String expectedPath,
                                                                final String apiSign,
                                                                final long nonce,
                                                                final Optional<Long> otp,
                                                                final Map<String, String> otherParams) throws UnirestException {
        // setup request handling
        final Map<String, String> params = new HashMap<>(otherParams);
        params.put("nonce", String.valueOf(nonce));
        otp.ifPresent(val -> params.put("otp", String.valueOf(val)));
        final Map<String, String> headers = new HashMap<>();
        headers.put("API-Key", API_KEY);
        headers.put("API-Sign", apiSign);
        requestMocks.handlePrivateConnection(expectedPath, headers, params);

        //build request
        final Optional<T> optionalRequest = requestSupplier.get();
        assertThat(optionalRequest).isPresent();
        T request = optionalRequest.get();
        validateUrl(request.getUrl(), expectedPath);

        //run request
        final HttpResponse<JsonNode> response = request.asJson();
        assertThat(response.getStatus()).isEqualTo(HttpStatusCode.OK_200.code());
    }

    private void validateUrl(final String actualUrl, final String expectedPath) {
        assertThat(actualUrl).isEqualTo(BASE_URL + ":" + PORT + "/" + VERSION + "/private/" + expectedPath);
    }

}
