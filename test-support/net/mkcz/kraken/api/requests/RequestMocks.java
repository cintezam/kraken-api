package net.mkcz.kraken.api.requests;

import org.mockserver.client.server.MockServerClient;
import org.mockserver.model.HttpStatusCode;

import java.util.concurrent.TimeUnit;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * Created by cintezam on 05/03/16.
 */
public class RequestMocks {
    private final MockServerClient mockServerClient;
    private final String version;

    public RequestMocks(final MockServerClient mockServerClient, final String version) {

        this.mockServerClient = mockServerClient;
        this.version = version;
    }

    public void handleTime() {
        mockServerClient.when(request().withPath("/" + version + "/public/Time"))
                .respond(response().withStatusCode(HttpStatusCode.OK_200.code()).withDelay(TimeUnit.SECONDS, 1));
    }
}
