package danfe.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.StringReader;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class XmlUtil {

    public static <T> Object toObject(String xmlContent, Class<T> clazz) throws JAXBException {
        var jaxbContext = JAXBContext.newInstance(clazz);
        var unmarshaller = jaxbContext.createUnmarshaller();

        var reader = new StringReader(xmlContent);
        return ((JAXBElement) unmarshaller.unmarshal(reader)).getValue();
    }

}