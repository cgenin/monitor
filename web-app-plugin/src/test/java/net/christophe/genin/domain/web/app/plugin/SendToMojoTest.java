package net.christophe.genin.domain.web.app.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class SendToMojoTest {

    private MavenProject mavenProject;

    @Before
    public void before() {
        mavenProject = mock(MavenProject.class);
        when(mavenProject.getArtifactId()).thenReturn("artifact-id");
        when(mavenProject.getGroupId()).thenReturn("fr.test");
        when(mavenProject.getVersion()).thenReturn("1.0.0-SNAPSHOT");
    }

    @Test
    public void shouldExecute() throws MojoFailureException, MojoExecutionException {
        SendToMojo mojo = new SendToMojo();

        mojo.setProject(mavenProject);
        mojo.setSendResult(true);
        mojo.setPackageJsonPath("src/test/resources/package.json");
        mojo.setUrl("http://localhost:8279/api/fronts");
        mojo.execute();

    }
}
