package pl.compci.ppm.panel.profile;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.com.comp.peopay.model.access.User;
import pl.com.comp.peopay.model.core.*;
import pl.compci.ppm.panel.util.BoApiClient;
import pl.compci.ppm.panel.util.PanelQueues;

import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static javax.ejb.ConcurrencyManagementType.BEAN;
import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static pl.compci.ppm.panel.util.StandardJson.Type.DANGER;
import static pl.compci.ppm.panel.util.StandardJson.Type.SUCCESS;
import static pl.compci.ppm.panel.util.StandardJson.standardJson;

@Singleton
@ConcurrencyManagement(BEAN)
@Path("/") @Api("merchant")
@Consumes(APPLICATION_FORM_URLENCODED)
@Produces("application/json;charset=utf-8")
public class ProfileResource {

    private static final Logger logger = LoggerFactory.getLogger(ProfileResource.class);

    @Context
    ResourceContext rc;

    @Resource
    SessionContext sessionContext;

    @EJB
    private UserDao userDao;

    @EJB
    private PanelQueues panelQueues;

    @EJB
    private BoApiClient boApiClient;

    @ApiOperation("Log out merchant")
    @POST @Path("/logout")
    public void logout(@Context HttpServletRequest request) throws ServletException {
        request.logout();
    }

    @ApiOperation("DEPRECATED")
    @GET @Path("/user/username")
    public JsonObject username() {
        return createObjectBuilder().add("username", getLogin()).build();
    }

    @ApiOperation("Change password for logged in merchant")
    @POST @Path("/user/changePassword")
    public void changePassword(
            @FormParam("currentPassword") @NotNull @ApiParam(required = true, value = "Current password") String currentPassword,
            @FormParam("newPassword") @NotNull @ApiParam(required = true, value = "New password") String newPassword
    ) {
        String login = sessionContext.getCallerPrincipal().getName();
        String currentHashedPassword;
        String newHashedPassword;

        try {
            currentHashedPassword = sha256Encoding(currentPassword);
            newHashedPassword = sha256Encoding(newPassword);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            logger.error("Cannot encode password " + newPassword);
            throw new ServerErrorException("Cannot encode password", INTERNAL_SERVER_ERROR, e);
        }
        // todo PASSWORD POLICY VALIDATION
        if(!isPasswordMatch(login, currentHashedPassword)) {
            throw new ForbiddenException("Current password is incorrect");
        }
        userDao.changeUserPassword(login, newHashedPassword);
    }

    @ApiOperation("Updates merchant's name of terminal")
    @PUT @Path("/terminals/{id}/name")
    public void terminalName(
            @PathParam("id") @NotNull @ApiParam(required = true, value = "Terminal ID") Long id,
            @FormParam("name") @NotNull @ApiParam(required = true, value = "New name") String newName
    ) {
        // todo CHECK IF TERMINAL BELONGS TO MERCHANT
        userDao.changeTerminalName(id, newName);
    }

    @ApiOperation("DEPRECATED")
    @GET @Path("/address")
    public JsonArray address() {
        JsonArrayBuilder addressArrayBuilder = createArrayBuilder();
        for (Address address : userDao.getAddressByLogin(getLogin())) {
            addressArrayBuilder.add(
                    createObjectBuilder()
                            .add("city", address.getCity())
                            .add("street", address.getStreet())
                            .add("building", address.getBuilding())
                            .add("local", address.getLocal())
                            .add("postalCode", address.getPostalCodeString())

            );
        }
        return addressArrayBuilder.build();
    }

    @ApiOperation("DEPRECATED")
    @GET @Path("/merchant")
    public JsonObject merchant() {
        Merchant loadedMerchant = userDao.getMerchantByLogin(getLogin());
        MerchantParam merchantParam = userDao.getMerchantParamByLogin(getLogin());
        JsonObjectBuilder result = createObjectBuilder()
                .add("id", loadedMerchant.getId())
                .add("login", loadedMerchant.getLogin())
                .add("name", loadedMerchant.getName())
                .add("surname", loadedMerchant.getSurname())
                .add("email", loadedMerchant.getEmail())
                .add("companyName", merchantParam.getCompanyName());
        if (loadedMerchant.getPhone() != null && loadedMerchant.getPhone().length() > 0) {
            result.add("phone", loadedMerchant.getPhone());
        }
        return result.build();
    }

    @ApiOperation("DEPRECATED")
    @POST @Path("/payback/lead")
    public JsonObject confirm(JsonObject additionalData) {
        String category = additionalData.getString("category");
        String location = additionalData.getString("location");
        Merchant merchant = userDao.getMerchantByLogin(getLogin());
        List<Address> addresses = userDao.getAddressByLogin(getLogin());
        MerchantParam merchantParam = userDao.getMerchantParamByLogin(getLogin());
        try {
            panelQueues.sendPaybackActivationToProxy(merchant, addresses, merchantParam, category, location);
            return standardJson(SUCCESS, "Activation successful").build();
        } catch (IOException e) {
            return standardJson(DANGER, "Activation error").build();
        }
    }

    @ApiOperation("Responses if token exists and not expired and also if true, sends password policy, that must be satisfied for token to be consumed.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Token is valid"),
            @ApiResponse(code = 400, message = "Token not specified"),
            @ApiResponse(code = 404, message = "Token not found or expired")
    })
    @POST @Path("/user/validateToken")
    public PasswordPolicy validateToken(
            @FormParam("token") @NotNull @ApiParam(required = true, value = "Token for password reset") String token
    ) {
        return boApiClient.postEntity("/merchant/validateToken", PasswordPolicy.class, new Form()
          .param("token", token));
    }

    @ApiOperation("Validates password according to the policy, sets password to the merchant, unlocks account and consumes the token.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Consuming token finished with success"),
            @ApiResponse(code = 400, message = "Token or password not specified"),
            @ApiResponse(code = 404, message = "Token not found or expired"),
            @ApiResponse(code = 422, message = "Password does not meet password policy")
    })
    @POST @Path("/user/consumeToken")
    public ConsumeTokenResponse consumeToken(
            @FormParam("token") @NotNull @ApiParam(required = true, value = "Token for password reset") String token,
            @FormParam("password") @NotNull @ApiParam(required = true, value = "New password") String password
    ) {
        return boApiClient.postEntity("/merchant/consumeToken", ConsumeTokenResponse.class, new Form()
          .param("token", token)
          .param("password", password));
    }

    @ApiOperation("Finds a merchant by the given email address and sends him a message that allows him to reset his password.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully sent a message to a merchant with the given email adress"),
            @ApiResponse(code = 400, message = "Email address not specified"),
            @ApiResponse(code = 404, message = "Email address not found")})
    @POST @Path("/user/forgotPassword")
    public Response forgotPassword(
            @FormParam("email") @NotNull @ApiParam(required = true, value = "Email address") String email
    ) {
        return boApiClient.postResponse("/merchant/forgotPassword", new Form()
          .param("email", email));
    }

    @ApiOperation("Finds a merchant by the given token and sends him a message about successful account activation.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully sent a message to a merchant with the given token"),
            @ApiResponse(code = 400, message = "Token not specified")})
    @POST @Path("/user/activationConfirmed")
    public Response activationConfirmed(
            @FormParam("token") @NotNull @ApiParam(required = true, value = "Token for password reset") String token
    ) {
        User user = userDao.getUserByToken(token);
        return boApiClient.postResponse("/merchant/activationConfirmed", new Form()
          .param("email", user.getEmail()));
    }

    public String getLogin() {
        return sessionContext.getCallerPrincipal().getName();
    }

    public Long getUserId(){
      Merchant merchant = userDao.getMerchantByLogin(getLogin());
      return merchant.getId();
    }

    private boolean isPasswordMatch(final String login, final String currentHashedPassword) {
        String currentDatabaseHashedPassword =  userDao.getUserPasswordByLogin(login);
        return currentDatabaseHashedPassword.equals(currentHashedPassword);
    }

    private String sha256Encoding(final String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashedPasswordBytes = digest.digest(password.getBytes("UTF-8"));

        StringBuilder hashedPasswordString = new StringBuilder();
        for (byte hashedPasswordByte : hashedPasswordBytes) {
            hashedPasswordString.append(Integer.toString((hashedPasswordByte & 0xff) + 0x100, 16).substring(1));
        }

        return hashedPasswordString.toString();
    }

    @ApiOperation("Generates and sends an issue by an email to OTRS system.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully sent an issue to OTRS system"),
            @ApiResponse(code = 400, message = "Issue, merchant or message not specified"),
            @ApiResponse(code = 404, message = "Merchant not found"),
    })
    @POST @Path("/otrs/sendIssue")
    public Response otrsSendIssue(
            @FormParam("issue") @NotNull(message = "Issue not specified") @ApiParam(required = true, value = "Category of issue") String issue,
            @FormParam("terminal") @ApiParam("Number of terminal") String terminalNumber,
            @FormParam("service") @ApiParam("Code of service") String service,
            @FormParam("message") @NotNull(message = "Message not specified") @ApiParam(required = true, value = "Issue message") String message
    ) {
        return boApiClient.postResponse("/otrs/sendIssue", new Form()
          .param("userId", getUserId().toString())
          .param("issue", issue)
          .param("terminalNumber", terminalNumber)
          .param("service", service)
          .param("message", message));
    }

    @ApiOperation("List files available for merchant")
    @ApiResponse(code = 200, message = "List of available files")
    @GET @Path("/files")
    public List<pl.com.comp.peopay.model.core.Document> files(
    ){
      return boApiClient.getListByClass("/merchant/files", Document.class, "userId", getUserId());
    }

    @ApiOperation("Returns content of requested file")
    @ApiResponse(code = 200, message = "Binary file")
    @ApiResponses({
      @ApiResponse(code = 403, message = "Merchant cannot view specified file"),
      @ApiResponse(code = 404, message = "File or version not found")})
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @GET @Path("/files/{id}")
    public Response file(
      @PathParam("id") @NotNull @ApiParam(required = true, value = "File ID") Long fileId
    ){
      return boApiClient.getFile("/merchant/files/{id}", fileId, getUserId());

    }

  @ApiOperation("List files available for merchant")
  @ApiResponse(code = 200, message = "List of news")
  @GET @Path("/news")
  public List<News> news(
  ){
    return boApiClient.getListByClass("/merchant/news", News.class, null, null);
  }

}
