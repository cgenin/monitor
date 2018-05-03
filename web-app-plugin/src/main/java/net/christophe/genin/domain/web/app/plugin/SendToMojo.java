package net.christophe.genin.domain.web.app.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import java.util.HashMap;

@Mojo(name = "sendToMonitor", defaultPhase = LifecyclePhase.INSTALL, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
@Execute(goal = "sendToMonitor", phase = LifecyclePhase.INSTALL)
public class SendToMojo extends AbstractMojo {

    private final static Gson gson = new GsonBuilder().create();

    @Parameter(defaultValue = "${basedir}/package.json", required = true)
    private String packageJsonPath;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter()
    private Boolean sendResult = Boolean.FALSE;

    @Parameter()
    private String url = "http://localhost:8082/application-monitor/app";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            String jsonData = createJson(project, packageJsonPath);
            Sender.builder(sendResult, this.url, getLog()).put(jsonData);
        } catch (Exception ex) {
            getLog().error("Error in executing the plugin", ex);
        }
    }


    public String createJson(MavenProject project, String packageJsonPath) {
        HashMap<String, Object> jsons = new HashMap<>();
        jsons.put("artifactId", project.getArtifactId());
        jsons.put("groupId", project.getGroupId());
        jsons.put("version", project.getVersion());

        new ReadPackageJson(packageJsonPath, getLog()).load().ifPresent(map -> jsons.put("packagesJson", map));
        return gson.toJson(jsons);
    }

    void setPackageJsonPath(String packageJsonPath) {
        this.packageJsonPath = packageJsonPath;
    }

    void setProject(MavenProject project) {
        this.project = project;
    }

    void setSendResult(Boolean sendResult) {
        this.sendResult = sendResult;
    }

    void setUrl(String url) {
        this.url = url;
    }
}
