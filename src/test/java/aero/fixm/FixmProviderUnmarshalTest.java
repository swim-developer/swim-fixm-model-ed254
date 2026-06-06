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

@DisplayName("FIXM ED-254 Provider Namespace Unmarshal")
class FixmProviderUnmarshalTest {

    private static Ed254UnmarshallerPool pool;

    @BeforeAll
    static void setup() {
        pool = new Ed254UnmarshallerPool();
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("Should unmarshal provider XML to POJO")
    @ValueSource(strings = {
        "/ed254-provider-entity-example2.xml"
    })
    void shouldUnmarshalProviderXml(String resourcePath) throws Exception {
        String xml = loadXml(resourcePath);

        Object result = pool.unmarshalAndValidate(xml);

        assertThat(result).isInstanceOf(ArrivalSequence.class);
        ArrivalSequence seq = (ArrivalSequence) result;
        assertThat(seq.getAerodromeDesignator()).isEqualTo("LOWW");
        assertThat(seq.getCreationTime()).isNotNull();
        assertThat(seq.getSequenceEntries()).isNotNull();
        assertThat(seq.getSequenceEntries().getArrivalManagementInformation())
            .hasSize(8);

        var firstFlight = seq.getSequenceEntries().getArrivalManagementInformation().getFirst();
        assertThat(firstFlight.getFlightIdentification().getArcid()).isEqualTo("AUA9EB");
        assertThat(firstFlight.getFlightIdentification().getAdep()).isEqualTo("LOWI");
        assertThat(firstFlight.getFlightIdentification().getAdes()).isEqualTo("LOWW");
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
