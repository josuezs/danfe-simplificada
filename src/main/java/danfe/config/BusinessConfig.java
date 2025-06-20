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

    /**
     * Configure to show invoice total (default is No): 0-No, 1-Yes
     */
    public static final String PARAM_SHOW_INVOICE_TOTAL = "show-invoice-total";

    /**
     * Configure to show customer phone (default is Yes): 0-No, 1-Yes
     */
    public static final String PARAM_SHOW_CUSTOMER_PHONE = "show-customer-phone";

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
