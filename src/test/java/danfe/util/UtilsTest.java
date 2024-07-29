package danfe.util;

import danfe.dto.nfev400.TNfeProc;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class UtilsTest {

    @DisplayName("Given a XML file, must load the content.")
    @Test
    void loadFile() throws IOException {
        var file = "/sample-xml-nfe.xml";
        var strData = FileUtils.readFromFileName(file);
        log.debug("XML content: {}", strData);

        assertNotNull(strData);
    }

    @DisplayName("Given a NFe XML, must parse it into a java object.")
    @Test
    void parseXmlFile() throws IOException, JAXBException {
        var file = "/sample-xml-nfe.xml";
        var strData = FileUtils.readFromFileName(file);

        var nfe = (TNfeProc) XmlUtil.toObject(strData, TNfeProc.class);

        assertNotNull(nfe);
        assertEquals("92422411000193", nfe.getNFe().getInfNFe().getEmit().getCNPJ());
        assertEquals("12330602643603333137550019999319221457124111", nfe.getProtNFe().getInfProt().getChNFe());
    }

}
