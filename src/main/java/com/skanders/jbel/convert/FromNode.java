/*
 * Copyright (c) 2020 Alexander Iskander
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skanders.jbel.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.skanders.jbel.def.SkandersException;
import com.skanders.jbel.result.Resulted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class FromNode
{
    private static final Logger LOG = LoggerFactory.getLogger(FromNode.class);

    private static final String PATH_DELIM = "\\.";

    /**
     * Retrieves the optional value as a Boolean
     *
     * @return the value as a Boolean, or null if not found
     */
    public static Boolean toBool(JsonNode node)
    {
        return node.asBoolean();
    }

    /**
     * Retrieves the optional value as a Integer
     *
     * @return the value as a Integer, or null if not found
     */
    public static Integer toInt(JsonNode node)
    {
        return node.asInt();
    }

    /**
     * Retrieves the optional value as a Long
     *
     * @return the value as a Long, or null if not found
     */
    public static Long toLong(JsonNode node)
    {
        return node.asLong();
    }

    /**
     * Retrieves the optional value as a Double
     *
     * @return the value as a Double, or null if not found
     */
    public static Double toDouble(JsonNode node)
    {
        return node.asDouble();
    }

    /**
     * Retrieves the optional value as a String
     *
     * @return the value as a String, or null if not found
     */
    public static String toStr(JsonNode node)
    {
        return node.asText();
    }

    /**
     * Retrieves the optional value as a Boolean
     *
     * @param path dot delimited path to value in yaml
     * @return the value as a Boolean, or null if not found
     */
    public static Boolean toBool(JsonNode node, String path)
    {
        JsonNode value = toNode(node, path);

        return value == null ? null : toBool(value);
    }

    /**
     * Retrieves the optional value as a Integer
     *
     * @param path dot delimited path to value in yaml
     * @return the value as a Integer, or null if not found
     */
    public static Integer toInt(JsonNode node, String path)
    {
        JsonNode value = toNode(node, path);

        return value == null ? null : toInt(value);
    }

    /**
     * Retrieves the optional value as a Long
     *
     * @param path dot delimited path to value in yaml
     * @return the value as a Long, or null if not found
     */
    public static Long toLong(JsonNode node, String path)
    {
        JsonNode value = toNode(node, path);

        return value == null ? null : toLong(value);
    }

    /**
     * Retrieves the optional value as a Double
     *
     * @param path dot delimited path to value in yaml
     * @return the value as a Double, or null if not found
     */
    public static Double toDouble(JsonNode node, String path)
    {
        JsonNode value = toNode(node, path);

        return value == null ? null : toDouble(value);
    }

    /**
     * Retrieves the optional value as a String
     *
     * @param path dot delimited path to value in yaml
     * @return the value as a String, or null if not found
     */
    public static String toStr(JsonNode node, String path)
    {
        JsonNode value = toNode(node, path);

        return value == null ? null : toStr(value);
    }

    /**
     * Retrieves the optional value as a Boolean
     *
     * @param path         dot delimited path to value in yaml
     * @param defaultValue value to be returned if path is not found
     * @return the value as a Boolean, or defaultValue if not found
     */
    public static Boolean toBool(JsonNode node, String path, Boolean defaultValue)
    {
        Boolean value = toBool(node, path);

        return value == null ? defaultValue : value;
    }

    /**
     * Retrieves the optional value as a Integer
     *
     * @param path         dot delimited path to value in yaml
     * @param defaultValue value to be returned if path is not found
     * @return the value as a Integer, or defaultValue if not found
     */
    public static Integer toInt(JsonNode node, String path, Integer defaultValue)
    {
        Integer value = toInt(node, path);

        return value == null ? defaultValue : value;
    }

    /**
     * Retrieves the optional value as a Long
     *
     * @param path         dot delimited path to value in yaml
     * @param defaultValue value to be returned if path is not found
     * @return the value as a Long, or defaultValue if not found
     */
    public static Long toLong(JsonNode node, String path, Long defaultValue)
    {
        Long value = toLong(node, path);

        return value == null ? defaultValue : value;
    }

    /**
     * Retrieves the optional value as a Double
     *
     * @param path         dot delimited path to value in yaml
     * @param defaultValue value to be returned if path is not found
     * @return the value as a Double, or defaultValue if not found
     */
    public static Double toDouble(JsonNode node, String path, Double defaultValue)
    {
        Double value = toDouble(node, path);

        return value == null ? defaultValue : value;
    }

    /**
     * Retrieves the optional value as a String
     *
     * @param path         dot delimited path to value in yaml
     * @param defaultValue value to be returned if path is not found
     * @return the value as a String, or defaultValue if not found
     */
    public static String toStr(JsonNode node, String path, String defaultValue)
    {
        String value = toStr(node, path);

        return value == null ? defaultValue : value;
    }

    /**
     * Retrieves the required value as a Boolean
     *
     * @param path dot delimited path to value in yaml
     * @return the value as a Boolean
     * @throws SkandersException if the path is not found
     */
    public static Boolean toReqBool(JsonNode node, String path)
    {
        Boolean value = toBool(node, path);

        requiredValue(path, value);

        return value;
    }

    /**
     * Retrieves the required value as a Integer
     *
     * @param path dot delimited path to value in yaml
     * @return the value as a Integer
     * @throws SkandersException if the path is not found
     */
    public static Integer toReqInt(JsonNode node, String path)
    {
        Integer value = toInt(node, path);

        requiredValue(path, value);

        return value;
    }

    /**
     * Retrieves the required value as a Long
     *
     * @param path dot delimited path to value in yaml
     * @return the value as a Long
     * @throws SkandersException if the path is not found
     */
    public static Long toReqLong(JsonNode node, String path)
    {
        Long value = toLong(node, path);

        requiredValue(path, value);

        return value;
    }

    /**
     * Retrieves the required value as a Double
     *
     * @param path dot delimited path to value in yaml
     * @return the value as a Double
     * @throws SkandersException if the path is not found
     */
    public static Double toReqDouble(JsonNode node, String path)
    {
        Double value = toDouble(node, path);

        requiredValue(path, value);

        return value;
    }

    /**
     * Retrieves the required value as a String
     *
     * @param path dot delimited path to value in yaml
     * @return the value as a String
     * @throws SkandersException if the path is not found
     */
    public static String toReqStr(JsonNode node, String path)
    {
        String value = toStr(node, path);

        requiredValue(path, value);

        return value;
    }

    /**
     * Retrieves the optional value as an List
     *
     * @param classType array class type
     * @param <T>       List type to be determined by storing value
     * @return the value as an List, or null if not found
     */
    public static <T> List<T> toList(JsonNode node, Class<T> classType)
    {
        CollectionType type = Mapper.forJson().getTypeFactory().constructCollectionType(List.class, classType);

        return Mapper.forJson().convertValue(node, type);
    }

    /**
     * Retrieves the optional value as an List
     *
     * @param path      dot delimited path to value in yaml
     * @param classType array class type
     * @param <T>       List type to be determined by storing value
     * @return the value as an List, or null if not found
     */
    public static <T> List<T> toList(JsonNode node, String path, Class<T> classType)
    {
        JsonNode value = toNode(node, path);

        return value == null ? null : toList(value, classType);
    }

    /**
     * Retrieves the required value as an List
     *
     * @param path      dot delimited path to value in yaml
     * @param classType array class type
     * @param <T>       List type to be determined by storing value
     * @return the value as an List
     * @throws SkandersException if the path is not found
     */
    public static <T> List<T> toReqList(JsonNode node, String path, Class<T> classType)
    {
        List<T> value = toList(node, path, classType);

        requiredValue(path, value);

        return value;
    }

    /**
     * Retrieves the optional value as a Map
     *
     * @param keyClass   key class type
     * @param valueClass value class type
     * @param <T>        Map key type to be determined by storing value
     * @param <S>        Map value type to be determined by storing value
     * @return the value as a Map, or null if not found
     */
    public static <T, S> Map<T, S> toMap(JsonNode node, Class<T> keyClass, Class<S> valueClass)
    {
        MapType type = Mapper.forJson().getTypeFactory().constructMapType(Map.class, keyClass, valueClass);

        return Mapper.forJson().convertValue(node, type);
    }

    /**
     * Retrieves the optional value as a Map
     *
     * @param path       dot delimited path to value in yaml
     * @param keyClass   key class type
     * @param valueClass value class type
     * @param <T>        Map key type to be determined by storing value
     * @param <S>        Map value type to be determined by storing value
     * @return the value as a Map, or null if not found
     */
    public static <T, S> Map<T, S> toMap(JsonNode node, String path, Class<T> keyClass, Class<S> valueClass)
    {
        JsonNode value = toNode(node, path);

        return value == null ? null : toMap(value, keyClass, valueClass);
    }

    /**
     * Retrieves the required value as a Map
     *
     * @param path       dot delimited path to value in yaml
     * @param keyClass   key class type
     * @param valueClass value class type
     * @param <T>        Map key type to be determined by storing value
     * @param <S>        Map value type to be determined by storing value
     * @return the value as a Map
     * @throws SkandersException if the path is not found
     */
    public static <T, S> Map<T, S> toReqMap(JsonNode node, String path, Class<T> keyClass, Class<S> valueClass)
    {
        Map<T, S> value = toMap(node, path, keyClass, valueClass);

        requiredValue(path, value);

        return value;
    }

    /**
     * Retrieves the optional value as a POJO
     *
     * @param pojoClass pojo class
     * @param <T>       pojo class type
     * @return the value as a POJO, or null if not found
     */
    public static <T> T toPojo(JsonNode node, Class<T> pojoClass)
    {
        Resulted<T> pojo = ToPOJO.fromNode(node, pojoClass);

        if (pojo.notValid())
            throw new SkandersException("Failed to map pojo " + pojo.result().message());

        return pojo.value();
    }

    /**
     * Retrieves the optional value as a POJO
     *
     * @param path      dot delimited path to value in yaml
     * @param pojoClass pojo class
     * @param <T>       pojo class type
     * @return the value as a POJO, or null if not found
     */
    public static <T> T toPojo(JsonNode node, String path, Class<T> pojoClass)
    {
        JsonNode value = toNode(node, path);

        return value == null ? null : toPojo(value, pojoClass);
    }

    /**
     * Retrieves the required value as a POJO
     *
     * @param path      dot delimited path to value in yaml
     * @param pojoClass pojo class
     * @param <T>       pojo class type
     * @return the value as a POJO
     * @throws SkandersException if the path is not found
     */
    public static <T> T toReqPojo(JsonNode node, String path, Class<T> pojoClass)
    {
        T value = toPojo(node, path, pojoClass);

        requiredValue(path, value);

        return value;
    }

    /**
     * Retrieves the path as a JsonNode
     *
     * @param path dot delimited path to value in yaml
     * @return an instance of JsonNode, or null if not found
     */
    public static JsonNode toNode(JsonNode node, String path)
    {
        JsonNode value = node;

        for (String key : path.split(PATH_DELIM)) {
            value = value.get(key);

            if (value == null)
                return null;
        }

        return value;
    }

    /**
     * Checks if a value is missing, if it is a Error is thrown with a message
     * stating which path was not found.
     *
     * @param path dot delimited path to value in yaml
     * @param ob   object to check
     * @throws SkandersException if the object is null
     */
    private static void requiredValue(String path, Object ob)
    {
        if (ob == null)
            throw new SkandersException("Required path: '" + path + "' not found.");
    }
}
