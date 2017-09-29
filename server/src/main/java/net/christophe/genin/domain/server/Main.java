package net.christophe.genin.domain.server;

import io.vertx.core.Launcher;

import java.io.IOException;
import java.util.logging.LogManager;

/**
 * Main Classe
 */
public class Main extends Launcher {

    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("/logging.properties"));
            new Main().dispatch(
                    args
            );
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }
}
