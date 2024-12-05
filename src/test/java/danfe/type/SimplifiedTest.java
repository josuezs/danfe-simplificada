package danfe.type;

import danfe.dto.nfev400.TNfeProc;
import danfe.util.FileUtils;
import danfe.util.XmlUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimplifiedTest {

    @DisplayName("Given a XML file, must generate a PDF file.")
    @Test
    void generatePdfFile() throws IOException, JAXBException {
        var file = "/sample-xml-nfe.xml";
        var strData = FileUtils.readFromFileName(file);

        var nfe = (TNfeProc) XmlUtil.toObject(strData, TNfeProc.class);

        var pdfFile = new Simplified().generatePdf(List.of(nfe), true);

        assertNotNull(strData);
        assertNotNull(pdfFile);
        assertTrue(pdfFile.exists());
    }

    @DisplayName("Given multiple XML file, must generate a PDF file. After execution, please check the layout by opening the generated PDF file.")
    @Test
    void validateVisualLayout() throws IOException, JAXBException {
        var file = "/sample-xml-nfe.xml";
        var strData = FileUtils.readFromFileName(file);

        var nfe = (TNfeProc) XmlUtil.toObject(strData, TNfeProc.class);
        var nfe2 = (TNfeProc) XmlUtil.toObject(strData, TNfeProc.class);
        var nfe3 = (TNfeProc) XmlUtil.toObject(strData, TNfeProc.class);
        var nfe4 = (TNfeProc) XmlUtil.toObject(strData, TNfeProc.class);
        var nfe5 = (TNfeProc) XmlUtil.toObject(strData, TNfeProc.class);

        var pdfFile = new Simplified().generatePdf(List.of(nfe, nfe2, nfe3, nfe4, nfe5), true);

        assertNotNull(strData);
        assertNotNull(pdfFile);
        assertTrue(pdfFile.exists());
    }

    @Test
    void formatNfeKey() {
        var nfeKey = "12345678901234567890123456789012345678904444";
        var formattedValue = new Simplified().formatNfeKey(nfeKey);
        assertEquals("1234 5678 9012 3456 7890 1234 5678 9012 3456 7890 4444", formattedValue);
    }

    @Test
    void formatMonetaryValue() {
        var amount = "123456.78";
        var formattedValue = new Simplified().formatMonetaryValue(amount);
        assertEquals("R$ 123.456,78", formattedValue);
    }

}
