package com.github.spotbugs;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class KotlinBuildScriptTest {

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    private File sourceDir;

    @Before
    public void createKotlinDslProject() throws IOException {
        String buildScript = "plugins {\n" +
                "  java\n" +
                "  id(\"com.github.spotbugs\")\n" +
                "}\n" +
                "version = 1.0\n" +
                "repositories {\n" +
                "  mavenCentral()\n" +
                "  mavenLocal()\n" +
                "}\n" +
                "if(project.hasProperty(\"ignoreFailures\")) { spotbugs.setIgnoreFailures(true) }";
        File buildFile = folder.newFile("build.gradle.kts");
        Files.write(buildFile.toPath(), buildScript.getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE);

        sourceDir = folder.newFolder("src", "main", "java");
        File to = new File(sourceDir, "Foo.java");
        File from = new File("src/test/java/com/github/spotbugs/Foo.java");
        Files.copy(from.toPath(), to.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
    }

    @Test
    public void TestSpotBugsTasksExist() throws Exception{
        BuildResult result = GradleRunner.create()
                .withProjectDir(folder.getRoot())
                .withArguments(Arrays.asList("tasks", "--all"))
                .withPluginClasspath()
                .build();
        assertTrue(result.getOutput().contains("spotbugsMain"));
        assertTrue(result.getOutput().contains("spotbugsTest"));
    }

    @Test
    @Ignore
    public void testSpotBugsTaskCanRun() throws Exception {
        BuildResult result = GradleRunner.create()
                .withProjectDir(folder.getRoot())
                .withArguments(Arrays.asList("compileJava", "spotbugsMain"))
                .withPluginClasspath()
                .build();
        Optional<BuildTask> spotbugsMain = findTask(result, ":spotbugsMain");
        assertTrue(spotbugsMain.isPresent());
        assertThat(spotbugsMain.get().getOutcome(), is(TaskOutcome.SUCCESS));
        assertTrue(new File(folder.getRoot(), "build/reports/spotbugs/main.xml").exists());
    }

    private Optional<BuildTask> findTask(BuildResult result, String taskName) {
        return result.getTasks().stream()
                .filter(task -> task.getPath().equals(taskName))
                .findAny();
    }

}