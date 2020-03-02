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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class Mapper
{
    private static final ObjectMapper JSON_MAPPER;
    private static final ObjectMapper YAML_MAPPER;
    private static final ObjectMapper XML_MAPPER;

    static {
        JSON_MAPPER = new ObjectMapper();
        YAML_MAPPER = new ObjectMapper(new YAMLFactory());
        XML_MAPPER  = new ObjectMapper(new XmlFactory());
    }

    public static ObjectMapper forJson()
    {
        return JSON_MAPPER;
    }

    public static ObjectMapper forYaml()
    {
        return YAML_MAPPER;
    }

    public static ObjectMapper forXml()
    {
        return XML_MAPPER;
    }

    public static ObjectNode newNode()
    {
        return JSON_MAPPER.createObjectNode();
    }
}
