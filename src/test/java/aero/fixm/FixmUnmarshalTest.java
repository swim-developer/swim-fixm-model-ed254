package aero.fixm;

import aero.fixm.ed254.ArrivalSequence;
import aero.fixm.validation.Ed254UnmarshallerPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FIXM ED-254 JAXB Unmarshal")
class FixmUnmarshalTest {

    private static Ed254UnmarshallerPool pool;

    @BeforeAll
    static void setup() {
        pool = new Ed254UnmarshallerPool();
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("Valid XML — should unmarshal successfully")
    @ValueSource(strings = {
        "SEQ_EGLL_001.xml",
        "SEQ_EDDF_004.xml",
        "SEQ_EHAM_010.xml",
        "SEQ_LEMD_033.xml",
        "SEQ_LPPT_016.xml",
        "SEQ_LFPG_017.xml",
        "SEQ_EGKK_005.xml",
        "SEQ_EHAM_outage_recovery_004.xml",
        "SEQ_ESSA_normal_001.xml",
        "SEQ_LFPG_advisories_003.xml"
    })
    void validXml_shouldUnmarshalSuccessfully(String filename) throws Exception {
        String xml = loadXml("/ed254-valid/" + filename);

        Object result = pool.unmarshalAndValidate(xml);

        assertThat(result).isInstanceOf(ArrivalSequence.class);
        ArrivalSequence seq = (ArrivalSequence) result;
        assertThat(seq.getAerodromeDesignator())
            .as("aerodromeDesignator must be present")
            .isNotNull()
            .isNotEmpty();
        assertThat(seq.getCreationTime())
            .as("creationTime must be present")
            .isNotNull();
        assertThat(seq.getSequenceEntries())
            .as("sequenceEntries must be present")
            .isNotNull();
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("Invalid XML — should fail with validation error")
    @ValueSource(strings = {
        "INVALID_SEQ_EDDF_015.xml",
        "INVALID_SEQ_EHAM_099.xml",
        "INVALID_SEQ_EIDW_024.xml"
    })
    void invalidXml_shouldFailWithValidationError(String filename) {
        assertThatThrownBy(() -> pool.unmarshalAndValidate(loadXml("/ed254-invalid/" + filename)))
            .isInstanceOf(Ed254UnmarshallerPool.Ed254UnmarshalException.class)
            .hasMessageContaining("validation failed");
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
