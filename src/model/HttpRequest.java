package model;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String uri;
    private String version;
    private String clientIp; 
    private Map<String, String> headers = new HashMap<>();

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri = uri; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

   
    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }

    public void addHeader(String key, String value) {
        headers.put(key.toLowerCase(), value);
    }

    public String getHeader(String key) {
        return headers.get(key.toLowerCase());
    }
}