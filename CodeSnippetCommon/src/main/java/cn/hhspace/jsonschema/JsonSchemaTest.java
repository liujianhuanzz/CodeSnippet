package cn.hhspace.jsonschema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/1/18 7:43 下午
 * @Package: cn.hhspace.jsonschema
 */
public class JsonSchemaTest {
    public static void main(String[] args) throws JsonProcessingException, InstantiationException, IllegalAccessException {

        Injector injector = Guice.createInjector(new JacksonModule());
        ObjectMapper mapper = new ObjectMapper();

        //
        TestAnotherPerson anotherPerson = injector.getInstance(TestAnotherPerson.class);
        System.out.println(mapper.writeValueAsString(anotherPerson));

        Class<TestPersion> testPersionClass = TestPersion.class;
        TestPersion testPersion = testPersionClass.newInstance();

        mapper.registerSubtypes(new NamedType(testPersionClass, "TEST"));
        JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(mapper);
        JsonNode jsonNode = schemaGenerator.generateJsonSchema(testPersion.getClass());
        String s = mapper.writeValueAsString(jsonNode);
        System.out.println(s);
    }
}
