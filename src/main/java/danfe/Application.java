package danfe;

import danfe.dto.nfev400.TNfeProc;
import danfe.type.Simplified;
import danfe.util.FileUtils;
import danfe.util.XmlUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import static danfe.util.Constants.XML_FORMAT;

@Slf4j
public class Application {

    public static void main(String[] args) {
        log.info("Starting...");
        try {
            var currentDir = new File(".");
            FilenameFilter xmlFilter = (dir, name) -> name.toLowerCase().endsWith("." + XML_FORMAT);

            log.info("Reading XML from directory {}", currentDir.getAbsolutePath());
            var xmlFiles = currentDir.listFiles(xmlFilter);

            if (xmlFiles != null && xmlFiles.length > 0) {
                log.info("Found {} XML files. Start reading..", xmlFiles.length);
                var lstNfe = generateXmlIntoList(xmlFiles);

                if (lstNfe.isEmpty()) {
                    log.info("No elegible XML found.");
                } else {
                    new Simplified().generatePdf(lstNfe, true);
                    log.info("Successfully concluded!");
                }
            } else {
                log.info("No XML files found.");
            }
        } catch (Exception e) {
            log.info("Error =/", e);
        }
    }

    private static ArrayList<TNfeProc> generateXmlIntoList(File[] xmlFiles) {
        var lstNfe = new ArrayList<TNfeProc>();
        for (File xmlFile : xmlFiles) {
            try {
                var xmlContent = FileUtils.readFromFile(xmlFile);
                var nfeObject = (TNfeProc) XmlUtil.toObject(xmlContent, TNfeProc.class);
                lstNfe.add(nfeObject);
            } catch (Exception e) {
                log.error("Skipping XML {} with error {}", xmlFile, e.getMessage());
            }
        }
        return lstNfe;
    }

}
