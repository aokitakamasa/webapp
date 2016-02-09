package pl.compci.ppm.panel;

import io.swagger.jaxrs.config.BeanConfig;
import pl.compci.ppm.panel.profile.ProfileResource;
import pl.compci.ppm.panel.store.StoreResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("api")
public class PanelApplication extends Application {

    public PanelApplication() {
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setTitle("M\\platform Merchant API (panel.api)");
        beanConfig.setVersion("1.2.0");
        beanConfig.setBasePath("/panel/api");
        beanConfig.setResourcePackage("pl.compci.ppm.panel");
        beanConfig.setScan(true);
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();

        resources.add(io.swagger.jaxrs.listing.ApiListingResource.class);
        resources.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);

        resources.add(ProfileResource.class);
        resources.add(StoreResource.class);

        return resources;
    }

}