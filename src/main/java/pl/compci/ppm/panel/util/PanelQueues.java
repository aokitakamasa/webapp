package pl.compci.ppm.panel.util;

import pl.com.comp.peopay.model.core.Address;
import pl.com.comp.peopay.model.core.Merchant;
import pl.com.comp.peopay.model.core.MerchantParam;
import pl.compci.ppm.esb.JsonOut;
import pl.compci.ppm.esb.ProducerBean;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.json.Json;
import java.io.IOException;
import java.util.List;

import static javax.ejb.ConcurrencyManagementType.BEAN;

@Singleton
@ConcurrencyManagement(BEAN)
public class PanelQueues {

    public static final String PROXY_SMTP_QUEUE = "proxy.smtp.in";
    public static final String PAYBACK_EMAIL = "michal.barela@comp-ci.pl";
    public static final String PAYBACK_SUBJECT = "Zgłoszenie zainteresowania usługami Payback";

    @EJB
    private ProducerBean producerBean;

    public void sendPaybackActivationToProxy(Merchant merchant, List<Address> addresses, MerchantParam merchantParam, String category, String location) throws IOException {
        producerBean.publishMessage(PROXY_SMTP_QUEUE, new JsonOut()
                        .add("recipients", Json.createArrayBuilder().add(PAYBACK_EMAIL))
                        .add("subject", PAYBACK_SUBJECT)
                        .add("body", createPaybackMessage(merchant, addresses, merchantParam, category, location))
        );
    }

    private String createPaybackMessage(Merchant merchant, List<Address> addresses, MerchantParam merchantParam, String category, String location) {
        StringBuilder sb = new StringBuilder();
        sb.append("Merchant <strong> ");
        sb.append(merchantParam.getCompanyName());
        sb.append("</strong> zgłasza zainteresowanie usługami Payback. Merchant udostępnił następujące dane:<br/> Adres: ");
        sb.append(addresses.get(0).getStreet());
        sb.append(" ");
        sb.append(addresses.get(0).getBuilding());
        String local = addresses.get(0).getLocal();
        if (local != null) {
            sb.append("/");
            sb.append(local);
        }
        sb.append(", ");
        sb.append(addresses.get(0).getPostalCodeString());
        sb.append(" ");
        sb.append(addresses.get(0).getCity());
        sb.append("<br/> Dane merchanta: ");
        sb.append(merchant.getName());
        sb.append(" ");
        sb.append(merchant.getSurname());
        sb.append("<br/> NIP: ");
        sb.append(merchant.getNip());
        sb.append("<br/> E-mail: ");
        sb.append(merchant.getEmail());
        sb.append("<br/> Telefon: ");
        sb.append(merchant.getPhone());
        sb.append("<br/> Kategoria: ");
        sb.append(category);
        sb.append("<br/> Szczegóły lokalizacji: ");
        sb.append(location);
        return sb.toString();
    }

}