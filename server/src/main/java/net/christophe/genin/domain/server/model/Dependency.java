package net.christophe.genin.domain.server.model;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.server.adapter.Adapters;
import rx.Observable;
import rx.Single;

public abstract class Dependency {

    private final String resource;
    private final String usedBy;

    public static Single<Integer> removeByUsedBy(String usedBY) {
        return Adapters.get().dependencyHandler().removeByUsedBy(usedBY);
    }

    public static Single<Boolean> create(String resource, String usedBY) {
        return Adapters.get().dependencyHandler().create(resource, usedBY);
    }

    public static Single<Integer> removeAll() {
        return Adapters.get().dependencyHandler().removeAll();
    }


    public static Observable<String> findAllResource() {
        return Adapters.get().dependencyHandler().findAllResource();
    }

    public static Observable<String> usedBy(String resource) {
        return Adapters.get().dependencyHandler().usedBy(resource) ;
    }

    public static Observable<Dependency> findAll() {
        return Adapters.get().dependencyHandler().findAll() ;
    }

    public Dependency(String resource, String usedBy) {
        this.resource = resource;
        this.usedBy = usedBy;
    }

    public String resource(){
        return resource;
    }

    public String usedBy(){
        return usedBy;
    }
}
