package danfe.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String PDF_FORMAT = "pdf";
    public static final String XML_FORMAT = "xml";
    public static final int DOCUMENT_MARGIN = 20;
    public static final int FONT_SIZE = 9;
    public static final Locale LOCALE_PT_BR = new Locale("pt", "BR");

}
