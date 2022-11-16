package ec.nbdemetra.spreadsheet;

import ec.nbdemetra.core.NbmMavenClassPath;
import nbbrd.io.FileParser;
import org.assertj.core.api.Condition;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.jar.Manifest;

import static org.assertj.core.api.Assertions.assertThat;

public class RuntimeDependenciesTest {

    @Test
    public void test() throws IOException {
        assertThat(getRuntimeDependencies())
                .describedAs("Check runtime dependencies")
                .satisfies(RuntimeDependenciesTest::checkDemetra)
                .satisfies(RuntimeDependenciesTest::checkJavaIoUtil)
                .satisfies(RuntimeDependenciesTest::checkSpreadsheet4j)
                .satisfies(RuntimeDependenciesTest::checkSlf4j)
                .satisfies(RuntimeDependenciesTest::checkLog4j)
                .satisfies(RuntimeDependenciesTest::checkGuava)
                .hasSize(24);
    }

    private static void checkGuava(List<? extends NbmMavenClassPath.GAV> coordinates) {
        assertThatGroupId(coordinates, "com.google.guava").isEmpty();
    }

    private static void checkLog4j(List<? extends NbmMavenClassPath.GAV> coordinates) {
        assertThatGroupId(coordinates, "org.apache.logging.log4j")
                .has(sameVersion())
                .extracting(NbmMavenClassPath.GAV::getArtifactId)
                .containsExactlyInAnyOrder("log4j-api", "log4j-to-slf4j");
    }

    private static void checkSlf4j(List<? extends NbmMavenClassPath.GAV> coordinates) {
        assertThatGroupId(coordinates, "org.slf4j").isEmpty();
    }

    private static void checkSpreadsheet4j(List<? extends NbmMavenClassPath.GAV> coordinates) {
        assertThatGroupId(coordinates, "com.github.nbbrd.spreadsheet4j")
                .has(sameVersion())
                .extracting(NbmMavenClassPath.GAV::getArtifactId)
                .containsExactlyInAnyOrder(
                        "spreadsheet-api",
                        "spreadsheet-html",
                        "spreadsheet-od",
                        "spreadsheet-poi",
                        "spreadsheet-util",
                        "spreadsheet-xl",
                        "spreadsheet-xmlss");
    }

    private static void checkJavaIoUtil(List<? extends NbmMavenClassPath.GAV> coordinates) {
        assertThatGroupId(coordinates, "com.github.nbbrd.java-io-util").isEmpty();
    }

    private static void checkDemetra(List<? extends NbmMavenClassPath.GAV> coordinates) {
        assertThatGroupId(coordinates, "eu.europa.ec.joinup.sat")
                .has(sameVersion())
                .extracting(NbmMavenClassPath.GAV::getArtifactId)
                .containsExactlyInAnyOrder("demetra-spreadsheet");
    }

    private static ListAssert<? extends NbmMavenClassPath.GAV> assertThatGroupId(List<? extends NbmMavenClassPath.GAV> coordinates, String groupId) {
        return assertThat(coordinates)
                .describedAs("Check " + groupId)
                .filteredOn(NbmMavenClassPath.GAV::getGroupId, groupId);
    }

    private static Condition<List<? extends NbmMavenClassPath.GAV>> sameVersion() {
        return new Condition<>(NbmMavenClassPath.GAV::haveSameVersion, "same version");
    }

    private static List<NbmMavenClassPath.GAV> getRuntimeDependencies() throws IOException {
        return FileParser.onParsingStream(Manifest::new)
                .andThen(NbmMavenClassPath::parse)
                .parseResource(RuntimeDependenciesTest.class, "/runtime-dependencies.mf");
    }
}
