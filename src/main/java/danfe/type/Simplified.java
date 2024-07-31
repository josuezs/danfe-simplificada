package danfe.type;

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import danfe.dto.nfev400.TNFe;
import danfe.dto.nfev400.TNfeProc;
import danfe.util.Constants;
import danfe.util.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.itextpdf.io.font.constants.StandardFonts.HELVETICA;
import static com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD;
import static danfe.util.Constants.DOCUMENT_MARGIN;

@Slf4j
public class Simplified extends AbstractPdf {

    /**
     * Generate a PDF file with each "DANFE Simplificada" of the XML contents.
     *
     * @param lstNfe
     * @param samePage If <code>false</code> will generate one page per XML.
     * @return
     * @throws IOException
     */
    public File generatePdf(List<TNfeProc> lstNfe, boolean samePage) throws IOException {
        log.info("Starting DANFE generation...");
        loadFonts();

        String fileDestination = "danfe." + Constants.PDF_FORMAT;
        FileUtils.deleteFile(fileDestination);

        Document document = createDocument(fileDestination);

        for (TNfeProc nfe : lstNfe) {
            addLabel(document, "DANFE SIMPLIFICADO - ETIQUETA", TextAlignment.CENTER);

            addHorizontalLine(document);

            var infProt = nfe.getProtNFe().getInfProt();
            addLabel(document, "CHAVE DE ACESSO:");
            addBarcode(document, infProt.getChNFe());
            addText(document, infProt.getChNFe(), TextAlignment.CENTER);

            addHorizontalLine(document);
            addLabelAndText(document, "PROTOCOLO DE AUTORIZAÇÃO:", infProt.getNProt() + " - " + infProt.getDhRecbto().substring(0, 10));

            var infNfe = nfe.getNFe().getInfNFe();
            var ide = infNfe.getIde();
            addLabelAndText(document, "NOTA:", ide.getNNF(), "SÉRIE:", ide.getSerie());
            addLabelAndText(document, "EMISSÃO:", ide.getDhEmi().substring(0, 10),
                    "TIPO OPERAÇÃO:", getOperationType(ide.getTpNF()));

            addHorizontalLine(document);
            putDest(infNfe, document);

            addHorizontalLine(document);
            putEmit(infNfe, document);

            addHorizontalLine(document);
            var vICMS = infNfe.getTotal().getICMSTot().getVNF();
            // TODO consider adding the ISSQN value here
            addLabelAndText(document, "VALOR TOTAL DA NOTA:", "R$ " + vICMS.replace(".", ",")); // replace for BRL decimal separator

            if (samePage) {
                log.debug("Break page (true) or add line (false): {} idx={}", ((lstNfe.indexOf(nfe) + 1) % 2 == 0), lstNfe.indexOf(nfe) + 1);
                if ((lstNfe.indexOf(nfe) + 1) % 2 == 0) {
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

    private void loadFonts() throws IOException {
        // Unfortunately, when using static loading of Font files, the itext library generates an exception from the second generation of the PDF.
        try {
            labelFont = PdfFontFactory.createFont(HELVETICA_BOLD);
            textFont = PdfFontFactory.createFont(HELVETICA);
            barcodeFont = PdfFontFactory.createFont("fonts/LibreBarcode128-Regular.ttf");
        } catch (IOException e) {
            log.error("Error to create HELVETICA font: verify your OS if this font exists.", e);
            throw new IOException(e);
        }
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

    private static String getOperationType(String operationType) {
        var description = new StringBuffer(operationType).append(" - ");
        if ("0".equals(operationType)) {
            description.append("Entrada");
        } else {
            description.append("Saída");
        }
        return description.toString();
    }

    private void putDest(TNFe.InfNFe infNfe, Document document) {
        var dest = infNfe.getDest();
        var address = dest.getEnderDest();
        addLabel(document, "DESTINATÁRIO", TextAlignment.CENTER);
        addLabelAndText(document, "NOME:", dest.getXNome());
        addLabelAndText(document, "CPF/CNPJ:", Objects.requireNonNullElse(dest.getCPF(), dest.getCNPJ()),
                "IE:", Objects.requireNonNullElse(dest.getIE(), ""),
                "UF:", address.getUF().value());
        addLabelAndText(document, "ENDEREÇO:",
                String.format("%s, %s - %s", address.getXLgr(), address.getNro(), address.getXCpl()));
        addText(document,
                String.format("Bairro: %s - %s/%s", address.getXBairro(), address.getXMun(), address.getUF()));
    }

    private void putEmit(TNFe.InfNFe infNfe, Document document) {
        var emit = infNfe.getEmit();
        addLabel(document, "EMITENTE", TextAlignment.CENTER);
        addLabelAndText(document, "NOME:", emit.getXNome());
        addLabelAndText(document, "CNPJ:", emit.getCNPJ(),
                "IE:", Objects.requireNonNullElse(emit.getIE(), ""),
                "UF:", emit.getEnderEmit().getUF().value());
    }

}
