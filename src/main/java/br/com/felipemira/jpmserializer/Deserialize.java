package br.com.felipemira.jpmserializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.avro.util.Utf8;

/***
 Classe para converter o Map em um json
 author: Felipe Mira
 version: 1.0
 date: 2024-03-17
 update: 2024-03-21
 */
public class Deserialize {

    /***
     * Construtor vazio
     */
    public Deserialize() {
    }

    /***
     * Converte uma lista de Maps contendo JSONPaths na Chave em Json
     * @param map Lista de Maps
     * @return Objeto JSON
     */
    public String deserializeMapToJson(Map<CharSequence, Object> map) {
        map = this.convertData(map);
        return this.getJsonObject(this.convertNestedStructureToArray(this.buildJsonStructure(map)));
    }

    /***
     * Converte valores em uma lista de Maps que contenha utf8 para String em um JSON
     * @param data Lista de Maps
     * @return Objeto JSON
     */
    private Map<CharSequence, Object> convertData(Map<CharSequence, Object> data) {
        Map<CharSequence, Object> convertedData = new HashMap<>();

        for (Map.Entry<CharSequence, Object> entry : data.entrySet()) {
            CharSequence key = entry.getKey().toString();
            Object value = entry.getValue();
            if (value instanceof Utf8) {
                value = value.toString();
            }
            convertedData.put(key, value);
        }

        return convertedData;
    }

    /***
     * Transforma um objeto JSON em uma String
     * @param jsonObject Objeto JSON
     * @return String
     */
    private String getJsonObject(Object jsonObject) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
        return gson.toJson(jsonObject);
    }

    /***
     * Constroi a estrutura de um JSON a partir de uma lista de Maps
     * @param map Lista de Maps
     * @return Objeto JSON
     */
    private Map<CharSequence, Object> buildJsonStructure(Map<CharSequence, Object> map) {
        Map<CharSequence, Object> jsonObject = new HashMap<>();

        for (Map.Entry<CharSequence, Object> entry : map.entrySet()) {
            String[] parts = ((String)entry.getKey()).split("\\['|']\\['|']");
            Map<CharSequence, Object> currentMap = jsonObject;

            for (int i = 1; i < parts.length; i++) {
                String part = parts[i];

                if (parts[0].matches("^\\$\\[\\d+]$")) {
                    currentMap = jsonObject;
                    var number = parts[0].substring(2, parts[0].length() - 1); // Remove o "$[" inicial e o "]" final
                    String key = "["+ number +"]";
                    Map<CharSequence, Object> list;
                    if (currentMap.containsKey(key)) {
                        // A chave ja existe, entao apenas adicione o valor
                        //noinspection unchecked
                        list = (Map<CharSequence, Object>) currentMap.get(key);
                    } else {
                        // A chave nao existe, entao crie uma nova entrada
                        //noinspection unchecked
                        list = (Map<CharSequence, Object>) currentMap.computeIfAbsent(key, k -> new HashMap<>());
                    }
                    if (i == parts.length - 1) {
                        list.put(part, entry.getValue());
                    } else {
                        //noinspection unchecked
                        currentMap = (Map<CharSequence, Object>) list.computeIfAbsent(part, k -> new HashMap<>());
                    }
                } else {
                    if (i == parts.length - 1) {
                        currentMap.put(part, entry.getValue());
                    } else {
                        //noinspection unchecked
                        currentMap = (Map<CharSequence, Object>) currentMap.computeIfAbsent(part, k -> new HashMap<>());
                    }
                }
            }
        }
        return jsonObject;
    }

    /***
     *  Converte a estrutura aninhada em Array
     * @param obj Objeto JSON
     * @return Objeto JSON
     */
    private Object convertNestedStructureToArray(Object obj) {
        if (obj instanceof Map) {
            //noinspection unchecked
            Map<CharSequence, Object> map = (Map<CharSequence, Object>) obj;
            Map<CharSequence, Object> newMap = new HashMap<>();
            List<Object> list = new ArrayList<>();
            boolean isArray = false;

            for (Map.Entry<CharSequence, Object> entry : map.entrySet()) {
                CharSequence key = entry.getKey();
                Object value = convertNestedStructureToArray(entry.getValue());

                if (((String)key).matches("\\[\\d+]")) {
                    isArray = true;
                    list.add(value);
                } else {
                    newMap.put(key, value);
                }
            }

            if (isArray) {
                return list;
            } else {
                return newMap;
            }
        } else if (obj instanceof List) {
            List<Object> newList = new ArrayList<>();
            //noinspection unchecked
            for (Object item : (List<Object>) obj) {
                newList.add(convertNestedStructureToArray(item));
            }
            return newList;
        } else return obj;
    }
}
