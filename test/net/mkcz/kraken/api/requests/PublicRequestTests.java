package net.mkcz.kraken.api.requests;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.request.GetRequest;

import net.mkcz.kraken.api.request.KrakenRequestBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpStatusCode;

import java.util.Optional;
import java.util.ResourceBundle;

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
        //setup for time handling
        requestMocks.handleTime();

        //build request
        Optional<GetRequest> optionalTimeGet = krakenRequestBuilder.publicRequest().time();
        assertThat(optionalTimeGet).isPresent();
        GetRequest getRequest = optionalTimeGet.get();
        assertThat(getRequest.getUrl()).isEqualTo(BASE_URL + ":" + PORT + "/" + VERSION + "/public/Time");

        //run request
        HttpResponse<JsonNode> response = getRequest.asJson();
        assertThat(response.getStatus()).isEqualTo(HttpStatusCode.OK_200.code());
    }
}
