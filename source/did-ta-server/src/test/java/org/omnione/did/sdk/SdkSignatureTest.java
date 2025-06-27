/*
 * Copyright 2025 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.sdk;

import org.omnione.did.base.util.BaseMultibaseUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SdkSignatureTest {

    @Test
    public void parsePublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String keyStr = "-----BEGIN PUBLIC KEY----- MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEvTQK0jDScJOb8QdZqcmqT/E+H7j5V4C1LgHhCecQyGgZvBEM3Hb9kGVGJ7HrI69GG4YvcGsB2u5MeEYYZ3vGEQ== -----END PUBLIC KEY-----";
        keyStr = keyStr
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", ""); // 모든 공백 문자 제거

        byte[] keyBytes = Base64.getDecoder().decode(keyStr);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        keyFactory.generatePublic(spec);

        String encodedStr = BaseMultibaseUtil.encode(keyStr.getBytes());
        String decodedStr = new String(BaseMultibaseUtil.decode(encodedStr));

        Assertions.assertEquals(keyStr, decodedStr);
    }

    @Test
    public void parsePrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String keyStr = "-----BEGIN PRIVATE KEY----- MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgzyBZZbk9jv2ejYWcFmnJAzG5IMR+/GBdf47cXHqHTC6hRANCAAS9NArSMNJwk5vxB1moyapP8T4fuPlXgLUuAeEJ5xDIaBm8EQzcdv2QZUYnse0jrwYbhixwawHa7kx4Rhhne8YR -----END PRIVATE KEY-----";
        keyStr = keyStr
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", ""); // 모든 공백 문자 제거


        byte[] keyBytes = Base64.getDecoder().decode(keyStr);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        keyFactory.generatePrivate(spec);

        String encodedStr = BaseMultibaseUtil.encode(keyStr.getBytes());
        String decodedStr = new String(BaseMultibaseUtil.decode(encodedStr));

        Assertions.assertEquals(keyStr, decodedStr);
    }

    @Test
    public void createMultibasePublicKey()  {
        String keyStr = "-----BEGIN PUBLIC KEY----- MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEVbkgKzFyYQ8nX1aD2NVr1+4BL8tjhG6GcbmZ5smZwIfOpSbXKy3Gc8xsZLgeiBQ+Wc5Mk5Boz7kxhDrHE9YNOw== -----END PUBLIC KEY-----";
        keyStr = keyStr
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", ""); // 모든 공백 문자 제거

        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        System.out.println(BaseMultibaseUtil.encode(keyBytes));
    }
}
