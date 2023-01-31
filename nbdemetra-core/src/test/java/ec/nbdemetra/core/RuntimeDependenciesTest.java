package ec.nbdemetra.core;

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
                .satisfies(RuntimeDependenciesTest::checkSlf4j)
                .satisfies(RuntimeDependenciesTest::checkGuava)
                .hasSize(21);
    }

    private static void checkGuava(List<? extends NbmMavenClassPath.GAV> coordinates) {
        assertThatGroupId(coordinates, "com.google.guava")
                .extracting(NbmMavenClassPath.GAV::getArtifactId)
                .contains("guava")
                .hasSize(3);
    }

    private static void checkSlf4j(List<? extends NbmMavenClassPath.GAV> coordinates) {
        assertThatGroupId(coordinates, "org.slf4j")
                .has(sameVersion())
                .extracting(NbmMavenClassPath.GAV::getArtifactId)
                .containsExactlyInAnyOrder("slf4j-api", "slf4j-jdk14", "jcl-over-slf4j");
    }

    private static void checkJavaIoUtil(List<? extends NbmMavenClassPath.GAV> coordinates) {
        assertThatGroupId(coordinates, "com.github.nbbrd.java-io-util")
                .has(sameVersion())
                .hasSize(3);
    }

    private static void checkDemetra(List<? extends NbmMavenClassPath.GAV> coordinates) {
        assertThatGroupId(coordinates, "eu.europa.ec.joinup.sat")
                .has(sameVersion())
                .extracting(NbmMavenClassPath.GAV::getArtifactId)
                .containsExactlyInAnyOrder("demetra-tss", "demetra-tstoolkit", "demetra-utils", "demetra-workspace");
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
