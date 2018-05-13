package net.christophe.genin.domain.monitor.addon.json;

import static org.junit.Assert.*;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class JsonsTest {

    private JsonObject jsonObject;

    @Before
    public void before() {
        jsonObject = new JsonObject().put("att", "a").put("att2", 1);
    }

    @Test
    public void should_builder_create_an_instance() {
        assertNotNull(Jsons.builder(new JsonArray()));
        assertNotNull(Jsons.builder(null));
    }

    @Test
    public void should_builder_create_an_empty_list_when_null() {
        Assertions.assertThat(Jsons.builder(null).toListString()).isEmpty();
    }

    @Test
    public void should_builder_create_an_list() {
        Assertions.assertThat(Jsons.builder(new JsonArray().add("test")).toListString())
                .hasSize(1).contains("test");
        JsonObject obj = new JsonObject().put("essay", "novel");
        List<JsonObject> t = Jsons.builder(new JsonArray().add(obj)).toStream().collect(Collectors.toList());
        Assertions.assertThat(t)
                .hasSize(1).contains(obj);
    }

    @Test
    public void should_convert_string_object() {
        JsonObject result = Jsons.objToJson(jsonObject.encode());
        assertEquals(jsonObject, result);
    }

    @Test
    public void should_convert_json_object() {
        JsonObject result = Jsons.objToJson(jsonObject);
        assertEquals(jsonObject, result);
    }

    @Test
    public void should_convert_map() {
        JsonObject result = Jsons.objToJson(jsonObject.getMap());
        assertEquals(jsonObject, result);
    }
}
