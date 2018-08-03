package net.christophe.genin.domain.web.app.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class SendToMojoTest {

    private MavenProject mavenProject;
    private static final String JSON_EXAMPLE = "{\"packagesJson\":{\"name\":\"test-web\",\"version\":\"0.0.1-SNAPSHOT\",\"description\":\"Une application de test\",\"main\":\"index.js\",\"scripts\":{},\"keywords\":[\"mdpa\",\"sinistre\"],\"private\":true,\"author\":\"Christophe Genin\",\"license\":\"ISC\",\"devDependencies\":{\"@types/angular\":\"^1.6.23\",\"@types/angular-ui-bootstrap\":\"^0.13.43\",\"@types/angular-ui-router\":\"1.1.37\",\"@types/chrome\":\"0.0.50\",\"@types/node\":\"^8.0.4\",\"angular-mocks\":\"1.5.9\",\"babel-core\":\"6.26.0\",\"babel-eslint\":\"^7.1.1\",\"babel-loader\":\"7.0.0\",\"babel-plugin-transform-class-properties\":\"^6.24.1\",\"babel-plugin-transform-object-rest-spread\":\"^6.23.0\",\"clean-webpack-plugin\":\"0.1.16\",\"compression-webpack-plugin\":\"^1.0.1\",\"css-loader\":\"^0.28.2\",\"eslint\":\"^3.12.2\",\"eslint-loader\":\"1.7.1\",\"expose-loader\":\"0.7.3\",\"extract-text-webpack-plugin\":\"2.1.0\",\"file-loader\":\"0.11.1\",\"home-or-tmp\":\"^2.0.0\",\"isparta-instrumenter-loader\":\"^1.0.1\",\"istanbul-instrumenter-loader\":\"^2.0.0\",\"jasmine-core\":\"^2.5.2\",\"karma\":\"^1.3.0\",\"karma-chrome-launcher\":\"^2.0.0\",\"karma-coverage\":\"^1.1.1\",\"karma-ie-launcher\":\"^1.0.0\",\"karma-jasmine\":\"^1.1.0\",\"karma-sourcemap-loader\":\"^0.3.7\",\"karma-spec-reporter\":\"^0.0.26\",\"karma-webpack\":\"2.0.3\",\"mdpa-pre-build\":\"1.0.0\",\"null-loader\":\"0.1.1\",\"raw-loader\":\"0.5.1\",\"style-loader\":\"0.18.0\",\"ts-loader\":\"^2.2.0\",\"typescript\":\"2.4.0\",\"url-loader\":\"0.5.8\",\"webpack\":\"^3.3.0\",\"webpack-dev-server\":\"2.4.5\",\"webpack-merge\":\"4.1.0\",\"webpack-node-externals\":\"1.6.0\"},\"dependencies\":{\"@types/systemjs\":\"^0.20.6\",\"@uirouter/angularjs\":\"1.0.10\",\"acteur-service-client\":\"1.5.2-SNAPSHOT\",\"acteur-services\":\"1.5.1\",\"adresse-services\":\"1.0.1\",\"ajout-ged-component\":\"1.1.1-27\",\"angular\":\"1.5.9\",\"angular-animate\":\"1.5.9\",\"angular-aria\":\"1.5.9\",\"angular-block-ui\":\"^0.2.2\",\"angular-circular-navigation\":\"^0.2.1\",\"angular-file-upload\":\"^2.3.4\",\"angular-i18n\":\"1.5.9\",\"angular-loading-bar\":\"0.8.0\",\"angular-message-format\":\"1.5.9\",\"angular-resource\":\"1.5.9\",\"angular-sanitize\":\"1.5.9\",\"angular-toasty\":\"^1.0.5\",\"angular-touch\":\"1.5.9\",\"angular-ui-bootstrap\":\"1.3.3\",\"angular-ui-grid\":\"^4.0.11\",\"animate.css\":\"^3.5.2\",\"babel-polyfill\":\"6.3.14\",\"babel-preset-es2015\":\"6.3.13\",\"babel-preset-es2017\":\"6.24.1\",\"babel-register\":\"6.4.3\",\"banque-service-client\":\"1.2.2-SNAPSHOT\",\"bloc-adresse\":\"1.0.4\",\"bloc-codepostal-ville\":\"0.0.3-SNAPSHOT\",\"bloc-coordonnees\":\"0.0.1-SNAPSHOT\",\"bloc-email\":\"^1.0.1-SNAPSHOT\",\"bootstrap\":\"^3.3.7\",\"collapsable-header\":\"1.0.1\",\"common-filters\":\"1.1.0\",\"comptabilite-service-client\":\"^1.1.1-SIN-SNAPSHOT\",\"contrat-generique-service-angular\":\"1.2.6\",\"contrat-generique-service-client\":\"1.2.6\",\"contrat-incendie-service-angular\":\"1.1.5\",\"contrat-vam-service-angular\":\"1.1.5\",\"decompte-de-service-angular\":\"0.0.1-SNAPSHOT\",\"droit-service-client\":\"1.4.5\",\"droit-services\":\"1.4.5\",\"font-awesome\":\"^4.7.0\",\"ged-list\":\"1.0.0\",\"ged-service-client\":\"2.1.3\",\"iban\":\"1.0.1-9\",\"intranet-service-client\":\"1.0.4\",\"lodash\":\"3.10.1\",\"mail-service-angular\":\"1.0.3\",\"mdpa-toogles\":\"^1.0.0-1\",\"mdpa1-menu\":\"3.0.8\",\"menu-application\":\"1.1.1\",\"ng-sortable\":\"1.3.6\",\"oclazyload\":\"^1.1.0\",\"partenaires-recherche-directive\":\"1.0.1-SNAPSHOT\",\"partenaires-service-client\":\"1.0.5-SNAPSHOT\",\"pdfmake\":\"^0.1.34\",\"rangy\":\"1.3.0\",\"societe-services\":\"1.0.3-SNAPSHOT\",\"textangular\":\"1.5.16\",\"timestamp-date\":\"^0.0.1-2\",\"ui-grid\":\"^0.0.0\",\"util-services\":\"2.0.3-SNAPSHOT\",\"wcs-nature-prod-service-client\":\"1.1.4-SNAPSHOT\",\"webcomponents.js\":\"^0.7.23\",\"webpack-manifest-plugin\":\"1.1.0\"}},\"groupId\":\"fr.test\",\"artifactId\":\"artifact-id\",\"version\":\"1.0.0-SNAPSHOT\"}";
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

    @Test
    public void should_read_packages_json() throws MojoFailureException, MojoExecutionException {
        SendToMojo mojo = new SendToMojo();


        String json = mojo.createJson(mavenProject, "src/test/resources/package.json");

        Assertions.assertThat(json).isEqualTo(JSON_EXAMPLE);

    }


}
