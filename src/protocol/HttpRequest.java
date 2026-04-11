package protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class HttpRequest {
    private final String method;
    private final String path;
    private final String version;
    private final Map<String, String> headers = new HashMap<>();

    public HttpRequest(String method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static HttpRequest parse(InputStream in) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

          
            String startLine = reader.readLine();
            if (startLine == null || startLine.trim().isEmpty()) {
                return null;
            }
            String[] parts = startLine.split("\\s+");
            if (parts.length < 3) {
                return null;
            }
            String method = parts[0];
            String path = parts[1];
            String version = parts[2];

            HttpRequest req = new HttpRequest(method, path, version);

           
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) break;
                int idx = line.indexOf(':');
                if (idx > 0) {
                    String name = line.substring(0, idx).trim();
                    String value = line.substring(idx + 1).trim();
                    req.headers.put(name, value);
                }
            }
            return req;
        } catch (IOException e) {
            return null;
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public String getHeader(String name) {
        return headers.getOrDefault(name, null);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}