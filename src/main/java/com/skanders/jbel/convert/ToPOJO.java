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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.*;
import com.skanders.jbel.def.LogPattern;
import com.skanders.jbel.def.SkandersResult;
import com.skanders.jbel.result.Result;
import com.skanders.jbel.result.Resulted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ToPOJO
{
    private static final Logger LOG = LoggerFactory.getLogger(ToPOJO.class);

    public static <T> Resulted<T> fromNode(
            JsonNode jsonNode, Class<T> className, DeserializationFeature... feature)
    {
        return fromJsonNode(Mapper.forJson(), jsonNode, className, feature);
    }

    public static <T> Resulted<T> fromJson(
            InputStream jsonText, Class<T> className, DeserializationFeature... feature)
    {
        return fromInputStream(Mapper.forJson(), jsonText, className, feature);
    }

    public static <T> Resulted<T> fromJson(
            File jsonFile, Class<T> className, DeserializationFeature... feature)
    {
        return fromFile(Mapper.forJson(), jsonFile, className, feature);
    }

    public static <T> Resulted<T> fromJson(
            String jsonText, Class<T> className, DeserializationFeature... feature)
    {
        return fromString(Mapper.forJson(), jsonText, className, feature);
    }

    public static <T> Resulted<T> fromJson(
            byte[] jsonBytes, Class<T> className, DeserializationFeature... feature)
    {
        return fromBytes(Mapper.forJson(), jsonBytes, className, feature);
    }

    public static <T> Resulted<T> fromXml(
            InputStream xmlText, Class<T> className, DeserializationFeature... feature)
    {
        return fromInputStream(Mapper.forXml(), xmlText, className, feature);
    }

    public static <T> Resulted<T> fromXml(
            File xmlFile, Class<T> className, DeserializationFeature... feature)
    {
        return fromFile(Mapper.forXml(), xmlFile, className, feature);
    }

    public static <T> Resulted<T> fromXml(
            String xmlText, Class<T> className, DeserializationFeature... feature)
    {
        return fromString(Mapper.forXml(), xmlText, className, feature);
    }

    public static <T> Resulted<T> fromXml(
            byte[] xmlBytes, Class<T> className, DeserializationFeature... feature)
    {
        return fromBytes(Mapper.forXml(), xmlBytes, className, feature);
    }

    public static <T> Resulted<T> fromYaml(
            InputStream yamlText, Class<T> className, DeserializationFeature... feature)
    {
        return fromInputStream(Mapper.forYaml(), yamlText, className, feature);
    }

    public static <T> Resulted<T> fromYaml(
            File yamlFile, Class<T> className, DeserializationFeature... feature)
    {
        return fromFile(Mapper.forYaml(), yamlFile, className, feature);
    }

    public static <T> Resulted<T> fromYaml(
            String yamlText, Class<T> className, DeserializationFeature... feature)
    {
        return fromString(Mapper.forYaml(), yamlText, className, feature);
    }

    public static <T> Resulted<T> fromYaml(
            byte[] yamlBytes, Class<T> className, DeserializationFeature... feature)
    {
        return fromBytes(Mapper.forYaml(), yamlBytes, className, feature);
    }

    private static <T> Resulted<T> fromInputStream(
            ObjectMapper mapper, InputStream inputStream, Class<T> className, DeserializationFeature... feature)
    {
        try {
            ObjectReader reader = (feature == null || feature.length == 0) ?
                                  mapper.readerFor(className) :
                                  mapper.readerFor(className).withFeatures(feature);

            return Resulted.inValue(reader.readValue(inputStream));

        } catch (IOException e) {
            LOG.error(LogPattern.ERROR, e.getClass(), e.getMessage());
            return Resulted.inResult(convert(e));

        }
    }

    private static <T> Resulted<T> fromFile(
            ObjectMapper mapper, File fileName, Class<T> className, DeserializationFeature... feature)
    {
        try {
            ObjectReader reader = createReader(mapper, className, feature);

            return Resulted.inValue(reader.readValue(fileName));

        } catch (IOException e) {
            LOG.error(LogPattern.ERROR, e.getClass(), e.getMessage());
            return Resulted.inResult(convert(e));

        }
    }

    private static <T> Resulted<T> fromString(
            ObjectMapper mapper, String text, Class<T> className, DeserializationFeature... feature)
    {
        try {
            ObjectReader reader = createReader(mapper, className, feature);

            return Resulted.inValue(reader.readValue(text));

        } catch (IOException e) {
            LOG.error(LogPattern.ERROR, e.getClass(), e.getMessage());
            return Resulted.inResult(convert(e));

        }
    }

    private static <T> Resulted<T> fromJsonNode(
            ObjectMapper mapper, JsonNode node, Class<T> className, DeserializationFeature... feature)
    {
        try {
            ObjectReader reader = createReader(mapper, className, feature);

            return Resulted.inValue(reader.treeToValue(node, className));

        } catch (IOException e) {
            LOG.error(LogPattern.ERROR, e.getClass(), e.getMessage());
            return Resulted.inResult(convert(e));

        }
    }

    private static <T> Resulted<T> fromBytes(
            ObjectMapper mapper, byte[] bytes, Class<T> className, DeserializationFeature... feature)
    {
        try {
            ObjectReader reader = createReader(mapper, className, feature);

            return Resulted.inValue(reader.readValue(bytes));

        } catch (IOException e) {
            LOG.error(LogPattern.ERROR, e.getClass(), e.getMessage());
            return Resulted.inResult(convert(e));

        }
    }

    private static <T> ObjectReader createReader(
            ObjectMapper mapper, Class<T> className, DeserializationFeature... feature)
    {
        return (feature == null || feature.length == 0) ?
               mapper.readerFor(className) :
               mapper.readerFor(className).withFeatures(feature);
    }

    private static Result convert(Exception e)
    {
        if (e instanceof JsonMappingException) {
            return SkandersResult.JSON_MAPPING_EXCEPT;

        } else if (e instanceof JsonParseException) {
            return SkandersResult.JSON_PARSE_EXCEPT;

        } else {
            return Result.exception(e);

        }
    }
}
