package ec.nbdemetra.ui;

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
                .satisfies(RuntimeDependenciesTest::checkNoDemetra)
                .satisfies(RuntimeDependenciesTest::checkJFree)
                .satisfies(RuntimeDependenciesTest::checkJavaDesktopUtil)
                .satisfies(RuntimeDependenciesTest::checkJavaIoUtil)
                .satisfies(RuntimeDependenciesTest::checkNoSlf4j)
                .satisfies(RuntimeDependenciesTest::checkNoGuava)
                .hasSize(14);
    }

    private static void checkNoGuava(List<? extends NbmMavenClassPath.GAV> coordinates) {
        assertThatGroupId(coordinates, "com.google.guava").isEmpty();
    }

    private static void checkNoSlf4j(List<? extends NbmMavenClassPath.GAV> coordinates) {
        assertThatGroupId(coordinates, "org.slf4j").isEmpty();
    }

    private static void checkJavaIoUtil(List<? extends NbmMavenClassPath.GAV> coordinates) {
        assertThatGroupId(coordinates, "com.github.nbbrd.java-io-util")
                .has(sameVersion())
                .extracting(NbmMavenClassPath.GAV::getArtifactId)
                .containsExactlyInAnyOrder("java-io-win");
    }

    private static void checkJFree(List<? extends NbmMavenClassPath.GAV> coordinates) {
        assertThatGroupId(coordinates, "org.jfree")
                .extracting(NbmMavenClassPath.GAV::getArtifactId)
                .containsExactlyInAnyOrder("jfreechart", "jcommon", "jfreesvg");
    }

    private static void checkJavaDesktopUtil(List<? extends NbmMavenClassPath.GAV> coordinates) {
        assertThatGroupId(coordinates, "com.github.nbbrd.java-desktop-util")
                .has(sameVersion())
                .extracting(NbmMavenClassPath.GAV::getArtifactId)
                .hasSize(4);
    }

    private static void checkNoDemetra(List<? extends NbmMavenClassPath.GAV> coordinates) {
        assertThatGroupId(coordinates, "eu.europa.ec.joinup.sat").isEmpty();
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
