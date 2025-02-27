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

package org.omnione.did.tas.v1.agent.service.sample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.omnione.did.base.datamodel.enums.ServerTokenPurpose;
import org.omnione.did.tas.v1.agent.dto.user.RequestCreateTokenReqDto;
import org.omnione.did.tas.v1.agent.dto.user.RequestCreateTokenResDto;
import org.omnione.did.tas.v1.agent.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * this is a sample implementation of the TokenService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("sample")
public class TokenServiceSample implements TokenService {
    @Override
    public RequestCreateTokenResDto requestCreateToken(RequestCreateTokenReqDto requestCreateTokenReqDto) {
        ObjectMapper objectMapper = new ObjectMapper();

        log.debug("$$$ request purpose :: " + requestCreateTokenReqDto.getSeed().getPurpose());
        if (requestCreateTokenReqDto.getSeed().getPurpose().equals(ServerTokenPurpose.CREATE_DID)) {
            String request = """
                    {"encStd":"mHRB7lOr7fAeMMvhB9rOU5/wLMk7rSJZI0U0lpBrF8oOKEc1ctRC7/uTDmotpL1mF/hrjmqXMaQ39GnoLTefzmtrP82bAIpbTrxwU9cYmOvJNqUYswy8+9IX7jGRX0hto22oDlMn6LUC6SCJ/wmi/KrtnmLDkBY0gu629BES13vgY/b9sBcw7zA+CCV1St5szAYwmfwN4QubIIe4W/rVS//Ucz+azbMKYI7REmCNdpI3EPzYSc5L9Hd3E8PTYzmxfRj1OG56lAJeQHtH8/1kBgDXp+1zeT37X/dDTs0OlwckpD7yN/BltABQK/Wi8wRzmjj3S6F3zd2S5GphzfjEvJUMx4z6qXhTiH4FEtV/tJgl44G/hlWKw70OKdvuhBUSXlfgDF/PgMhhx5KgmhZUCMGev3M9y7CVugu38V6GYsmYuuYjQn1y7BRtnKrWuOaak0vSOv5MYg5qbUfHNAYmqZNFgjyJA4tBELWukeEi6raOzg2FO6ZesHR7jcArskWkqdVtQIyvuMavu4hNnQDeHbAfrnPe5NGaZjCZ2mA/vMCRo09qotFG2LfPb8+4tIbOUqA+DMQVfljKsxlr1s8z3Ghz9SJf/LhqAtxzXdHaYIiijCzMAy3ZJFHglrPnWMxtaw/cUghffdrNYgIE8EBu4VzHiZQ+sljachNrISOaiAW9yT0Gka5tEvetilmpi7YgL","iv":"m269B16J8Sl0jar6LJWcyWA","txId":"61e4164d-939d-4252-b2f4-5026c8225a3b"}
                    """;
            try {
                return objectMapper.readValue(request, RequestCreateTokenResDto.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[16];
        String txId = "99999999-9999-9999-9999-999999999999";
        String encStd = "zHri4iJ8q2mcv4GKmrb3GgnsyY6hT93rbvQir1eAmnqvMrfRcRUTfs16NQvrReuV9hx76X5qSpQ19NVm78ca4jnRuDoqbDwAqmtGPLwUvVaLUFMh6oEXZzfQQ5ds6JHMDvcYpeKCHtmyfUb2W7DbhZNEg9D4Au5TqQomey9A2vWG9FrN91PUg9nfyt9NCfqX6s38JHvedKCjqixBiv4Gs5hk2HNN3aCuS5Y53ACGeADA3cKFHwpJZNYBubHN7QAraBFu5zjWRv4RgK46MnTmfyxXzPLucjeRg9qAUabCJWmb6RwWT1SoUFzk8CMoQtppfn8GDHfcUrhGHEFcU2PYu3kKr97NLGbrpdftha2wVprd4ZKD4YS78pLSeXKGGEsnWU5CatFN7ayZqTU5ZspwZ567SUohWfJZn3XXp9y938rDr5WW1RtWD6UsxFxSY14h7C694DUkNsZKJejcnBxLqdqxbeqRn8AMvx";

        secureRandom.nextBytes(randomBytes);

        return RequestCreateTokenResDto.builder()
                .encStd(encStd)
                .txId(txId)
                .iv("z75M7MfQsC4p2rTxeKxYh2M")
                .build();
    }


}
