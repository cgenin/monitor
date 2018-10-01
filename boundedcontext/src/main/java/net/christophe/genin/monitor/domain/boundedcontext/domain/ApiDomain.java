package net.christophe.genin.monitor.domain.boundedcontext.domain;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.monitor.addon.json.Jsons;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IApi;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Api;
import net.christophe.genin.monitor.domain.boundedcontext.domain.utils.Schemas;
import rx.Observable;
import rx.Single;

import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.christophe.genin.monitor.domain.boundedcontext.domain.utils.Schemas.Apis.*;

public class ApiDomain {
    private static final Logger logger = LoggerFactory.getLogger(ApiDomain.class);

    private final IApi apiDao;


    public ApiDomain(IApi apiDao) {
        this.apiDao = apiDao;
    }

    public Observable<String> removeAll() {
        return apiDao.removeAll().map(nb -> "Api's deleted : " + nb).toObservable();
    }


    public Observable<JsonArray> findAll() {
        return apiDao.findAll()
                .map(api -> new JsonObject()
                        .put(id.name(), api.id())
                        .put(name.name(), api.name())
                        .put(artifactId.name(), api.artifactId())
                        .put(groupId.name(), api.groupId())
                        .put(method.name(), api.method())
                        .put(returns.name(), api.returns())
                        .put(path.name(), api.path())
                        .put(params.name(), api.params())
                        .put(comment.name(), api.comment())
                        .put(since.name(), api.since())
                        .put(className.name(), api.className())
                        .put(latestUpdate.name(), api.latestUpdate()))
                .reduce(new JsonArray(), JsonArray::add);
    }

    public Observable<String> saveByProject(String idProject, JsonObject apis, String artifactId, String version, long update) {
        JsonArray services = apis.getJsonArray(Schemas.Raw.Apis.services.name(), new JsonArray());

        return apiDao.deleteByIdProject(idProject)
                .doOnNext(nb -> {
                    logger.debug("DELETE for '" + artifactId + "' = " + nb);
                })
                .flatMap(nb -> servicesToJson(services))
                .flatMap(methodJson -> {
                    String groupId = apis.getString(Schemas.Raw.Apis.groupId.name(), "");
                    final String method = methodJson.getString("method", "");
                    final String path = methodJson.getString("path", "");
                    String newId = UUID.randomUUID().toString();
                    Api api = apiDao.newInstance(method, path, idProject)
                            .setId(newId)
                            .setArtifactId(artifactId)
                            .setGroupId(groupId)
                            .setSince(version)
                            .setLatestUpdate(update)
                            .setName(methodJson.getString("name"))
                            .setReturns(methodJson.getString("returns"))
                            .setParams(methodJson.getJsonArray("params").encode())
                            .setComment(methodJson.getString("comment"))
                            .setClassName(methodJson.getString("className"));
                    return Single.just(api)
                            .flatMap(Api::create)
                            .map(updateResult ->
                                    "Api '" + path + "' for artifact '" + artifactId + "' updated : " + updateResult
                            ).toObservable();
                });
    }

    static Observable<JsonObject> servicesToJson(JsonArray services) {
        Function<JsonObject, Stream<? extends JsonObject>> convert2Json = obj -> {
            String className = obj.getString(Schemas.Raw.Apis.Services.name.name(), "");
            JsonArray methods = obj.getJsonArray(Schemas.Raw.Apis.Services.methods.name(), new JsonArray());
            return Jsons.builder(methods)
                    .toStream()
                    .map(o -> o.put("className", className));
        };
        return Observable.from(
                Jsons.builder(services).toStream().flatMap(convert2Json).collect(Collectors.toList())
        );
    }
}
