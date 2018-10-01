package net.christophe.genin.monitor.domain.boundedcontext.domain.model;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.boundedcontext.domain.Treatments;
import rx.Single;

/**
 * Model representing an Raw events of an service domain.
 */
public interface Raw {


    JsonObject json();

    Treatments state();

    String artifactId();

    Long update();

    Long id();

    Single<Boolean> updateState(Treatments treatments);



}
