package danfe.type;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Objects;

import static danfe.util.Constants.FONT_SIZE;
import static danfe.util.Constants.LOCALE_PT_BR;

public abstract class AbstractPdf {

    protected PdfFont labelFont;
    protected PdfFont textFont;
    protected PdfFont barcodeFont;

    /**
     * @param document
     * @param lstContent Put values in pairs, like key/value (label/text).
     */
    protected void addLabelAndText(Document document, String... lstContent) {
        var paragraph = new Paragraph().setMargin(0);
        int idx = 0;
        for (String content : lstContent) {
            Text text = null;
            if (++idx % 2 != 0) {
                if (idx > 1) {
                    content = "    " + content; // Add extra space between the labels
                }
                text = new Text(content)
                        .setFont(labelFont)
                        .setFontSize(FONT_SIZE);
            } else {
                text = new Text(" " + content)
                        .setFont(textFont)
                        .setFontSize(FONT_SIZE);
            }
            paragraph.add(text);
        }
        document.add(paragraph);
    }

    protected void addLabel(Document document, String content) {
        addLabel(document, content, null);
    }

    protected void addLabel(Document document, String content, TextAlignment textAlign) {
        Text label = new Text(content)
                .setFont(labelFont)
                .setFontSize(FONT_SIZE);
        document.add(new Paragraph(label)
                .setMargin(0)
                .setTextAlignment(Objects.requireNonNullElse(textAlign, TextAlignment.LEFT)));
    }

    protected void addText(Document document, String content) {
        addText(document, content, null);
    }

    protected void addText(Document document, String content, TextAlignment textAlign) {
        addText(document, content, textAlign, null);
    }

    protected void addText(Document document, String content, TextAlignment textAlign, Integer[] rgb) {
        Text value = new Text(content)
                .setFont(textFont)
                .setFontSize(FONT_SIZE);
        if (rgb != null && rgb.length == 3) {
            value.setBackgroundColor(new DeviceRgb(rgb[0], rgb[1], rgb[2]));
        }
        document.add(new Paragraph(value)
                .setMargin(0)
                .setTextAlignment(Objects.requireNonNullElse(textAlign, TextAlignment.LEFT)));
    }

    protected void addTextWithBackground(Document document, String content, Integer[] rgb) {
        addText(document, content, null, rgb);
    }

    protected void addBarcode(Document document, String content) {
        Text value = new Text(content)
                .setFont(barcodeFont)
                .setFontSize(35)
                .setHorizontalScaling(0.5f);
        // HorizontalScaling adjusts the width by compressing in the horizontal direction.
        document.add(new Paragraph(value)
                .setMargin(0)
                .setTextAlignment(TextAlignment.CENTER)
                .setFixedLeading(30f));
        // Barcode add extra spaces, so "FixedLeading" adjusts it.
    }

    protected void addHorizontalLine(Document document) {
        addHorizontalLine(document, true);
    }

    protected void addHorizontalLine(Document document, boolean isSolid) {
        ILineDrawer line = null;
        if (isSolid) {
            line = new SolidLine(0.5f);
        } else {
            line = new DashedLine(0.5f);
        }
        line.setColor(ColorConstants.BLACK);
        LineSeparator ls = new LineSeparator(line);
        ls.setWidth(UnitValue.createPercentValue(100)); // Line width in percentage (based on the document printable area)
        document.add(ls);
    }

    protected String formatCpfCnpj(String content) {
        if (content != null) {
            if (content.length() == 11)
                return content.substring(0, 3) + "." +
                        content.substring(3, 6) + "." +
                        content.substring(6, 9) + "-" +
                        content.substring(9);
            else if (content.length() == 14)
                return content.substring(0, 2) + "." +
                        content.substring(2, 5) + "." +
                        content.substring(5, 8) + "/" +
                        content.substring(8, 12) + "-" +
                        content.substring(12);
        }
        return content;
    }

    protected String formatNfeKey(String nfeKey) {
        return nfeKey.replaceAll("(.{4})", "$1 ").trim();
        // "(.{4})"  Captures any 4 characters sequence.
        // "$1 "     Replace it with the same string and add a space at the end.
    }

    protected String formatMonetaryValue(String amount) {
        // Format for BRL value, ex: 123456.79 --> R$ 123.456,79
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(LOCALE_PT_BR);
        return currencyFormatter.format(new BigDecimal(amount));
    }
}
