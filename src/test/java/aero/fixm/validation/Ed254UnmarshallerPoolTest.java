package aero.fixm.validation;

import aero.fixm.ed254.ArrivalSequence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Ed254UnmarshallerPoolTest {

    private Ed254UnmarshallerPool pool;

    @BeforeEach
    void setUp() {
        pool = new Ed254UnmarshallerPool();
    }

    @Test
    void validArrivalSequenceUnmarshalsSuccessfully() throws Exception {
        String xml = loadXml("/ed254-valid/SEQ_LPPT_016.xml");

        Object result = pool.unmarshalAndValidate(xml);

        assertThat(result).isInstanceOf(ArrivalSequence.class);
        ArrivalSequence seq = (ArrivalSequence) result;
        assertThat(seq.getAerodromeDesignator()).isEqualTo("LPPT");
        assertThat(seq.getCreationTime()).isNotNull();
    }

    @Test
    void nullXmlThrowsUnmarshalException() {
        assertThatThrownBy(() -> pool.unmarshalAndValidate(null))
                .isInstanceOf(Ed254UnmarshallerPool.Ed254UnmarshalException.class)
                .hasMessageContaining("null or empty");
    }

    @Test
    void emptyXmlThrowsUnmarshalException() {
        assertThatThrownBy(() -> pool.unmarshalAndValidate("   "))
                .isInstanceOf(Ed254UnmarshallerPool.Ed254UnmarshalException.class)
                .hasMessageContaining("null or empty");
    }

    @Test
    void invalidXmlThrowsUnmarshalException() {
        assertThatThrownBy(() -> pool.unmarshalAndValidate("<not-valid/>"))
                .isInstanceOf(Ed254UnmarshallerPool.Ed254UnmarshalException.class);
    }

    @Test
    void allValidSamplesUnmarshalWithoutError() throws Exception {
        String[] samples = {
                "SEQ_EGLL_001.xml", "SEQ_EDDF_004.xml", "SEQ_EHAM_010.xml",
                "SEQ_LEMD_033.xml", "SEQ_LPPT_016.xml", "SEQ_LFPG_017.xml",
                "SEQ_EGKK_005.xml", "SEQ_EHAM_outage_recovery_004.xml",
                "SEQ_ESSA_normal_001.xml", "SEQ_LFPG_advisories_003.xml"
        };

        for (String sample : samples) {
            String xml = loadXml("/ed254-valid/" + sample);
            assertThatCode(() -> pool.unmarshalAndValidate(xml))
                    .as("Failed for: %s", sample)
                    .doesNotThrowAnyException();
        }
    }

    private String loadXml(String resourcePath) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return new String(is.readAllBytes());
        }
    }
}
