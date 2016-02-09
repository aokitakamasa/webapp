package pl.compci.ppm.panel.profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.com.comp.peopay.model.access.User;
import pl.com.comp.peopay.model.core.Address;
import pl.com.comp.peopay.model.core.Merchant;
import pl.com.comp.peopay.model.core.MerchantParam;
import pl.com.comp.peopay.model.dictionaries.Common;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * @author Marcin Wlazły
 */
@Stateless
public class UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    @PersistenceContext(unitName = "panel")
    EntityManager entityManager;

    public String getUserPasswordByLogin(final String login){
        Query query = entityManager.createNativeQuery("select u.password from s_users u where u.login = :login");
        query.setParameter("login", login);
        return (String)query.getSingleResult();
    }

    public void changeUserPassword(final String login, final String hashedPassword) {
        Query query = entityManager.createNativeQuery("update s_users set password = :password where login = :login");
        query.setParameter("login", login);
        query.setParameter("password", hashedPassword);
        query.executeUpdate();
    }

    public Merchant getMerchantByLogin(String login) {
        Query query = entityManager.createQuery("select m from Merchant m where m.login=:login");
        query.setParameter("login", login);
        return (Merchant) query.getSingleResult();
    }


    public List<Address> getAddressByLogin(String login) {
        Query query = entityManager.createQuery("select a from Address a, Merchant m where a.merchant = m and m.login=:login");
        query.setParameter("login", login);
        return (List<Address>) query.getResultList();
    }

    public MerchantParam getMerchantParamByLogin(String login) {
        Query query = entityManager.createQuery("select m from MerchantParam m where m.merchant.login = :login ");
        query.setParameter("login", login);
        return (MerchantParam) query.getSingleResult();
    }

    public void changeTerminalName (final Long id, final String newName) {
        Query query = entityManager.createQuery("update Terminal set name = :name where id = :id");
        query.setParameter("id", id);
        query.setParameter("name", newName);
        query.executeUpdate();
    }

    public User getTokenUser(String token) {
        try {
            TypedQuery<User> query = entityManager.createQuery("select ut.user from UserToken ut " +
              "where ut.token = :encodedToken and ut.used is null and ut.created > :expiration", User.class);
            query.setParameter("encodedToken", encodeWithSha256(token + "{t5ci19}"));
            query.setParameter("expiration", Date.from(Instant.now().minus(1, ChronoUnit.DAYS)));
            List<User> users = query.getResultList();
            return users.size() == 1 ? users.get(0) : null;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            logger.warn("", e);
            return null;
        }
    }

    public User getUserByToken(String token) {
      try {
        TypedQuery<User> query = entityManager.createQuery("select ut.user from UserToken ut " +
          "where ut.token = :encodedToken", User.class);
        query.setParameter("encodedToken", encodeWithSha256(token + "{t5ci19}"));
        List<User> users = query.getResultList();
        return users.size() == 1 ? users.get(0) : null;
      } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
        logger.warn("", e);
        return null;
      }
    }

    private String encodeWithSha256(String string) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(string.getBytes("UTF-8"));
        return DatatypeConverter.printHexBinary(md.digest()).toLowerCase();
    }

    public String savePassword(String token, String password) {
        try {
            User user = getTokenUser(token);
            if (user == null) {
                return null;
            }
            Query query = entityManager.createQuery(
                    "update User u set u.locked = 'N', u.emailStatus = 'A', u.password = :password where u.id = :user_id");
            query.setParameter("password", encodeWithSha256(password));
            query.setParameter("user_id", user.getId());
            if (query.executeUpdate() != 1) {
                return null;
            }
            query = entityManager.createQuery(
                    "update UserToken ut set ut.used = :used where ut.token = :encodedToken");
            query.setParameter("used", new Date());
            query.setParameter("encodedToken", encodeWithSha256(token + "{t5ci19}"));
            query.executeUpdate();
            return user.getLogin();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            logger.warn("", e);
            return null;
        }
    }

    public List<Common> getUserPasswordConfig() {
        TypedQuery<Common> query = entityManager.createQuery("select c from Common c where c.commonType.code = 'USER_CONFIG'", Common.class);
        return query.getResultList();
    }
}
