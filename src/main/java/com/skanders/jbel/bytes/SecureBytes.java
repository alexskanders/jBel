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


package com.skanders.jbel.bytes;

import com.google.common.io.BaseEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Arrays;

public class SecureBytes
{
    private static final Logger LOG = LoggerFactory.getLogger(SecureBytes.class);

    private static final byte B_CLEAR = '\u0000';

    private static final BaseEncoding BASE_16_ENCODING     = BaseEncoding.base16();
    private static final BaseEncoding BASE_32_ENCODING     = BaseEncoding.base32();
    private static final BaseEncoding BASE_32_HEX_ENCODING = BaseEncoding.base32Hex();
    private static final BaseEncoding BASE_64_ENCODING     = BaseEncoding.base64();
    private static final BaseEncoding BASE_64_URL_ENCODING = BaseEncoding.base64Url();

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static byte[] genBytes(int length)
    {
        byte[] bytes = new byte[length];
        SECURE_RANDOM.nextBytes(bytes);

        return bytes;
    }

    public static byte[] decode16(String encoded)
    {
        return BASE_16_ENCODING.decode(encoded);
    }

    public static byte[] decode32(String encoded)
    {
        return BASE_32_ENCODING.decode(encoded);
    }

    public static byte[] decode32Hex(String encoded)
    {
        return BASE_32_HEX_ENCODING.decode(encoded);
    }

    public static byte[] decode64(String encoded)
    {
        return BASE_64_ENCODING.decode(encoded);
    }

    public static byte[] decode64Url(String encoded)
    {
        return BASE_64_URL_ENCODING.decode(encoded);
    }

    public static String encode16(byte[] decoded)
    {
        return BASE_16_ENCODING.encode(decoded);
    }

    public static String encode32(byte[] decoded)
    {
        return BASE_32_ENCODING.encode(decoded);
    }

    public static String encode32Hex(byte[] decoded)
    {
        return BASE_32_HEX_ENCODING.encode(decoded);
    }

    public static String encode64(byte[] decoded)
    {
        return BASE_64_ENCODING.encode(decoded);
    }

    public static String encode64Url(byte[] decoded)
    {
        return BASE_64_URL_ENCODING.encode(decoded);
    }

    public static String gen16(int length)
    {
        return BASE_16_ENCODING.encode(genBytes(length));
    }

    public static String gen32(int length)
    {
        return BASE_32_ENCODING.encode(genBytes(length));
    }

    public static String gen32Hex(int length)
    {
        return BASE_32_HEX_ENCODING.encode(genBytes(length));
    }

    public static String gen64(int length)
    {
        return BASE_64_ENCODING.encode(genBytes(length));
    }

    public static String gen64Url(int length)
    {
        return BASE_64_URL_ENCODING.encode(genBytes(length));
    }

    public static void clearBytes(byte[] bytes)
    {
        LOG.trace("Clear Bytes Called");

        if (bytes == null) {
            LOG.trace("Nothing to clear");
            return;
        }

        Arrays.fill(bytes, B_CLEAR);

        LOG.trace("Bytes cleared");
    }

    public static boolean equals(byte[] arrayOne, byte[] arrayTwo)
    {
        return Arrays.equals(arrayOne, arrayTwo);
    }
}
