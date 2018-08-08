package com.voxelmodpack.hdskins.skins;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
/**
 * Ew. Why so many builders? >.<
 */
public class NetClient {

    private static CloseableHttpClient client = null;

    public static CloseableHttpClient nativeClient() {
        if (client == null) {
            client = HttpClients.createSystem();
        }

        return client;
    }


    private RequestBuilder rqBuilder;

    private Map<String, ?> headers;

    public NetClient(String method, String uri) {
        rqBuilder = RequestBuilder.create(method).setUri(uri);
    }

    /**
     * Adds a file to the request. Typically used with PUT/POST for uploading.
     * @param key           Key identifier to index the file in the request.
     * @param contentType   Type of file being sent. Usually the mime-type.
     * @param file          The file or a link to the file.
     *
     * @return itself for chaining
     */
    public NetClient putFile(String key, String contentType, URI file) {
        File f = new File(file);
        HttpEntity entity = MultipartEntityBuilder.create().addBinaryBody(key, f, ContentType.create(contentType), f.getName()).build();

        rqBuilder.setEntity(entity);

        return this;
    }

    /**
     * Sets the headers to be included with this request.
     * @param headers   Headers to send
     *
     * @return itself for chaining
     */
    public NetClient putHeaders(Map<String, ?> headers) {
        this.headers = headers;

        return this;
    }

    /**
     * Commits and sends the request.
     */
    public MoreHttpResponses send() throws IOException {
        HttpUriRequest request = rqBuilder.build();

        if (headers != null) {
            for (Map.Entry<String, ?> parameter : headers.entrySet()) {
                request.addHeader(parameter.getKey(), parameter.getValue().toString());
            }
        }

        return MoreHttpResponses.execute(nativeClient(), request);
    }
}
