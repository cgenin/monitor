package net.christophe.genin.domain.monitor.addon.json;

import static org.junit.Assert.*;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Before;
import org.junit.Test;

public class JsonsTest {

    private JsonObject jsonObject;

    @Before
    public void before(){
        jsonObject = new JsonObject().put("att", "a").put("att2", 1);
    }

    @Test(expected = NullPointerException.class)
    public void should_builder_create_an_instance(){
        assertNotNull(Jsons.builder(new JsonArray()));
        Jsons.builder(null);
    }

    @Test
    public void should_convert_string_object(){
        JsonObject result = Jsons.objToJson(jsonObject.encode());
        assertEquals(jsonObject, result);
    }

    @Test
    public void should_convert_json_object(){
        JsonObject result = Jsons.objToJson(jsonObject);
        assertEquals(jsonObject, result);
    }

    @Test
    public void should_convert_map(){
        JsonObject result = Jsons.objToJson(jsonObject.getMap());
        assertEquals(jsonObject, result);
    }
}
