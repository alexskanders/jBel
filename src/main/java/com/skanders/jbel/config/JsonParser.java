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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.util.Iterator;
import java.util.Map;

/**
 * JsonParser
 * <p>
 * If Config is created as an encrypted type then the entire JsonNode tree will
 * be parsed and the decryptor will replace all instances of 'enc="value"' with
 * its decrypted value
 */
class JsonParser
{
    private static final String ENCRYPTED_VALUE_LABEL = "enc=";

    /**
     * Starting point for JsonNodeDecryptor. Starts parsing as Either a Array Or
     * Object first. If the JsonNode is either of these then the JsonValue is
     * ignored.
     * <p>
     * Only JsonNode Objects will are valid for RMSProperties as values can only
     * be retrieved by a key and not index. However Array Is left for future
     * additions to RMSProperties and This class will even decrypt Yamls that
     * are arrays.
     *
     * @param nodes     JsonNode built from RMS config yaml file
     * @param encryptor an instance of StandardPBEStringEncryptor
     */
    public static void decryptNodes(JsonNode nodes, StandardPBEStringEncryptor encryptor)
    {
        switch (nodes.getNodeType()) {
            case ARRAY:
                parseArray((ArrayNode) nodes, encryptor);
                break;

            case OBJECT:
                parseObject(nodes, encryptor);
                break;

            default:
                // continue

        }
    }

    /**
     * Parses an Object JsonNode, replacing ONLY text nodes IF the text value
     * was flagged with the label 'enc='
     *
     * @param objectNode an Object JsonNode instance
     * @param encryptor  an instance of StandardPBEStringEncryptor
     */
    private static void parseObject(JsonNode objectNode, StandardPBEStringEncryptor encryptor)
    {
        Iterator<Map.Entry<String, JsonNode>> it = objectNode.fields();

        while (it.hasNext()) {
            Map.Entry<String, JsonNode> node = it.next();

            switch (node.getValue().getNodeType()) {
                case ARRAY:
                    parseArray((ArrayNode) node.getValue(), encryptor);
                    break;

                case OBJECT:
                    parseObject(node.getValue(), encryptor);
                    break;

                case STRING:
                    String value = decrypt(node.getValue(), encryptor);

                    if (value != null)
                        ((ObjectNode) objectNode).replace(node.getKey(), new TextNode(value));

                    break;

                default:
                    // continue

            }
        }
    }

    /**
     * Parses an Array JsonNode (ArrayNode), replacing ONLY text nodes IF the
     * text value was flagged with the label 'enc='
     *
     * @param arrayNode an Array JsonNode (ArrayNode) instance
     * @param encryptor an instance of StandardPBEStringEncryptor
     */
    private static void parseArray(ArrayNode arrayNode, StandardPBEStringEncryptor encryptor)
    {
        int size = arrayNode.size();

        for (int i = 0; i < size; i++)
            switch (arrayNode.get(i).getNodeType()) {
                case ARRAY:
                    parseArray((ArrayNode) arrayNode.get(i), encryptor);
                    break;

                case OBJECT:
                    parseObject(arrayNode.get(i), encryptor);
                    break;

                case STRING:
                    String value = decrypt(arrayNode.get(i), encryptor);

                    if (value != null)
                        arrayNode.set(i, new TextNode(value));

                    continue;

                default:
                    //continue

            }
    }

    /**
     * Checks the value for encryption label. If labeled then the value will
     * automatically be decrypted.
     * <p>
     * If the label is missing then the nul will be returned to avoid replacing
     * the node
     *
     * @param node      TextNode to check
     * @param encryptor a StandardPBEStringEncryptor instance
     * @return the decrypted value or null
     */
    private static String decrypt(JsonNode node, StandardPBEStringEncryptor encryptor)
    {
        if (!node.isTextual())
            return null;

        String value = node.asText();

        if (value != null && value.startsWith(ENCRYPTED_VALUE_LABEL))
            return encryptor.decrypt(value.substring(ENCRYPTED_VALUE_LABEL.length()));
        else
            return null;
    }
}
