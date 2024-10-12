package de.yggdrasil128.factorial;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class JacksonTests {

    @Test
    void serializeObjectArray() throws JsonProcessingException {
        TypeWithObjectArray obj = new TypeWithObjectArray();
        obj.setName("Foo");
        obj.setVars(new String[]{"Lorem", "ipsum"});
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(obj);
        TypeWithObjectArray copy = mapper.readValue(json, TypeWithObjectArray.class);
        System.out.println(copy.getName());
        System.out.println(Arrays.toString(copy.getVars()));
    }

    static class TypeWithObjectArray {

        private String name;
        private Object[] vars;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object[] getVars() {
            return vars;
        }

        public void setVars(Object[] vars) {
            this.vars = vars;
        }

    }

}
