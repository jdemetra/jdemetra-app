package demetra.desktop.core.interchange;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigsTest {

    @Test
    public void testXml() throws IOException {

        Configs configs = Configs.xmlParser().parseResource(ConfigsTest.class, "configs.xml");

        assertThat(configs.getAuthor()).isEqualTo("CHARPHI");
        assertThat(configs.getCreationTime()).isEqualTo(1630569881523L);
        assertThat(configs.getItems())
                .hasSize(1)
                .element(0)
                .satisfies(config -> {
                    assertThat(config.getDomain()).isEqualTo("ec.tss.tsproviders.DataSource");
                    assertThat(config.getName()).isEqualTo("Insee.xlsx");
                    assertThat(config.getVersion()).isEqualTo("");
                    assertThat(config.getParameters())
                            .hasSize(1)
                            .containsEntry("uri", "demetra://tsprovider/XCLPRVDR/20111201?file=Insee.xlsx");
                });

        String text = Configs.xmlFormatter(true).formatToString(configs);
        assertThat(Configs.xmlParser().parseChars(text)).isEqualTo(configs);
    }
}
