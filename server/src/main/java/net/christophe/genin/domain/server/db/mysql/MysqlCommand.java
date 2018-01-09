package net.christophe.genin.domain.server.db.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.Commands;
import net.christophe.genin.domain.server.db.ConfigurationDto;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import rx.Observable;

import java.util.*;
import java.util.stream.Collectors;

public class MysqlCommand implements Commands {
    private static final Logger logger = LoggerFactory.getLogger(Mysqls.VertxMysql.class);


    @Override
    public Observable<String> projects(JsonObject json, String artifactId) {
        if (artifactId == null) {
            logger.warn("artifactId si null for " + json.encode());
            return Observable.empty();
        }

        Mysqls mysqls = Mysqls.Instance.get();
        return mysqls.select("SELECT document from PROJECTS WHERE NAME=?", new JsonArray().add(artifactId))
                .map(rs -> {
                    if (Objects.isNull(rs) || rs.getResults().isEmpty()) {
                        return new JsonObject().put(Schemas.Projects.name.name(), artifactId)
                                .put(Schemas.Projects.latestUpdate.name(), 0L);
                    }
                    JsonArray firstLine = rs.getResults().get(0);
                    String string = firstLine.getString(0);
                    return new JsonObject(string);
                })
                .flatMap(document -> {
                    final long update = json.getLong(Schemas.Raw.update.name());
                    final Long lUpdate = document.getLong(Schemas.Projects.latestUpdate.name());
                    if (lUpdate < update) {

                        final String version = json.getString(Schemas.Raw.version.name());
                        if (Commands.Projects.isSnapshot(version)) {
                            document.put(Schemas.Projects.snapshot.name(), version);
                        } else {
                            document.put(Schemas.Projects.release.name(), version);
                        }


                        final List<String> tables = Commands.Projects.extractTables(json);

                        final List<String> allDeps = Commands.Projects.extractJavaDeps(json);
                        final ConfigurationDto conf = Optional.ofNullable(Dbs.instance
                                .repository(ConfigurationDto.class)
                                .find().firstOrDefault())
                                .orElseGet(ConfigurationDto::new);

                        List<String> javaFilters = conf.getJavaFilters();
                        final List<String> javaDeps = allDeps.parallelStream()
                                .map(String::toUpperCase)
                                .filter(s ->
                                        javaFilters.isEmpty() ||
                                                javaFilters.parallelStream()
                                                        .map(String::toUpperCase)
                                                        .anyMatch(s::contains)
                                ).collect(Collectors.toList());
                        Optional.ofNullable(json.getString(Schemas.Projects.changelog.name()))
                                .ifPresent(s -> document.put(Schemas.Projects.changelog.name(), s));

                        final List<String> apis = Commands.Projects.extractUrls(json);

                        document.put(Schemas.Projects.apis.name(), apis)
                                .put(Schemas.Projects.tables.name(), tables)
                                .put(Schemas.Projects.javaDeps.name(), javaDeps)
                                .put(Schemas.Projects.name.name(), artifactId)
                                .put(Schemas.Projects.latestUpdate.name(), update);

                        return Observable.just(document);
                    }
                    return Observable.empty();

                })
                // Si un objet alors on doit updater le doc si un id sinon effectuer un insert
                .flatMap(document -> {
                    String id = document.getString(Schemas.Projects.id.name());
                    if (Objects.isNull(id)) {
                        String idProject = UUID.randomUUID().toString();
                        return mysqls.execute("INSERT INTO PROJECTS (ID, NAME, document) " +
                                        "  VALUES (?,?,?)",
                                new JsonArray()
                                        .add(idProject)
                                        .add(artifactId)
                                        .add(document.put(Schemas.Projects.id.name(), idProject).encode())
                        )
                                .toObservable();

                    }

                    return mysqls.execute("UPDATE PROJECTS SET document=? WHERE ID = ?", new JsonArray().add(document.encode()).add(id))
                            .toObservable();
                })
                .map(updateResult -> "Projects '" + artifactId + "' updated : " + updateResult.getUpdated());
    }

    @Override
    public Observable<String> tables(List<String> tables, String artifactId, long update) {


        Mysqls mysqls = Mysqls.Instance.get();
        Observable<String> creationTable = mysqls.select("SELECT NAME FROM TABLES WHERE SERVICE=?", new JsonArray().add(artifactId))
                .flatMap(rs -> {
                    if (Objects.isNull(rs)) {
                        return Observable.from(tables);
                    }
                    Set<String> collect = rs
                            .getResults()
                            .stream()
                            .map(row -> row.getString(0))
                            .collect(Collectors.toSet());
                    HashSet<String> toCreate = new HashSet<>(tables);
                    toCreate.removeAll(collect);
                    return Observable.from(toCreate);
                })
                .flatMap(tableName -> mysqls.execute(
                        "INSERT INTO TABLES (ID, NAME, SERVICE, latestUpdate) VALUES (?,?,?, ?)",
                        new JsonArray().add(UUID.randomUUID().toString()).add(tableName).add(artifactId).add(update))
                        .map(updateResult -> "Table '" + tableName + "' for '" + artifactId + "' creation " + updateResult.getUpdated())
                        .toObservable());
        Observable<String> deletion = mysqls.select("SELECT NAME FROM TABLES WHERE SERVICE=?", new JsonArray().add(artifactId))
                .flatMap(rs -> {
                    if (Objects.isNull(rs)) {
                        return Observable.empty();
                    }
                    Set<String> collect = rs
                            .getResults()
                            .stream()
                            .map(row -> row.getString(0))
                            .collect(Collectors.toSet());
                    collect.removeAll(tables);
                    return Observable.from(collect);
                })
                .flatMap(tableName ->
                        mysqls.execute(
                                "DELETE FROM TALBLES WHERE NAME=? AND SERVICE=?",
                                new JsonArray().add(tableName).add(artifactId))
                                .toObservable()
                                .map(updateResult -> "Table '" + tableName + "' for '" + artifactId + "'  deleted :" + updateResult.getUpdated())
                );
        return Observable.concat(deletion, creationTable);
    }

    @Override
    public boolean versions(JsonObject json, String artifactId) {
        return false;
    }
}
