package danfe.config;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class BusinessConfig {

    /**
     * Configure to 0-Without break, 1-Break column or 2-Break page.
     */
    public static final String PARAM_LAYOUT_BREAK = "layout-break";
    private static final Map<String, String> parameters = new HashMap<>();

    public static void loadConfigurations(String[] args) {
        try {
            for (String arg : args) {
                if (arg.startsWith("--")) {
                    // Remove "--" and split in key/value
                    String[] strSplit = arg.substring(2).split("=", 2);
                    if (strSplit.length == 2) {
                        parameters.put(strSplit[0], strSplit[1]);
                    } else {
                        log.warn("Invalid parameter: {}", arg);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to load configuration!", e);
        }
    }

    public static String get(String parameterName) {
        return Objects.requireNonNullElse(
                parameters.get(parameterName),
                ""
        );
    }

}
