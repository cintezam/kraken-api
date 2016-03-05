package net.mkcz.kraken.api.requests;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import net.mkcz.kraken.api.request.KrakenRequestBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpStatusCode;

import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Supplier;

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
    private KrakenRequestBuilder krakenRequestBuilder;

    @Before
    public void setUp() throws Exception {
        requestMocks = new RequestMocks(mockServerClient, VERSION);
        krakenRequestBuilder = releaseTheKraken(BASE_URL + ":" + PORT, VERSION,
                ResourceBundle.getBundle("api_0"));
    }

    @Test
    public void shouldCreateValidTimeRequest() throws Exception {
        validatePublicRequest(krakenRequestBuilder.publicRequest()::time, "public", "Time");
    }

    @Test
    public void shouldCreateValidAssertsRequest() throws Exception {
        validatePublicRequest(krakenRequestBuilder.publicRequest()::assets, "public", "Assets");
    }

    private <T extends HttpRequest> void validatePublicRequest(final Supplier<Optional<T>> requestSupplier,
                                                               final String type,
                                                               final String expectedPath) throws UnirestException {
        // setup request handling
        requestMocks.handlePublicConnection(expectedPath);

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
