package pl.compci.ppm.panel.util;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.com.comp.peopay.model.core.Document;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.EJBException;
import javax.ejb.Singleton;
import javax.json.JsonObject;
import javax.naming.NamingException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static javax.ejb.ConcurrencyManagementType.BEAN;
import static javax.ws.rs.core.MediaType.*;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

/**
 * JAX-RS Clients are heavy instances and should be cached as long as possible.
 * RestEasy implementation of JAX-RS states that clients are thread-safe so
 * we can create only one client in the singleton.
 *
 * @author Szymon Kuklewicz
 */
@Singleton
@ConcurrencyManagement(BEAN)
public class BoApiClient {

  private static final Logger logger = LoggerFactory.getLogger(BoApiClient.class);

  private Client client;

  @PostConstruct
  private void postConstruct() {
    client = new ResteasyClientBuilder().connectionPoolSize(10).build();
    try {
      client.register(new BoApiClientAuthenticator());
    } catch (NamingException | UnsupportedEncodingException e) {
      throw new EJBException("Cannot initialize BoApiClient", e);
    }
  }


  @PreDestroy
  private void preDestroy() {
    client.close();
  }

  @Resource(name = "java:global/app/panel/remote/bo.api/url")
  private String remoteBoapiUrl;

  public JsonObject get(String path) {
    try {
      logger.debug("GET " + path);
      return client
        .target(remoteBoapiUrl + path)
        .request(APPLICATION_JSON_TYPE)
        .accept(APPLICATION_JSON)
        .get(JsonObject.class);
    } catch (ProcessingException e) {
      throw new ServerErrorException("Communication to internal API failed", INTERNAL_SERVER_ERROR);
    }
  }

  public Response getFile(String path, Long fileId, Long merchantId) {
    try {
      logger.debug("GET " + path + " " + fileId + " " + merchantId);
      WebTarget target = client.target(remoteBoapiUrl + path).queryParam("userId", merchantId);
      Response internalResponse = target.resolveTemplate("id", fileId).request().get();
      String fileName = internalResponse.getHeaderString("Content-Disposition").split("filename=")[1];
      fileName = fileName.substring(1, fileName.length()-1);
      //todo in Java EE you should not use java.io.File - fix it
      java.io.File output = internalResponse.readEntity(java.io.File.class);
      Response.ResponseBuilder response = Response.ok(output);
      response.header("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
      return response.build();
    } catch (ProcessingException e) {
      throw new ServerErrorException("Communication to internal API failed", INTERNAL_SERVER_ERROR);
    }
  }

  public <T> List<T> getListByClass(String path, Class<T> type, String paramName, Long paramValue){
    try {
      logger.debug("GET " + path);
      ParameterizedType parameterizedGenericType = new ParameterizedType() {
        public Type[] getActualTypeArguments() {
          return new Type[] { type };
        }
        public Type getRawType() {
          return List.class;
        }
        public Type getOwnerType() {
          return List.class;
        }
      };
      GenericType<List<T>> genericType = new GenericType<List<T>>(
        parameterizedGenericType) {
      };
      if(paramName!=null && paramValue!=null) {
        return client.target(remoteBoapiUrl + path).queryParam(paramName, paramValue).request().get(genericType);
      } else {
        return client.target(remoteBoapiUrl + path).request().get(genericType);
      }
    } catch (ProcessingException e) {
      throw new ServerErrorException("Communication to internal API failed", INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Returns transformed response from internal API that can be directly send back to the client.
   */
  public Response postResponse(String path, Form input) {
    try {
      logger.debug("POST " + path);
      Response internalResponse = client
        .target(remoteBoapiUrl + path)
        .request(APPLICATION_FORM_URLENCODED)
        .accept(APPLICATION_JSON)
        .post(Entity.form(input));
      return handleResponse(internalResponse, response -> {
        Response.ResponseBuilder newResponse = Response.status(internalResponse.getStatus());
        if (internalResponse.hasEntity()) newResponse.entity(internalResponse.getEntity());
        return newResponse.build();
      });
    } catch (ProcessingException e) {
      throw new ServerErrorException("Communication to internal API failed", INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Returns JSON extracted from response from internal API, that may send back or further processed.
   */
  public JsonObject postJson(String path, JsonObject input) {
    try {
      logger.debug("POST " + path);
      Response internalResponse = client
        .target(remoteBoapiUrl + path)
        .request(APPLICATION_JSON_TYPE)
        .accept(APPLICATION_JSON)
        .post(Entity.json(input));
      return handleResponse(internalResponse, response -> internalResponse.readEntity(JsonObject.class));
    } catch (ProcessingException e) {
      throw new ServerErrorException("Communication to internal API failed", INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Returns entity extracted from response from internal API, that may send back or further processed.
   */
  public <E> E postEntity(String path, Class<E> clazz, Form input) {
    try {
      logger.debug("POST " + path);
      Response internalResponse = client
        .target(remoteBoapiUrl + path)
        .request(APPLICATION_FORM_URLENCODED_TYPE)
        .accept(APPLICATION_JSON)
        .post(Entity.form(input));
      return handleResponse(internalResponse, response -> internalResponse.readEntity(clazz));
    } catch (ProcessingException e) {
      throw new ServerErrorException("Communication to internal API failed", INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Generic response conversion / entity extraction for bo.api (Backoffice Internal API)
   */
  private <E> E handleResponse(Response internalResponse, Function<Response, E> successAction) {
    switch (internalResponse.getStatus()) {
      case 200:
        return successAction.apply(internalResponse);
      case 404:
        throw new NotFoundException("Entity not found");
      case 422:
        throw new WebApplicationException("Cannot process request", internalResponse.getStatus());
      default:
        throw new ServerErrorException("Response from internal API " + internalResponse.getStatus(), INTERNAL_SERVER_ERROR);
    }
  }

}
