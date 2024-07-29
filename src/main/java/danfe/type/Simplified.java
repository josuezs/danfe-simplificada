package danfe.type;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import danfe.dto.nfev400.TNfeProc;
import danfe.util.Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.itextpdf.io.font.constants.StandardFonts.*;
import static danfe.util.Constants.*;

@Slf4j
public class Simplified {

    private static final PdfFont LABEL_FONT;
    private static final PdfFont TEXT_FONT;
    private static final PdfFont BARCODE_FONT;

    static {
        try {
            LABEL_FONT = PdfFontFactory.createFont(HELVETICA_BOLD);
            TEXT_FONT = PdfFontFactory.createFont(HELVETICA);
            BARCODE_FONT = PdfFontFactory.createFont("src/main/resources/fonts/LibreBarcode128-Regular.ttf");
        } catch (IOException e) {
            log.error("Error to create HELVETICA font: verify your OS if this font exists.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate a PDF file with each "DANFE Simplificada" of the XML contents.
     * @param lstNfe
     * @param samePage If <code>false</code> will generate one page per XML.
     * @return
     * @throws IOException
     */
    public static File generatePdf(List<TNfeProc> lstNfe, boolean samePage) throws IOException {
        log.info("Starting DANFE generation...");
        String fileDestination = "danfe." + Constants.PDF_FORMAT;

        Document document = createDocument(fileDestination);

        for (TNfeProc nfe: lstNfe) {
            addLabel(document, "DANFE SIMPLIFICADO - ETIQUETA", TextAlignment.CENTER);

            addHorizontalLine(document);

            var infProt = nfe.getProtNFe().getInfProt();
            addLabel(document, "CHAVE DE ACESSO:");
            addBarcode(document, infProt.getChNFe());
            addText(document, infProt.getChNFe());

            addHorizontalLine(document);
            addLabelAndText(document, "PROTOCOLO DE AUTORIZAÇÃO:", infProt.getNProt() + " - " + infProt.getDhRecbto().substring(0, 10));

            var infNfe = nfe.getNFe().getInfNFe();
            var ide = infNfe.getIde();
            addLabelAndText(document, "NOTA:", ide.getNNF(), "SÉRIE:", ide.getSerie());
            addLabelAndText(document, "EMISSÃO:", ide.getDhEmi().substring(0, 10),
                    "TIPO OPERAÇÃO:", getOperationType(ide.getTpNF()));

            addHorizontalLine(document);
            var emit = infNfe.getEmit();
            addLabelAndText(document, "EMITENTE:", emit.getXNome());
            addLabelAndText(document, "CNPJ:", emit.getCNPJ(),
                    "IE:", Objects.requireNonNullElse(emit.getIE(), ""),
                    "UF:", emit.getEnderEmit().getUF().value());

            addHorizontalLine(document);
            var dest = infNfe.getDest();
            addLabelAndText(document, "DESTINATÁRIO:", dest.getXNome());
            addLabelAndText(document,"CPF/CNPJ:", Objects.requireNonNullElse(dest.getCPF(), dest.getCNPJ()),
                    "IE:", Objects.requireNonNullElse(dest.getIE(),""),
                    "UF:", dest.getEnderDest().getUF().value());

            addHorizontalLine(document);
            var vICMS = infNfe.getTotal().getICMSTot().getVNF();
            // TODO consider adding the ISSQN value here
            addLabelAndText(document, "VALOR TOTAL DA NOTA:", "R$ " + vICMS.replace(".", ",")); // replace for BRL decimal separator

            if (samePage) {
                log.debug("Break page (true) or add line (false): {} idx={}", ((lstNfe.indexOf(nfe) + 1) % 2 == 0), lstNfe.indexOf(nfe) + 1);
                if ( (lstNfe.indexOf(nfe) + 1) % 2 == 0) {
                    // Every 2 NFes performs a page break in order to jump to the next column.
                    document.add(new AreaBreak());
                } else {
                    // Adds a dashed line between 2 NFes and extra lines.
                    document.add(new Paragraph("\n"));
                    addHorizontalLine(document, false);
                    document.add(new Paragraph("\n"));
                }
            } else {
                // Since we have 2 columns the break jumps to the next column. So, 2 breaks jump the page.
                document.add(new AreaBreak()).add(new AreaBreak());
            }
        }

        // Close the document
        document.close();

        var fPdf = new File(fileDestination);
        log.info("DANFE successfully created at: {}", fPdf.getAbsolutePath());
        return fPdf;
    }

    private static String getOperationType(String operationType) {
        var description = new StringBuffer(operationType).append(" - ");
        if ("0".equals(operationType)) {
            description.append("Entrada");
        } else {
            description.append("Saída");
        }
        return description.toString();
    }

    private static Document createDocument(String fileDestination) throws FileNotFoundException {
        // Creation of the document
        PdfWriter writer = new PdfWriter(fileDestination);
        PdfDocument pdfDoc = new PdfDocument(writer);
        var pageSize = PageSize.A4;
        pdfDoc.setDefaultPageSize(pageSize); // To define landscape layout, increment it with".rotate()"

        //PdfPage pdfPage = pdfDoc.addNewPage();
        Document document = new Document(pdfDoc);
        document.setMargins(DOCUMENT_MARGIN, DOCUMENT_MARGIN, DOCUMENT_MARGIN, DOCUMENT_MARGIN); // Reduces page margins

        // Calculates width and height for a document with 2 columns
        float columnWidth = (pageSize.getWidth() - document.getLeftMargin() - document.getRightMargin()) / 2; // margin for 2 columns on the page
        float columnHeight = pageSize.getHeight() - document.getTopMargin() - document.getBottomMargin();

        // Define area of each column
        Rectangle[] columns = {
                new Rectangle(document.getLeftMargin(), document.getBottomMargin(), columnWidth, columnHeight),
                new Rectangle(document.getLeftMargin() + columnWidth, document.getBottomMargin(), columnWidth, columnHeight)
        };
        // Apply column rendering
        document.setRenderer(new ColumnDocumentRenderer(document, columns));

        return document;
    }

    /**
     * @param document
     * @param lstContent Put values in pairs, like key/value (label/text).
     */
    private static void addLabelAndText(Document document, String... lstContent) {
        var paragraph = new Paragraph().setMargin(0);
        int idx = 0;
        for (String content : lstContent) {
            Text text = null;
            if (++idx % 2 != 0) {
                if (idx > 1) {
                    content = "    " + content; // Add extra space between the labels
                }
                text = new Text(content)
                        .setFont(LABEL_FONT)
                        .setFontSize(FONT_SIZE);
            } else {
                text = new Text(" " + content)
                        .setFont(TEXT_FONT)
                        .setFontSize(FONT_SIZE);
            }
            paragraph.add(text);
        }
        document.add(paragraph);
    }

    private static void addLabel(Document document, String content) {
        addLabel(document, content, null);
    }

    private static void addLabel(Document document, String content, TextAlignment textAlign) {
        Text label = new Text(content)
                .setFont(LABEL_FONT)
                .setFontSize(FONT_SIZE);
        document.add(new Paragraph(label)
                .setMargin(0)
                .setTextAlignment(Objects.requireNonNullElse(textAlign, TextAlignment.LEFT)));
    }

    private static void addText(Document document, String content) {
        Text value = new Text(content)
                .setFont(TEXT_FONT)
                .setFontSize(FONT_SIZE);
        document.add(new Paragraph(value)
                .setMargin(0)
                .setTextAlignment(TextAlignment.CENTER));
    }

    private static void addBarcode(Document document, String content) {
        Text value = new Text(content)
                .setFont(BARCODE_FONT)
                .setFontSize(18);
        document.add(new Paragraph(value)
                .setMargin(0)
                .setTextAlignment(TextAlignment.CENTER));
    }

    private static void addHorizontalLine(Document document) {
        addHorizontalLine(document, true);
    }

    private static void addHorizontalLine(Document document, boolean isSolid) {
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
