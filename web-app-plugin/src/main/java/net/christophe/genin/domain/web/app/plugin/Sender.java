package net.christophe.genin.domain.web.app.plugin;

import okhttp3.*;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;

/**
 * Classe permettant d'envoyer les informations brut aux serveur.
 */
public interface Sender {


    default void put(String jsonData) {
    }

    /**
     * Factory.
     *
     * @param send envoyer
     * @param url  L'url de destination
     * @param log  le loggueur maven pour afficher les trcaes dans le build maven.
     * @return une instance.
     */
    static Sender builder(boolean send, String url, Log log) {
        if (send) {
            return new SenderOkHttp(url, log);
        }
        return new MockSender();
    }

    /**
     * Implémentation via okHttp.
     */
    class SenderOkHttp implements Sender {

        private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        private final String url;
        private final Log log;

        /**
         * Constructeur
         *
         * @param url l'url
         * @param log log le loggueur maven pour afficher les trcaes dans le build maven.
         */
        private SenderOkHttp(String url, Log log) {
            this.url = url;
            this.log = log;
        }

        @Override
        public void put(String jsonData) {

            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, jsonData);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Erreur lors de l'envoie des données JSON" + response);
                    return;
                }
                log.info("Succès de l'envoie des données du service au serveur (code:" + response.code() + ")");
            } catch (Exception ex) {
                throw new IllegalStateException("Error in sending", ex);
            }

        }

    }

    /**
     * NUll Implementation
     */
    class MockSender implements Sender {
    }
}
