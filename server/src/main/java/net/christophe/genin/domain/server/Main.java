package net.christophe.genin.domain.server;

import io.vertx.core.Launcher;

/**
 * Main Classe
 */
public class Main extends Launcher {

    public static void main(String[] args) {
        new Main().dispatch(new String[]{"run", Server.class.getName()});
    }
}
