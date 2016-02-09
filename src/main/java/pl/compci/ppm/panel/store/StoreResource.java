package pl.compci.ppm.panel.store;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import pl.com.comp.peopay.model.core.MerchantService;
import pl.com.comp.peopay.model.core.Terminal;
import pl.com.comp.peopay.model.core.TerminalService;
import pl.com.comp.peopay.modelcpd.EvoucherTransaction;
import pl.com.comp.peopay.modelcpd.PspTransactionHistory;
import pl.compci.ppm.panel.profile.ProfileResource;

import javax.annotation.security.RolesAllowed;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.ejb.ConcurrencyManagementType.BEAN;
import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;

@Singleton
@ConcurrencyManagement(BEAN)
@Path("/") @Api("store")
@Consumes(APPLICATION_FORM_URLENCODED)
@Produces("application/json;charset=utf-8")
public class StoreResource {

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @EJB
    private StoreServiceDao storeServiceDao;

    @EJB
    private ProfileResource profileResource;

    @Context
    ResourceContext rc;

    @ApiOperation("Returns detailed information about merchant's terminals and services")
    @GET @Path("/")
    @RolesAllowed({"Admin", "Merchant"})
    public JsonObject store() {
        JsonArrayBuilder terminalsArrayBuilder = createArrayBuilder();

        List<Terminal> terminals = storeServiceDao.getTerminalsByLogin(profileResource.getLogin());
        List<TerminalService> services = storeServiceDao.getServicesListByLogin(profileResource.getLogin());
        Map<String, Integer> terminalCountByService = new HashMap<>();

        for (Terminal terminal : terminals) {
            JsonArrayBuilder serviceArrayBuilder = createArrayBuilder();

            for (TerminalService terminalService : services) {
                if (terminalService.getTerminalId()== terminal.getId()) {
                    serviceArrayBuilder.add(terminalService.getService().getCode().toLowerCase());
                    String serviceCode = terminalService.getService().getCode().toLowerCase();
                    if (terminalCountByService.get(serviceCode) == null) {
                        terminalCountByService.put(serviceCode, 1);
                    } else {
                        terminalCountByService.put(serviceCode, terminalCountByService.get(serviceCode) + 1);
                    }
                }
            }
            terminalsArrayBuilder.add(createObjectBuilder()
                            .add("name", terminal.getName())
                            .add("id", terminal.getId())
                            .add("number", terminal.getBeb())
                            .add("image", "/panel/img/kasa.png")
                            .add("limits", createObjectBuilder()
                                            .add("products", createLimits(8644, 25321))
                                            .add("packages", createLimits(9, 64))
                            )
                            .add("services", serviceArrayBuilder)

            );
        }
        return createObjectBuilder()
                .add("merchant", profileResource.merchant())
                .add("terminals", terminalsArrayBuilder)
                .add("services", services(terminalCountByService))
                .build();
    }

    private JsonObjectBuilder createLimits(int current, int max) {
        return createObjectBuilder()
                .add("current", current)
                .add("max", max);
    }

    private JsonArray services(Map<String, Integer> terminalCountByService) {
        JsonArrayBuilder merchantServicesArrayBuilder = createArrayBuilder();

        for (MerchantService merchantService : storeServiceDao. getServices2ListByLogin(profileResource.getLogin())) {
            String code = merchantService.getService().getCode().toLowerCase();
            Integer terminalCount = terminalCountByService.get(code);
            merchantServicesArrayBuilder.add(createObjectBuilder()
                            .add("service", code)
                            .add("name", merchantService.getService().getName())
                            .add("startDate", merchantService.getStartDate() == null? "-" : dateFormat.format(merchantService.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))
                            .add("endDate", merchantService.getEndDate() == null? "-" : dateFormat.format(merchantService.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))
                            .add("status", merchantService.getService().getStatus())
                            .add("terminalCount", terminalCount==null?0:terminalCount)
            );
        }
        return merchantServicesArrayBuilder.build();
    }

    @ApiOperation("DEPRECATED - NOT USED")
    @GET @Path("/terminals/{terminalNo}/evoucherHistory")
    @RolesAllowed({"Admin", "Merchant"})
    public JsonArray getEvoucherHistory(@PathParam("terminalNo")String terminalNo) {
        JsonArrayBuilder evoucherHistoryArrayBuilder = createArrayBuilder();
        for (EvoucherTransaction evoucherTransaction : storeServiceDao.getEvoucherHistory(terminalNo)) {
            evoucherHistoryArrayBuilder.add(
                    createObjectBuilder()
                            .add("id", evoucherTransaction.getId())
                            .add("cashAmount", evoucherTransaction.getCashAmount())
                            .add("productCode", evoucherTransaction.getProductCode())
                            .add("state", evoucherTransaction.getState())
                            .add("terminalNo", evoucherTransaction.getTerminalNo())
                            .add("started", evoucherTransaction.getStarted().toString())

            );
        }
        return evoucherHistoryArrayBuilder.build();
    }

    @ApiOperation("DEPRECATED - NOT USED")
    @GET @Path("terminals/{terminalNo}/blikHistory")
    @RolesAllowed({"Admin", "Merchant"})
    public JsonArray getBlikHistory (@PathParam("terminalNo")String terminalNo) {
        JsonArrayBuilder blikHistoryArrayBuilder = createArrayBuilder();
        for (PspTransactionHistory pspTransactionHistory : storeServiceDao.getBlikHistory(terminalNo)) {
            blikHistoryArrayBuilder.add(
                    createObjectBuilder()
                            .add("id", pspTransactionHistory.getId())
                            .add("cashAmount", pspTransactionHistory.getCashAmount())
      //                      .add("errorMessage", pspTransactionHistory.getErrorMessage())
     //                       .add("pspTransactionId", pspTransactionHistory.getPspTransactionId())
                            .add("state", pspTransactionHistory.getState())
                            .add("terminalNumber", pspTransactionHistory.getTerminalNumber())
                            .add("transactionId", pspTransactionHistory.getTransactionId())
                            .add("timestamp", pspTransactionHistory.getTimestamp().toString())

            );
        }
        return blikHistoryArrayBuilder.build();
    }

}
