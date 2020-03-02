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

package com.skanders.jbel.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.skanders.jbel.convert.FromNode;
import com.skanders.jbel.convert.Mapper;
import com.skanders.jbel.def.SkandersException;
import com.skanders.jbel.def.Verify;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A manager that extracts required, optional, and encrypted values from a
 * config file in YAML format.
 * <p>
 * When stating a value to 'get' from the yaml, a dot delimited string must be
 * given to state its location.
 * <p>
 * For example:
 * <pre>
 * key:
 *     value:
 *         var1: "one"
 * </pre>
 * To 'get' var one as a string the function call must be {@code
 * getStr("key.value.var1")}
 * <p>
 * Required values are marked by using the getReq...(value) functions. If a
 * required value is not found then an exception will be thrown with a message
 * indicating the missing value.
 * <p>
 * Optional values can be missing and are marked by using the get...(value)
 * functions. Since optional values can be missing a value not found will be
 * returned as null or, if marked using the get...(value, default) functions,
 * will be returned as the default value.
 * <p>
 * Encrypted values can only be strings and must be designated by having its raw
 * value start with the label 'enc='. The value will automatically be decrypted
 * if both, the value starts with the label AND Properties was created using the
 * {@link #fromEncrypted(String, String, String)} or {@link
 * #fromEncrypted(String, String, char[])} functions.
 * <p>
 * This is in a similar manner as JASYPT's encrypted properties file but made to
 * work with YAML files instead of properties files in order to allow more
 * organized config files as well as array, hashmap and pojo extraction. The
 * difference however is in the label: 'enc=' Alone designates a value as
 * encrypted and will cause the value to be given to {@link
 * StandardPBEStringEncryptor} to be decrypted.
 */
public class Config
{
    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    private final JsonNode configTree;

    /**
     * Static builder that reads all yaml values as 'plain' values and will skip
     * checking for encrypted values
     *
     * @param propertiesFileName a string corresponding to the config yaml file
     *                           location
     * @return an instance of Properties
     */
    public static Config fromPlain(@Nonnull String propertiesFileName)
    {
        Verify.notNull(propertiesFileName, "propertiesFileName cannot be null");

        JsonNode properties = loadConfigProps(propertiesFileName);

        return new Config(properties);
    }

    /**
     * Static builder that will check all strings for encrypted values and
     * automatically convert them.
     *
     * @param propertiesFileName a string corresponding to the config yaml file
     *                           location
     * @param algorithm          encryption algorithm to decrypt values
     * @param password           password to decrypt values
     * @return an instance of Properties
     */
    public static Config fromEncrypted(@Nonnull String propertiesFileName, @Nonnull String algorithm, @Nonnull String password)
    {
        Verify.notNull(propertiesFileName, "propertiesFileName cannot be null");
        Verify.notNull(algorithm, "algorithm cannot be null");
        Verify.notNull(password, "password cannot be null");

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        encryptor.setAlgorithm(algorithm);
        encryptor.setIvGenerator(new RandomIvGenerator());

        JsonNode properties = loadConfigProps(propertiesFileName);

        JsonParser.decryptNodes(properties, encryptor);

        return new Config(properties);
    }

    /**
     * Static builder that will check all strings for encrypted values and
     * automatically convert them.
     *
     * @param propertiesFileName a string corresponding to the config yaml file
     *                           location
     * @param algorithm          encryption algorithm to decrypt values
     * @param passwordArr        password to decrypt values in a char array
     * @return an instance of Properties
     */
    public static Config fromEncrypted(@Nonnull String propertiesFileName, @Nonnull String algorithm, @Nonnull char[] passwordArr)
    {
        Verify.notNull(propertiesFileName, "propertiesFileName cannot be null");
        Verify.notNull(algorithm, "algorithm cannot be null");
        Verify.notNull(passwordArr, "passwordArr cannot be null");

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPasswordCharArray(passwordArr);
        encryptor.setAlgorithm(algorithm);
        encryptor.setIvGenerator(new RandomIvGenerator());

        JsonNode properties = loadConfigProps(propertiesFileName);

        JsonParser.decryptNodes(properties, encryptor);

        return new Config(properties);
    }

    /**
     * Private constructor to be called from static builders
     *
     * @param configTree an instance of JsonNode modeled from yaml file
     */
    private Config(JsonNode configTree)
    {
        this.configTree = configTree;
    }

    /**
     * Builds a JsonNode instance from the given config file
     *
     * @param propertiesFileName a string corresponding to the config yaml file
     *                           location
     * @return an instance of JsonNode modeled from yaml file
     */
    private static JsonNode loadConfigProps(String propertiesFileName)
    {
        try {
            return Mapper.forYaml().readValue(new File(propertiesFileName), JsonNode.class);

        } catch (FileNotFoundException e) {
            throw new SkandersException("Could not find Yaml File.");

        } catch (IOException e) {
            throw new SkandersException("Could not load Yaml File.");

        }
    }

    /**
     * Checks if the given path exists and issues a warning as a ignored value
     *
     * @param path dot delimited path to value in yaml
     */
    public void checkIgnored(String path)
    {
        JsonNode value = getNode(path);

        if (value != null)
            LOG.warn("Presence of ignored value: " + path);
    }

    /**
     * Retrieves the optional value as a Boolean
     *
     * @param path dot delimited path to value in yaml
     * @return the value as a Boolean, or null if not found
     */
    public Boolean getBool(String path)
    {
        return FromNode.toBool(configTree, path);
    }

    /**
     * Retrieves the optional value as a Integer
     *
     * @param path dot delimited path to value in yaml
     * @return the value as a Integer, or null if not found
     */
    public Integer getInt(String path)
    {
        return FromNode.toInt(configTree, path);
    }

    /**
     * Retrieves the optional value as a Long
     *
     * @param path dot delimited path to value in yaml
     * @return the value as a Long, or null if not found
     */
    public Long getLong(String path)
    {
        return FromNode.toLong(configTree, path);
    }

    /**
     * Retrieves the optional value as a Double
     *
     * @param path dot delimited path to value in yaml
     * @return the value as a Double, or null if not found
     */
    public Double getDouble(String path)
    {
        return FromNode.toDouble(configTree, path);
    }

    /**
     * Retrieves the optional value as a String
     *
     * @param path dot delimited path to value in yaml
     * @return the value as a String, or null if not found
     */
    public String getStr(String path)
    {
        return FromNode.toStr(configTree, path);
    }

    /**
     * Retrieves the optional value as a Boolean
     *
     * @param path         dot delimited path to value in yaml
     * @param defaultValue value to be returned if path is not found
     * @return the value as a Boolean, or defaultValue if not found
     */
    public Boolean getBool(String path, Boolean defaultValue)
    {
        return FromNode.toBool(configTree, path, defaultValue);
    }

    /**
     * Retrieves the optional value as a Integer
     *
     * @param path         dot delimited path to value in yaml
     * @param defaultValue value to be returned if path is not found
     * @return the value as a Integer, or defaultValue if not found
     */
    public Integer getInt(String path, Integer defaultValue)
    {
        return FromNode.toInt(configTree, path, defaultValue);
    }

    /**
     * Retrieves the optional value as a Long
     *
     * @param path         dot delimited path to value in yaml
     * @param defaultValue value to be returned if path is not found
     * @return the value as a Long, or defaultValue if not found
     */
    public Long getLong(String path, Long defaultValue)
    {
        return FromNode.toLong(configTree, path, defaultValue);
    }

    /**
     * Retrieves the optional value as a Double
     *
     * @param path         dot delimited path to value in yaml
     * @param defaultValue value to be returned if path is not found
     * @return the value as a Double, or defaultValue if not found
     */
    public Double getDouble(String path, Double defaultValue)
    {
        return FromNode.toDouble(configTree, path, defaultValue);
    }

    /**
     * Retrieves the optional value as a String
     *
     * @param path         dot delimited path to value in yaml
     * @param defaultValue value to be returned if path is not found
     * @return the value as a String, or defaultValue if not found
     */
    public String getStr(String path, String defaultValue)
    {
        return FromNode.toStr(configTree, path, defaultValue);
    }

    /**
     * Retrieves the required value as a Boolean
     *
     * @param path dot delimited path to value in yaml
     * @return the value as a Boolean
     * @throws SkandersException if the path is not found
     */
    public Boolean getReqBool(String path)
    {
        return FromNode.toReqBool(configTree, path);
    }

    /**
     * Retrieves the required value as a Integer
     *
     * @param path dot delimited path to value in yaml
     * @return the value as a Integer
     * @throws SkandersException if the path is not found
     */
    public Integer getReqInt(String path)
    {
        return FromNode.toReqInt(configTree, path);
    }

    /**
     * Retrieves the required value as a Long
     *
     * @param path dot delimited path to value in yaml
     * @return the value as a Long
     * @throws SkandersException if the path is not found
     */
    public Long getReqLong(String path)
    {
        return FromNode.toReqLong(configTree, path);
    }

    /**
     * Retrieves the required value as a Double
     *
     * @param path dot delimited path to value in yaml
     * @return the value as a Double
     * @throws SkandersException if the path is not found
     */
    public Double getReqDouble(String path)
    {
        return FromNode.toReqDouble(configTree, path);
    }

    /**
     * Retrieves the required value as a String
     *
     * @param path dot delimited path to value in yaml
     * @return the value as a String
     * @throws SkandersException if the path is not found
     */
    public String getReqStr(String path)
    {
        return FromNode.toReqStr(configTree, path);
    }

    /**
     * Retrieves the optional value as an List
     *
     * @param path      dot delimited path to value in yaml
     * @param classType array class type
     * @param <T>       List type to be determined by storing value
     * @return the value as an List, or null if not found
     */
    public <T> List<T> getList(String path, Class<T> classType)
    {
        return FromNode.toList(configTree, path, classType);
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
    public <T> List<T> getReqList(String path, Class<T> classType)
    {
        return FromNode.toReqList(configTree, path, classType);
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
    public <T, S> Map<T, S> getMap(String path, Class<T> keyClass, Class<S> valueClass)
    {
        return FromNode.toMap(configTree, path, keyClass, valueClass);
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
    public <T, S> Map<T, S> getReqMap(String path, Class<T> keyClass, Class<S> valueClass)
    {
        return FromNode.toReqMap(configTree, path, keyClass, valueClass);
    }

    /**
     * Retrieves the optional value as a POJO
     *
     * @param path      dot delimited path to value in yaml
     * @param pojoClass pojo class
     * @param <T>       pojo class type
     * @return the value as a POJO, or null if not found
     */
    public <T> T getPOJO(String path, Class<T> pojoClass)
    {
        return FromNode.toPojo(configTree, path, pojoClass);
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
    public <T> T getReqPOJO(String path, Class<T> pojoClass)
    {
        return FromNode.toReqPojo(configTree, path, pojoClass);
    }

    /**
     * Retrieves the path as a JsonNode
     *
     * @param path dot delimited path to value in yaml
     * @return an instance of JsonNode, or null if not found
     */
    private JsonNode getNode(String path)
    {
        return FromNode.toNode(configTree, path);
    }
}
