package org.mule.ibeans.bitly;

import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.State;
import org.mule.ibeans.api.client.authentication.HttpBasicAuthentication;
import org.mule.ibeans.api.client.filters.JsonErrorFilter;
import org.mule.ibeans.api.client.filters.XmlErrorFilter;
import org.mule.ibeans.api.client.params.ReturnType;
import org.mule.ibeans.api.client.params.UriParam;

/**
 * An iBean client for working with the Bit.ly api for shortening URLs. The API provides a few methods for shortening an expanding
 * URLs as well as quering usage of the URL.
 * <p/>
 * Thi client support both JSON and XML formats and authentication using HTTP Basic or using a bit.ly API Key.
 */
@JsonErrorFilter(expr = "errorCode!=0", errorCode = "errorCode")
@XmlErrorFilter(expr = "/bitly/errorCode != '0'", errorCode = "/bitly/errorCode")
public interface BitlyIBean extends HttpBasicAuthentication
{
    @UriParam("version")
    public static final String VERSION = "2.0.1";

    @UriParam("bitly_url")
    public static final String BITLY_API_URL = "http://api.bit.ly/";

    @UriParam("format")
    public static final String DEFAULT_FORMAT = "json";

    @ReturnType
    public static final Class DEFAULT_RETURN_TYPE = String.class;

    @State
    public void init(@UriParam("login") String login, @UriParam("api_key") String apiKey);

    @State
    public void setFormat(@UriParam("format") String format, @ReturnType Class returnType);

    @Call(uri = "http://api.bit.ly/shorten?version={version}&longUrl={url}&login={login}&apiKey={api_key}&format={format}")
    public <T> T shorten(@UriParam("url") String url) throws CallException;

    @Call(uri = "http://api.bit.ly/expand?version={version}&shortUrl={url}&login={login}&apiKey={api_key}&format={format}")
    public <T> T expand(@UriParam("url") String url) throws CallException;

    @Call(uri = "http://api.bit.ly/info?version={version}&shortUrl={url}&login={login}&apiKey={api_key}&format={format}")
    public <T> T info(@UriParam("url") String url) throws CallException;

    @Call(uri = "http://api.bit.ly/stats?version={version}&shortUrl={url}&login={login}&apiKey={api_key}&format={format}")
    public <T> T stats(@UriParam("url") String url) throws CallException;

    @Call(uri = "http://api.bit.ly/errors?version={version}&login={login}&apiKey={api_key}&format={format}")
    public <T> T errors() throws CallException;

}
