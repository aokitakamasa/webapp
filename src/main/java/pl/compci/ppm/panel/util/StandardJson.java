package pl.compci.ppm.panel.util;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

/**
 * @author Marcin Wlazły
 */
public class StandardJson {

    public enum Type {
        SUCCESS, WARNING, DANGER
    }

    public static JsonObjectBuilder standardJson(Type type, String message) {
        return Json.createObjectBuilder()
                .add("type", type.name().toLowerCase())
                .add("message", message);
    }

}