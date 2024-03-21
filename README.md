# Json Path Map Serializer

Este projeto é um facilitador focado na serialização e deserialização de dados JSON, permitindo a conversão de  um 
JSON em um Map(String, Object) onde na chave tenho o JsonPath do respectivo Object
(serializando o mesmo) e a reconstrução dessas chaves e valores para o objeto JSON (deserialização).

## Como funciona

O projeto contém duas classes principais: `Serialize` e `Deserialize`.

A classe `Serialize` contém o método `serializeJsonToMapJPath`, que recebe uma string JSON e a converte em um mapa de dados.

A classe `Deserialize` contém o método `deserializeMapToJson`, que faz o inverso: recebe um mapa de dados e o converte de volta para uma string JSON.

## Exemplos práticos

Aqui está um exemplo de como você pode usar essas classes para serializar e deserializar dados:

```java
Serialize serialize = new Serialize();
Deserialize deserialize = new Deserialize();

// Exemplo de JSON
String json = "{\"name\":\"John\", \"age\":30, \"city\":\"New York\"}";

// Serialização
Map<String, Object> dataMap = serialize.serializeJsonToMapJPath(json);
serialize.printMap(dataMap);

// Deserialização
String jsonResult = deserialize.deserializeMapToJson(dataMap);
System.out.println(jsonResult);
```

No exemplo acima, a string JSON é primeiro convertida em um mapa de dados usando o método `serializeJsonToMapJPath`. Em seguida, o mapa de dados é convertido de volta para uma string JSON usando o método `deserializeMapToJson`.

## Testes

O projeto também inclui testes unitários para verificar a correta funcionalidade da serialização e deserialização. Os testes estão localizados no arquivo `ConvertTest.java`.

Para executar os testes, você pode usar o comando `gradle test` no terminal.

## Codificação

Este projeto usa a codificação UTF-8. Certifique-se de que seu ambiente de desenvolvimento esteja configurado para usar a mesma codificação para evitar problemas com caracteres especiais.

## Construção e execução

Para construir e executar o projeto, você pode usar os comandos Gradle `gradle build` e `gradle run`, respectivamente.