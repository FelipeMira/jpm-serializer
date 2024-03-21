import br.com.felipemira.jpmserializer.Deserialize;
import br.com.felipemira.jpmserializer.Serialize;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SerializeAndDeserializeTest {

    private Deserialize deserialize;
    private Serialize serialize;

    @BeforeEach
    public void setup() {
        deserialize = new Deserialize();
        serialize = new Serialize();
    }

    @Test
    public void shouldConvertJsonStringToListOfMaps() {
        String json = "{\"name\":\"John\", \"age\":30, \"city\":\"New York\"}";
        Map<CharSequence, Object> result = serialize.serializeJsonToMapJPath(json);

        assertEquals(3, result.size());
    }

    @Test
    public void shouldHandleEmptyJsonString() {
        String json = "{}";

        String expectedMessage = "JSON for create list of jsonPath is empty";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> serialize.serializeJsonToMapJPath(json));

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void shouldConvertListOfMapsToJsonString() {
        String json = "{\"name\":\"John\", \"age\":30, \"city\":\"New York\"}";

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonExpected = gson.fromJson(json, JsonElement.class).toString();

        var obj = serialize.serializeJsonToMapJPath(json);
        String result = deserialize.deserializeMapToJson(obj);


        JsonElement jsonExpectedValue = gson.fromJson(jsonExpected, JsonElement.class);
        JsonElement resultValue = gson.fromJson(result, JsonElement.class);

        assertEquals(jsonExpectedValue, resultValue);
    }

    @Test
    public void shouldHandleEmptyListOfMaps() {
        Map<CharSequence, Object> map = new HashMap<>();

        Object result = deserialize.deserializeMapToJson(map);

        assertEquals("{}", result.toString());
    }

    @Test
    public void shouldConvertNestedJsonStringToListOfMaps() {
        String json = "{\"person\":{\"name\":\"John\", \"age\":30, \"city\":\"New York\"}}";
        Map<CharSequence, Object> result = serialize.serializeJsonToMapJPath(json);

        assertEquals(3, result.size());
    }

    @Test
    public void shouldConvertNestedJsonStringWithEmptyArrayToListOfMaps() {
        String json = "{\"person\":{\"name\":\"John\", \"age\":30, \"city\":\"New York\"}, \"phones\":[]}";
        Map<CharSequence, Object> result = serialize.serializeJsonToMapJPath(json);

        assertEquals(4, result.size());
    }

    @Test
    public void shouldConvertNestedJsonStringWithArrayInRootToListOfMaps() {
        String json = "[{\"name\":\"John\", \"age\":30, \"city\":\"New York\"}, {\"name\":\"Jane\", \"age\":25, \"city\":\"Los Angeles\"}]";
        Map<CharSequence, Object> result = serialize.serializeJsonToMapJPath(json);

        assertEquals(6, result.size());
    }

    @Test
    public void testJsonStringtoMapJsonPathMapWithEmptyArray() throws Exception {
        Serialize serialize = new Serialize();
        String json = "{\"key\":[]}";

        Method method = Serialize.class.getDeclaredMethod("serializeJsonToMapJPath", String.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map<CharSequence, Object> result = (Map<CharSequence, Object>) method.invoke(serialize, json);

        CharSequence key = null;
        for (Map.Entry<CharSequence, Object> entry : result.entrySet()) {
            key = entry.getKey();
            if(key.equals("$['key']") ){
                break;
            }
            break;
        }

        assertEquals("$['key']", String.valueOf(key));
    }

    @Test
    public void testConvertNestedStructureToArrayWithList() throws Exception {
        Deserialize deserialize = new Deserialize();
        List<Object> list = new ArrayList<>();
        list.add("value1");
        list.add("value2");

        Method method = Deserialize.class.getDeclaredMethod("convertNestedStructureToArray", Object.class);
        method.setAccessible(true);

        Object result = method.invoke(deserialize, list);

        assertTrue(result instanceof List);
        List<?> resultList = (List<?>) result;
        assertEquals(2, resultList.size());
        assertEquals("value1", resultList.get(0));
        assertEquals("value2", resultList.get(1));
    }

    @Test
    public void testBuildJsonStructure_withKeyMatchingPattern() throws Exception {
        String json = "[\n" +
                "    {\n" +
                "        \"carro\": {\n" +
                "            \"color\": \"red\",\n" +
                "            \"ports\": 4\n" +
                "        }\n" +
                "    }\n" +
                "]";
        var listOfMaps = serialize.serializeJsonToMapJPath(json);
        serialize.printMap(listOfMaps);

        var object = deserialize.deserializeMapToJson(listOfMaps);
        System.out.println(object);
    }

    @Test
    public void shouldConvertJsonArrayToListOfMaps() {
        String json = "[{\"name\":\"John\", \"age\":30, \"city\":\"New York\"}, {\"name\":\"Jane\", \"age\":25, \"city\":\"Los Angeles\"}]";
        Map<CharSequence, Object> result = serialize.serializeJsonToMapJPath(json);

        assertEquals(6, result.size());
    }

    @Test
    public void shouldConvertListOfMapsToNestedJsonString() {
        String json = "{\"person\":{\"name\":\"John\", \"age\":30, \"city\":\"New York\"}}";

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonExpected = gson.fromJson(json, JsonElement.class).toString();

        var obj = serialize.serializeJsonToMapJPath(json);
        String result = deserialize.deserializeMapToJson(obj);

        JsonElement jsonExpectedValue = gson.fromJson(jsonExpected, JsonElement.class);
        JsonElement resultValue = gson.fromJson(result, JsonElement.class);

        assertEquals(jsonExpectedValue, resultValue);
    }

    @Test
    public void testPrintMap() {
        Serialize serialize = new Serialize();
        Map<CharSequence, Object> map = new HashMap<>();
        map.put("key", "value");
        assertDoesNotThrow(() -> serialize.printMap(map));
    }

    @Test
    public void testtoMapJsonPath() {
        String json = "{\"key\":\"value\"}";
        Map<CharSequence, Object> result = serialize.serializeJsonToMapJPath(json);

        CharSequence key = null;
        for (Map.Entry<CharSequence, Object> entry : result.entrySet()) {
            key = entry.getKey();
            if(key.equals("$['key']") ){
                break;
            }
            break;
        }

        assertEquals("$['key']", String.valueOf(key));
    }

    @Test
    public void testmapJsonPathToJsonString() {
        Map<CharSequence, Object> map = new HashMap<>();
        map.put("$['key']", "value");
        String result = deserialize.deserializeMapToJson(map);
        assertEquals("{\n" +
                "  \"key\": \"value\"\n" +
                "}", result);
    }

    @Test
    public void testJsonComplex(){
        String json = """
                {
                    "loja": {
                        "bicicleta": {
                            "cor": "vermelho",
                            "preco": 19.95,
                            "detalhes": []
                        },
                        "livro": [
                            {
                                "autor": "Felipe Mira",
                                "preco": 99.95,
                                "categoria": "reference",
                                "titulo": "Como sou top",
                                "detalhes": {
                                    "editora": "BookCo",
                                    "isbn": "123-4567890123",
                                    "edicao": "1st"
                                }
                            },
                            {
                                "autor": "Felipe Mira",
                                "preco": 12.99,
                                "categoria": "fiction",
                                "titulo": "o dia em que errei",
                                "detalhes": {
                                    "editora": "BookCo",
                                    "isbn": "456-7890123456",
                                    "edicao": "1st"
                                }
                            }
                        ],
                        "disco": [
                            {
                                "artista": "Felipe Mira",
                                "preco": 9.99,
                                "genero": "Pop",
                                "album": "My First Album",
                                "detalhes": {
                                    "gravadora": "MusicCo",
                                    "dataDeLancamento": "2022-01-01",
                                    "musicas": [
                                        {
                                            "titulo": "My First Song",
                                            "duracao": "4:00",
                                            "detalhes": []
                                        },
                                        {
                                            "titulo": "My Second Song",
                                            "duracao": "4:00",
                                            "detalhes": [
                                                {
                                                    "coParticipacao": "Mira Felipe",
                                                    "trecho": "3:25"
                                                },
                                                {
                                                    "coParticipacao": "Mira Felipe",
                                                    "trecho": "2:10"
                                                }
                                            ]
                                        }
                                    ]
                                }
                            },
                            {
                                "artista": "Felipe Mira",
                                "preco": 14.99,
                                "genero": "Rock",
                                "album": "My Second Album",
                                "detalhes": {
                                    "gravadora": "MusicCo",
                                    "dataDeLancamento": "2022-06-01",
                                    "musicas": []
                                }
                            }
                        ]
                    }
                }
                """;

        var listOfMaps = serialize.serializeJsonToMapJPath(json);
        serialize.printMap(listOfMaps);

        var object = deserialize.deserializeMapToJson(listOfMaps);
        System.out.println(object);
    }

    @Test
    public void testJsonArrayInRoot(){
        String json = """
                [
                    {
                      "rua": "Rua das Flores",
                      "numero": 123,
                      "bairro": "Jardim das Rosas",
                      "cidade": "Sao Paulo",
                      "estado": "SP",
                      "cep": "01234-567",
                      "detalhe": {}
                    },
                    {
                      "rua": "Avenida do Sol",
                      "numero": 456,
                      "bairro": "Vila do Sol",
                      "cidade": "Rio de Janeiro",
                      "estado": "RJ",
                      "cep": "98765-432",
                      "detalhe": {}
                    },
                    {
                      "rua": "Travessa da Lua",
                      "numero": 789,
                      "bairro": "Morro da Lua",
                      "cidade": "Salvador",
                      "estado": "BA",
                      "cep": "65432-109",
                      "detalhe": {
                        "complemento": "Casa 1",
                        "pontoDeReferencia": "Perto da praia"
                      }
                    }
                  ]
                """;


        var listOfMaps = serialize.serializeJsonToMapJPath(json);
        serialize.printMap(listOfMaps);

        var object = deserialize.deserializeMapToJson(listOfMaps);
        System.out.println(object);
    }

    @Test
    public void testJsonWithNullValuet(){
        String json = """
                {
                  "carros": [
                    {
                      "cor": "vermelho",
                      "quantidadeDePortas": 4,
                      "modelo": "Sedan",
                      "ano": 2020,
                      "quilometragem": 15000,
                      "consumoMedio": 15.5,
                      "potenciaMotor": 2.0,
                      "possuiArCondicionado": true,
                      "proprietarioAnterior": null
                    },
                    {
                      "cor": "azul",
                      "quantidadeDePortas": 2,
                      "modelo": "Esportivo",
                      "ano": 2021,
                      "quilometragem": 5000,
                      "consumoMedio": 12.3,
                      "potenciaMotor": 3.0,
                      "possuiArCondicionado": true,
                      "proprietarioAnterior": null
                    },
                    {
                      "cor": "preto",
                      "quantidadeDePortas": 4,
                      "modelo": "SUV",
                      "ano": 2019,
                      "quilometragem": 30000,
                      "consumoMedio": 10.0,
                      "potenciaMotor": 2.5,
                      "possuiArCondicionado": true,
                      "proprietarioAnterior": null
                    }
                  ]
                }
                """; // JSON de exemplo


        var listOfMaps = serialize.serializeJsonToMapJPath(json);
        serialize.printMap(listOfMaps);

        var object = deserialize.deserializeMapToJson(listOfMaps);
        System.out.println(object);
    }

    @Test
    public void testJsonEnderecot(){
        String json = """
                  {
                  	"endereco": {
                  		"rua": "Estrada do Jequitiba",
                  		"numero": 4,
                  		"detalhe": [
                  			{
                  				"complemento": "casa 1",
                  				"pontoDeReferencia": "Próximo a padaria"
                  			}
                  		]
                  	}
                  }
                """;

        var listOfMaps = serialize.serializeJsonToMapJPath(json);
        serialize.printMap(listOfMaps);

        var object = deserialize.deserializeMapToJson(listOfMaps);
        System.out.println(object);
    }
}
