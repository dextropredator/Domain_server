package protocol;

import model.HttpRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

public class HttpRequestParser {

    public static HttpRequest parse(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String requestLine = reader.readLine();

        if (requestLine == null || requestLine.isEmpty()) {
            return null;
        }

        HttpRequest request = new HttpRequest();
        String[] parts = requestLine.split(" ");
        if (parts.length >= 3) {
            request.setMethod(parts[0]);
            request.setUri(parts[1]);
            request.setVersion(parts[2]);
        }

        // Parse headers
        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            int colonIndex = headerLine.indexOf(":");
            if (colonIndex != -1) {
                String key = headerLine.substring(0, colonIndex).trim();
                String value = headerLine.substring(colonIndex + 1).trim();
                request.addHeader(key, value);
            }
        }

        return request;
    }
}