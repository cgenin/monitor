package net.christophe.genin.domain.server.apps;

import io.vertx.core.json.JsonObject;

import java.util.Random;
import java.util.stream.LongStream;

public enum Treatments {
    PROJECTS(0),
    TABLES(1),
    END(2);

    private final Integer state;

    Treatments(Integer state) {
        this.state = state;
    }

    public Integer getState() {
        return state;
    }

    public static long batchTime(JsonObject config){
        final Long batch = config.getLong("batch", 1_000L);
        Random r = new Random();
        final Long noise = r.longs(1_000L, 10_001L).findFirst().getAsLong();
        return noise + batch;
    }
}
