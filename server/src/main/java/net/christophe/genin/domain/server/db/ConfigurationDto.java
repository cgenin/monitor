package net.christophe.genin.domain.server.db;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.server.json.Jsons;
import org.dizitart.no2.objects.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ConfigurationDto implements Serializable {




    @Id
    private long confId = 0L;


    private List<String> javaFilters = new ArrayList<>();
    private List<String> npmFilters = new ArrayList<>();

    public long getConfId() {
        return confId;
    }

    public void setConfId(long confId) {
        this.confId = confId;
    }

    public List<String> getJavaFilters() {
        return javaFilters;
    }

    public void setJavaFilters(List<String> javaFilters) {
        this.javaFilters = javaFilters;
    }

    public List<String> getNpmFilters() {
        return npmFilters;
    }

    public void setNpmFilters(List<String> npmFilters) {
        this.npmFilters = npmFilters;
    }
}
