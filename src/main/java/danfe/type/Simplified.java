package danfe.type;

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.properties.TextAlignment;
import danfe.config.BusinessConfig;
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
import static danfe.config.BusinessConfig.*;
import static danfe.util.Constants.DOCUMENT_MARGIN;

@Slf4j
public class Simplified extends AbstractPdf {

    /**
     * Generate a PDF file with each "DANFE Simplificada" of the XML contents.
     *
     * @param lstNfe
     * @return
     * @throws IOException
     */
    public File generatePdf(List<TNfeProc> lstNfe) throws IOException {
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
            addText(document, formatNfeKey(infProt.getChNFe()), TextAlignment.CENTER);

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

            if ("1".equals(BusinessConfig.get(PARAM_SHOW_INVOICE_TOTAL))) {
                var vICMS = infNfe.getTotal().getICMSTot().getVNF();
                // TODO consider adding the ISSQN value here
                addLabelAndText(document, "VALOR TOTAL DA NOTA:", formatMonetaryValue(vICMS));
            }

            if (lstNfe.indexOf(nfe) < lstNfe.size() - 1) {
                addBreakLayout(lstNfe, nfe, document);
            }
        }

        // Close the document
        document.close();

        var fPdf = new File(fileDestination);
        log.info("DANFE successfully created at: {}", fPdf.getAbsolutePath());
        return fPdf;
    }

    private void addBreakLayout(List<TNfeProc> lstNfe, TNfeProc nfe, Document document) {
        log.debug("Break layout config: {}", BusinessConfig.get(PARAM_LAYOUT_BREAK));
        if ("1".equals(BusinessConfig.get(PARAM_LAYOUT_BREAK))) {
            // Jump the next column.
            document.add(new AreaBreak());
        } else if ("2".equals(BusinessConfig.get(PARAM_LAYOUT_BREAK))) {
            // Since we have 2 columns the break jumps to the next column. So, 2 breaks jump the page.
            document.add(new AreaBreak()).add(new AreaBreak());
        } else {
            log.debug("Break page (true) or add line (false): {} idx={}", ((lstNfe.indexOf(nfe) + 1) % 2 == 0), lstNfe.indexOf(nfe) + 1);
            // Without jump (only when it reaches the end of the sheet).
            if ((lstNfe.indexOf(nfe) + 1) % 2 == 0) {
                // Every 2 NFes performs a page break in order to jump to the next column.
                document.add(new AreaBreak());
            } else {
                // Adds a dashed line between 2 NFes.
                addHorizontalLine(document, false);
                // document.add(new Paragraph("\n")); // To add extra lines, if needed.
            }
        }
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
        // StringBuilder is faster, but StringBuffer is thread-safe.
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

        // TODO create an interface to do that!
        var address = dest.getEnderDest();
        var streetName = address.getXLgr();
        var number = address.getNro();
        var streetComplement = address.getXCpl();
        var neighborhood = address.getXBairro();
        var city = address.getXMun();
        var stateAcronym = address.getUF();
        var postalCode = address.getCEP();
        var phoneNumber = address.getFone();

        var shipping = infNfe.getEntrega();
        if (shipping != null) {
            streetName = shipping.getXLgr();
            number = shipping.getNro();
            streetComplement = shipping.getXCpl();
            neighborhood = shipping.getXBairro();
            city = shipping.getXMun();
            stateAcronym = shipping.getUF();
            postalCode = shipping.getCEP();
            phoneNumber = shipping.getFone();
        }

        addLabel(document, "DESTINATÁRIO", TextAlignment.CENTER);
        addLabelAndText(document, "NOME:", dest.getXNome());

        // TODO add apache commons lang library to validate empty config/phoneNumber
        var showPhone = Objects.requireNonNullElse(BusinessConfig.get(PARAM_SHOW_CUSTOMER_PHONE), "").trim().isEmpty()
                || "1".equals(BusinessConfig.get(PARAM_SHOW_CUSTOMER_PHONE));
        if (showPhone && !Objects.requireNonNullElse(phoneNumber, "").trim().isEmpty()) {
            // Add text with yellow background --> RGB(255, 255, 0)
            addTextWithBackground(document, formatPhoneNumber(phoneNumber), new Integer[]{255, 255, 0});
        }

        addLabelAndText(document, "CPF/CNPJ:", formatCpfCnpj(Objects.requireNonNullElse(dest.getCPF(), dest.getCNPJ())),
                "IE:", Objects.requireNonNullElse(dest.getIE(), ""),
                "UF:", dest.getEnderDest().getUF().value());

        addLabelAndText(document, "ENDEREÇO:",
                String.format("%s, %s - %s",
                        streetName,
                        number,
                        Objects.requireNonNullElse(streetComplement, "")),
                "Bairro:", neighborhood);

        addLabelAndText(document,
                "", String.format("%s/%s", city.toUpperCase(), stateAcronym),
                "CEP:", formatPostalCode(postalCode));
    }

    private void putEmit(TNFe.InfNFe infNfe, Document document) {
        var emit = infNfe.getEmit();
        addLabel(document, "EMITENTE", TextAlignment.CENTER);
        addLabelAndText(document, "NOME:", Objects.requireNonNullElse(emit.getXFant(), emit.getXNome()));
        addLabelAndText(document, "CNPJ:", formatCpfCnpj(emit.getCNPJ()),
                "IE:", Objects.requireNonNullElse(emit.getIE(), ""),
                "UF:", emit.getEnderEmit().getUF().value());
    }

}
