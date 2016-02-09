package pl.compci.ppm.panel.store;

import pl.com.comp.peopay.model.core.MerchantService;
import pl.com.comp.peopay.model.core.Terminal;
import pl.com.comp.peopay.model.core.TerminalService;
import pl.com.comp.peopay.modelcpd.EvoucherTransaction;
import pl.com.comp.peopay.modelcpd.PspTransactionHistory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author Szymon Kuklewicz
 */
@Stateless
public class StoreServiceDao {

    @PersistenceContext(unitName = "panel")
    EntityManager panelEntityManager;

    @PersistenceContext(unitName = "cpd")
    EntityManager cpdEntityManager;

    public List<Terminal> getTerminalsByLogin(String login) {
        return panelEntityManager
                .createQuery("select t from Terminal t, Merchant m where t.merchant = m and m.login = :login", Terminal.class)
                .setParameter("login", login)
                .getResultList();
    }

    public List<EvoucherTransaction> getEvoucherHistory(String terminalNo) {
        return cpdEntityManager
                .createQuery("select e from EvoucherTransaction e where e.terminalNo = :terminalNo", EvoucherTransaction.class)
                .setParameter("terminalNo", terminalNo)
                .getResultList();

    }

    public List<PspTransactionHistory> getBlikHistory(String terminalNo) {
        return cpdEntityManager
                .createQuery("select p from PspTransactionHistory p where p.terminalNumber = :terminalNo", PspTransactionHistory.class)
                .setParameter("terminalNo", terminalNo)
                .getResultList();
    }


    public List<MerchantService> getServices2ListByLogin(String login) {
        return panelEntityManager
                .createQuery("select distinct ms from MerchantService ms " +
                        "left outer join fetch ms.service s, Merchant m " +
                        "where ms.merchant = m and m.login = :login and (ms.endDate is null or ms.status = 'N')", MerchantService.class)
                .setParameter("login", login)
                .getResultList();
    }


    public List<TerminalService> getServicesListByLogin(String login) {
        return panelEntityManager
                .createQuery("select distinct ts from TerminalService ts " +
                        "left outer join fetch ts.service s, Terminal t, Merchant m " +
                        "where ts.terminal = t and m.login = :login and ts.endDate is null or current_timestamp  < ts.endDate", TerminalService.class)
                .setParameter("login", login)
                .getResultList();
    }

}
