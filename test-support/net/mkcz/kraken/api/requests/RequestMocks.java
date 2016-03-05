package net.mkcz.kraken.api.requests;

import org.mockserver.client.server.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;
import org.mockserver.model.Parameter;
import org.mockserver.model.ParameterBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * Created by cintezam on 05/03/16.
 */
public class RequestMocks {
    private static final String POST = "POST";
    private final MockServerClient mockServerClient;
    private final String version;

    public RequestMocks(final MockServerClient mockServerClient, final String version) {

        this.mockServerClient = mockServerClient;
        this.version = version;
    }

    public void handlePublicConnection(final String path, final Map<String, String> params) {
        handleConnection("public", path, params);
    }

    private void handleConnection(final String type, final String path, final Map<String, String> params) {
        final HttpRequest httpRequest = request()
                .withMethod(POST)
                .withPath("/" + version + "/" + type + "/" + path);
        if (!params.isEmpty()) {
            final List<Parameter> parameters = params.entrySet().stream()
                    .map(entry -> new Parameter(entry.getKey(), entry.getValue()))
                    .collect(toList());
            httpRequest.withBody(new ParameterBody(parameters));
        }
        final HttpResponse httpResponse = response().
                withStatusCode(HttpStatusCode.OK_200.code())
                .withDelay(TimeUnit.SECONDS, 1);
        mockServerClient
                .when(httpRequest)
                .respond(httpResponse);
    }
}
