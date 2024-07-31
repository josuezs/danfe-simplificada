package danfe.type;

import com.itextpdf.kernel.colors.ColorConstants;
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

import java.util.Objects;

import static danfe.util.Constants.FONT_SIZE;

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
        Text value = new Text(content)
                .setFont(textFont)
                .setFontSize(FONT_SIZE);
        document.add(new Paragraph(value)
                .setMargin(0)
                .setTextAlignment(Objects.requireNonNullElse(textAlign, TextAlignment.LEFT)));
    }

    protected void addBarcode(Document document, String content) {
        Text value = new Text(content)
                .setFont(barcodeFont)
                .setFontSize(18);
        document.add(new Paragraph(value)
                .setMargin(0)
                .setTextAlignment(TextAlignment.CENTER));
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

}
