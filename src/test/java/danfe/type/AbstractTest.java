package danfe.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AbstractTest {

    @Test
    void cpfFormatting() {
        var cpf = "12345678900";

        var formattedCpf = new Simplified().formatCpfCnpj(cpf);

        assertEquals("123.456.789-00", formattedCpf);
    }

    @Test
    void cnpjdFormatting() {
        var cnpj = "12345678000199";

        var formattedCnpj = new Simplified().formatCpfCnpj(cnpj);

        assertEquals("12.345.678/0001-99", formattedCnpj);
    }

    @Test
    void nonStandardValues() {
        var value1 = "1234";
        var value2 = "123456789012";

        var formattedNullValue = new Simplified().formatCpfCnpj(null);
        var formattedValue1 = new Simplified().formatCpfCnpj(value1);
        var formattedValue2 = new Simplified().formatCpfCnpj(value2);

        assertNull(formattedNullValue);
        assertEquals("1234", formattedValue1);
        assertEquals("123456789012", formattedValue2);
    }

    @Test
    void nfeKeyFormatting() {
        var nfeKey = "12345678901234567890123456789012345678904444";

        var formattedNfeKey = new Simplified().formatNfeKey(nfeKey);

        assertEquals("1234 5678 9012 3456 7890 1234 5678 9012 3456 7890 4444", formattedNfeKey);
    }

}
