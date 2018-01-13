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
import rx.functions.Func1;

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
                                "DELETE FROM TABLES WHERE NAME=? AND SERVICE=?",
                                new JsonArray().add(tableName).add(artifactId))
                                .toObservable()
                                .map(updateResult -> "Table '" + tableName + "' for '" + artifactId + "'  deleted :" + updateResult.getUpdated())
                );
        return Observable.concat(deletion, creationTable);
    }

    @Override
    public Observable<String> versions(JsonObject json, String artifactId, String version) {

        Mysqls mysqls = Mysqls.Instance.get();
        return getIdProject(artifactId, mysqls)
                .flatMap(idProjects -> mysqls.select("SELECT document from VERSIONS WHERE NAME=? AND IDPROJECT = ?", new JsonArray().add(version).add(idProjects))
                        .map(rs2 -> {
                            if (Objects.isNull(rs2) || rs2.getResults().isEmpty()) {
                                return new JsonObject()
                                        .put(Schemas.Version.idproject.name(), idProjects)
                                        .put(Schemas.Version.name.name(), version)
                                        .put(Schemas.Version.latestUpdate.name(), 0L);
                            }
                            JsonArray firstRow = rs2.getResults().get(0);
                            String string = firstRow.getString(0);
                            return new JsonObject(string);
                        })
                        .flatMap(currentDoc -> {
                            long lDate = currentDoc.getLong(Schemas.Version.latestUpdate.name());
                            long update = json.getLong(Schemas.Raw.update.name());
                            if (lDate < update) {
                                boolean snapshot = Projects.isSnapshot(version);
                                List<String> javaDeps = Projects.extractJavaDeps(json);
                                List<String> tables = Projects.extractTables(json);
                                List<String> urls = Projects.extractUrls(json);
                                Optional.ofNullable(json.getString(Schemas.Projects.changelog.name()))
                                        .ifPresent(s -> currentDoc.put(Schemas.Projects.changelog.name(), s));
                                return Observable.just(currentDoc
                                        .put(Schemas.Version.isSnapshot.name(), snapshot)
                                        .put(Schemas.Version.javaDeps.name(), javaDeps)
                                        .put(Schemas.Version.tables.name(), tables)
                                        .put(Schemas.Version.apis.name(), urls));
                            }
                            return Observable.empty();
                        })
                        .flatMap(document -> {
                            String id = document.getString(Schemas.Version.id.name());
                            if (Objects.isNull(id)) {
                                String newId = UUID.randomUUID().toString();

                                return mysqls.execute(
                                        "Insert INTO VERSIONS (ID, IDPROJECT, NAME, document) VALUES (?,?,?,?)",
                                        new JsonArray()
                                                .add(newId)
                                                .add(idProjects)
                                                .add(version)
                                                .add(document.put(Schemas.Version.id.name(), newId).encode()))
                                        .toObservable();
                            }
                            return mysqls.execute("UPDATE VERSIONS SET document=? WHERE ID=?",
                                    new JsonArray()
                                            .add(document.encode())
                                            .add(id))
                                    .toObservable();
                        }).map(updateResult -> "Version '" + version +
                                "' for artifact '" + artifactId + "' updated : " +
                                updateResult.getUpdated()));
    }

    private Observable<String> getIdProject(String artifactId, Mysqls mysqls) {
        return mysqls.select("SELECT ID from PROJECTS WHERE NAME=?", new JsonArray().add(artifactId))
                .map(resultSet -> {
                    if (Objects.isNull(resultSet) || resultSet.getResults().isEmpty()) {
                        throw new IllegalStateException("No Projects found for " + artifactId);
                    }

                    JsonArray row = resultSet.getResults().get(0);
                    return row.getString(0);
                });
    }

    @Override
    public Observable<String> apis(JsonObject apis, String artifactId, String version, long update, JsonArray services) {
        Mysqls mysqls = Mysqls.Instance.get();
        return getIdProject(artifactId, mysqls)
                .flatMap(idProject -> mysqls.execute(
                        "DELETE from APIS WHERE IDPROJECT=?",
                        new JsonArray().add(idProject)
                ).toObservable()
                        .flatMap(u -> {
                                    logger.debug("DELETE for '" + artifactId + "' = " + u.getUpdated());
                                    return servicesToJson(services)
                                            .flatMap(insertInApis(apis, artifactId, version, update, idProject));
                                }

                        ));
    }

    private Func1<JsonObject, Observable<? extends String>> insertInApis(JsonObject apis, String artifactId, String version, long update, String idProject) {
        return methodJson -> {
            String groupId = apis.getString(Schemas.Raw.Apis.groupId.name(), "");
            final String method = methodJson.getString("method", "");
            final String path = methodJson.getString("path", "");
            String newId = UUID.randomUUID().toString();
            JsonObject document = new JsonObject()
                    .put(Schemas.Projects.id.name(), newId)
                    .put(Schemas.Apis.method.name(), method)
                    .put(Schemas.Apis.path.name(), path)
                    .put(Schemas.Apis.artifactId.name(), artifactId)
                    .put(Schemas.Apis.groupId.name(), groupId)
                    .put(Schemas.Apis.since.name(), version)
                    .put(Schemas.Apis.latestUpdate.name(), update)
                    .put(Schemas.Apis.name.name(), methodJson.getString("name"))
                    .put(Schemas.Apis.returns.name(), methodJson.getString("returns"))
                    .put(Schemas.Apis.params.name(), methodJson.getJsonArray("params").encode())
                    .put(Schemas.Apis.comment.name(), methodJson.getString("comment"))
                    .put(Schemas.Apis.className.name(), methodJson.getString("className"));
            return Mysqls.Instance.get().execute(
                    "Insert INTO APIS (ID, IDPROJECT, METHOD, FULLURL, document) VALUES (?,?,?,?, ?)",
                    new JsonArray()
                            .add(newId)
                            .add(idProject)
                            .add(method)
                            .add(path)
                            .add(document.encode()))
                    .map(updateResult ->
                            "Api '" + path + "' for artifact '" + artifactId + "' updated : " + updateResult.getUpdated()
                    )
                    .toObservable();
        };
    }
}
