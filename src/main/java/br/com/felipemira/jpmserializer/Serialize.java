package br.com.felipemira.jpmserializer;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import net.minidev.json.JSONArray;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedHashMap;

/***
 * Classe para converter JSON em um Map de caminhos e valores
 author: Felipe Mira
 version: 1.0
 date: 2024-03-17
 update: 2024-03-21
 */
public class Serialize {

    /***
     * Construtor vazio
     */
    public Serialize() {
    }

    /***
     * Converte um JSON em uma lista de Maps
     * @param json JSON
     * @return Lista de Maps
     */
    public Map<CharSequence, Object> serializeJsonToMapJPath(String json) {
        return this.jsonStringToMap(json);
    }

    /***
     * Converte um JSON em uma lista de Maps
     * @param json JSON
     * @return Lista de Maps
     */
    private Map<CharSequence, Object> jsonStringToMap(String json){
        Configuration conf = Configuration.builder().options(Option.AS_PATH_LIST).build();
        List<String> paths;
        if (json == null || json.trim().isEmpty() || json.equals("{}")) {
            throw new IllegalArgumentException("JSON for create list of jsonPath is empty");
        } else {
            paths = JsonPath.using(conf).parse(json).read("$..*");
        }

        Map<CharSequence, Object> map = new HashMap<>();

        for (String path : paths) {
            if (!path.endsWith("[*]")) {
                Object value = JsonPath.read(json, path);
                if(!(value instanceof LinkedHashMap<?,?>) && !(value instanceof JSONArray)) {
                    map.put(path, value);
                }

                if((value instanceof JSONArray) && ((JSONArray) value).isEmpty()){
                    map.put(path, new JSONArray());
                }
                if(value instanceof LinkedHashMap<?,?>){
                    if(((LinkedHashMap<?, ?>) value).isEmpty()){
                        map.put(path, new LinkedHashMap<>());
                    }
                }
            }
        }
        return map;
    }

    /***
     * Imprime a lista de Maps
     * @param map Lista de Maps
     */
    public void printMap(Map<CharSequence, Object> map) {
        for (Map.Entry<CharSequence, Object> entry : map.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }
}
