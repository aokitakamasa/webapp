package pl.compci.ppm.panel.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Adds header for outgoing requests to bo.api to enable HTTP Basic Authentication
 *
 * @author Szymon Kuklewicz
 */
public class BoApiClientAuthenticator implements ClientRequestFilter {

  private final String authorizationHeader;

  public BoApiClientAuthenticator() throws NamingException, UnsupportedEncodingException {
    String login = InitialContext.doLookup("java:global/app/panel/remote/bo.api/login");
    String password = InitialContext.doLookup("java:global/app/panel/remote/bo.api/password");
    String token = login + ":" + password;
    authorizationHeader = "Basic " + DatatypeConverter.printBase64Binary(token.getBytes("UTF-8"));
  }

  @Override
  public void filter(ClientRequestContext requestContext) throws IOException {
    requestContext.getHeaders().add("Authorization", authorizationHeader);
  }

}
