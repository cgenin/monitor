package net.christophe.genin.monitor.domain.boundedcontext.domain;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.monitor.addon.json.Jsons;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IFrontApps;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.FrontApps;
import net.christophe.genin.monitor.domain.boundedcontext.domain.utils.Raws;
import rx.Observable;
import rx.Single;
import rx.functions.Action2;
import rx.functions.Func1;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FrontsDomain {
    private final IFrontApps frontsDao;

    public FrontsDomain(IFrontApps frontsDao) {
        this.frontsDao = frontsDao;
    }

    public Observable<JsonArray> findAll() {
        return frontsDao.findAll()
                .map(fa -> new JsonObject()
                        .put("id", fa.id())
                        .put("groupId", fa.groupId())
                        .put("artifactId", fa.artifactId())
                        .put("version", fa.version())
                        .put("lastUpdate", fa.lastUpdate())
                        .put("packagesJson", fa.packagesJson()))

                .reduce(new JsonArray(), JsonArray::add);
    }

    public Observable<JsonObject> findByGroup(List<String> npmFilters) {
        return frontsDao.findAll()
                .groupBy(fa -> fa.groupId() + "." + fa.artifactId())
                .flatMap(resumeJson(npmFilters));
    }

    public Observable<JsonArray> getServicesList(List<String> npmFilters) {
        return frontsDao.findAll()
                .map(FrontApps::packagesJson)
                .map(pj -> pj.getJsonObject("dependencies", new JsonObject()))
                .map(getDomains(npmFilters));
    }

    public Observable<JsonArray> findBy(String domain, Observable<List<String>> npmFiltersObs) {
        Objects.requireNonNull(npmFiltersObs);
        return npmFiltersObs.flatMap(npmFilters ->
                frontsDao.findAll()
                        .groupBy(fa -> fa.groupId() + "." + fa.artifactId())
                        .flatMap(groups -> groups.collect(JsonObject::new, reduceByDomain(domain)))
        )
                .filter(obj -> !obj.getJsonArray("services", new JsonArray()).isEmpty())
                .reduce(new JsonArray(), JsonArray::add);
    }

    public Single<Boolean> save(String groupId, String artifactId, String version, JsonObject packagesJson) {
        return frontsDao.findBy(groupId, artifactId, version)
                .observeOn(Schedulers.io())
                .map(fa -> fa.setPackageJson(packagesJson).setLastUpdate(new Date().getTime()))
                .switchIfEmpty(Observable.fromCallable(() -> frontsDao.newInstance(packagesJson, groupId, artifactId, version)))
                .toSingle()
                .flatMap(FrontApps::save);
    }

    private Func1<GroupedObservable<String, FrontApps>, Observable<JsonObject>> resumeJson(List<String> npmFilters) {
        return groups -> groups.collect(
                () -> new JsonObject()
                        .put("lastUpdateSnapshot", 0L)
                        .put("lastUpdateRelease", 0L)
                        .put("packageJson", new JsonObject())
                        .put("lastUpdate", 0L)
                ,
                (acc, fa) -> {
                    acc.put("groupId", fa.groupId())
                            .put("artifactId", fa.artifactId());
                    if (Raws.isSnapshot(fa.version())) {
                        if (acc.getLong("lastUpdateSnapshot") < fa.lastUpdate()) {
                            acc.put("snapshotVersion", fa.version())
                                    .put("lastUpdateSnapshot", fa.lastUpdate());
                        }
                    } else if (acc.getLong("lastUpdateRelease") < fa.lastUpdate()) {
                        acc.put("releaseVersion", fa.version())
                                .put("lastUpdateRelease", fa.lastUpdate());
                    }

                    if (acc.getLong("lastUpdate") < fa.lastUpdate()) {
                        JsonObject packagesJson = fa.packagesJson();
                        acc.put("packageJson", packagesJson)
                                .put("lastUpdate", fa.lastUpdate());
                        acc.put("servicesClient", servicesClient(packagesJson, npmFilters));
                    }
                }
        );
    }

    private JsonArray servicesClient(JsonObject packagesJson, List<String> npmFilters) {
        List<JsonObject> collect = serviceClientList(packagesJson, npmFilters);
        return new JsonArray(collect);
    }

    private List<JsonObject> serviceClientList(JsonObject packagesJson, List<String> npmFilters) {
        HashSet<String> npms = new HashSet<>(npmFilters);
        JsonObject dependencies = packagesJson.getJsonObject("dependencies", new JsonObject()).copy();
        return dependencies.fieldNames().stream()
                .filter(key -> npms.stream().anyMatch(key::contains))
                .distinct()
                .map(key -> new JsonObject().put("key", key).put("value", dependencies.getString(key)))
                .collect(Collectors.toList());
    }

    private Func1<JsonObject, JsonArray> getDomains(List<String> npmFilters) {
        return dependencies -> {
            Stream<String> libraries = dependencies.fieldNames().stream()
                    .filter(key -> npmFilters.stream().anyMatch(key::contains));
            return libraries.map(key -> npmFilters.stream().filter(key::contains)
                    .reduce(key, (tmp, k) -> tmp.replaceAll(k, "")))
                    .distinct()
                    .collect(Jsons.Collectors.toJsonArray());
        };
    }

    private Action2<JsonObject, FrontApps> reduceByDomain(String domain) {
        return (acc, fa) -> {
            acc.put("groupId", fa.groupId());
            acc.put("artifactId", fa.artifactId());
            Long lastUpdate = acc.getLong("lastUpdate", 0L);
            JsonObject dependencies = fa.packagesJson()
                    .getJsonObject("dependencies", new JsonObject());
            List<String> servicesNames = dependencies
                    .fieldNames()
                    .stream()
                    .filter(key -> key.contains(domain))
                    .collect(Collectors.toList());
            if (lastUpdate < fa.lastUpdate()) {
                acc.put("lastUpdate", fa.lastUpdate());
                if (servicesNames.isEmpty()) {
                    String deletedIn = acc.getString("deletedIn");
                    if (Objects.isNull(deletedIn) || deletedIn.compareTo(fa.version()) > 0) {
                        acc.put("deletedIn", fa.version());
                    }
                } else {
                    List<JsonObject> list = servicesNames.parallelStream()
                            .map(key -> new JsonObject().put("name", key).put("version", dependencies.getString(key)))
                            .collect(Collectors.toList());
                    acc.put("services", new JsonArray(list));
                    String since = acc.getString("since");
                    if (Objects.isNull(since) || since.compareTo(fa.version()) < 0) {
                        acc.put("since", fa.version());
                    }
                }
            } else {
                String since = acc.getString("since");
                if (!servicesNames.isEmpty() && !Objects.isNull(since)

                        && since.compareTo(fa.version()) > 0) {
                    acc.put("since", fa.version());
                }
            }

        };
    }


}
