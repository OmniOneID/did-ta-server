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

import org.omnione.did.tas.v1.agent.dto.entity.ConfirmEnrollEntityReqDto;
import org.omnione.did.tas.v1.agent.dto.entity.ProposeEnrollEntityReqDto;
import org.omnione.did.tas.v1.agent.dto.entity.RequestEnrollEntityReqDto;
import org.omnione.did.tas.v1.agent.service.EntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.tas.v1.agent.dto.entity.ConfirmEnrollEntityResDto;
import org.omnione.did.tas.v1.agent.dto.entity.ProposeEnrollEntityResDto;
import org.omnione.did.tas.v1.agent.dto.entity.RequestEnrollEntityResDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * this is a sample implementation of the EntityService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("sample")
public class EntityServiceSample implements EntityService {
    @Override
    public ProposeEnrollEntityResDto proposeEnrollEntity(ProposeEnrollEntityReqDto proposeEnrollEntityReqDto) {
        return ProposeEnrollEntityResDto.builder()
                .authNonce("mxbrrv9EupAFUumfyXD9Vag")
                .txId("b86855ad-6793-4e15-bd1c-d44c01a87ee8")
                .build();
    }

    @Override
    public RequestEnrollEntityResDto requestEnrollEntity(RequestEnrollEntityReqDto requestEnrollEntityReqDto) {
        return RequestEnrollEntityResDto.builder()
                .txId("b86855ad-6793-4e15-bd1c-d44c01a87ee8")
                .encVc("mJ2uwCQMr4GFiLW+sgVBflh9NGZ+AZ+7Pua00E9vWIS5sBDQrBnl5Zc9wUAml9fXbCmaQgTmgVFytjr2n5wEJsumqgjYRcv+DjmJEDCxXPogsMPMVrBLwjhy2pTE163oAplLE5YOgEe437H7xXY1ok00NzgkNlVsWCUrz3aBv+yKukuV1Rq9Dhe05JBFvrwBOwNnoaXEQ1SScaQCD+XsGzvYMqBcR1VcoR1Kaadpkr9iQm11wXz+Q8FhTwQ1mFbXWcmZaWcBtUUdGHYIu2z+wuCTwYszwCAIz1etDxqMYmgPYT0c5UPuk9dZ8TDfK4qTBSW1m+o7sfk1ctyNMrnJLK5gkG9A1mi9kLBa8lM0UPo2pLBnTHCplCS49PdziknsBRN284y6DQ9SKYLFMkNJ1u4YBg6sC4gKyrL3e4Q+haDvtdqBAxKSE5W63k9Vx3NsNAGrytE3Z63sv2qMM0F4x2cRgx0Vx3LCErVw+/1//R8ZD049ON+LsywdE/NI+I55jt8tXOBlHuTMBus3QKErsxdi/mX15WnQc7LGpKcEFJ9Fad6bthSzybBw7qUDo5bMUBWeBr3r68/jowgDKD0R4ddcT9vTUuHNBgwTjomFyllJWXzy5eImowmjORlIieLcqlYbGmbViCZ281xTk5pQB7gc993eLEQyx2bSjruhLcFhqDMOg/eHV504yDwhIZ6Wnr1IYJ2Y8evJhTkqaJAAkMVG1lScgmzEPvPUNGcqQHfSMD2XMGKtjHJOBJ5axlEYbtV53r/p89a8aibLSNow1m8Nou4mXKNmJPeQA60rIqAAsF9MzmjWkT3Z056ei6qWX8xRpUX+ww4Z9uPoYtHHRcSJjEzW2QTenqTF+H3WZtxGutI3O+68BkCmXkQrc9a0yriLTWK9DEPgPgw0/oxV/haGJxo5S8boq/8AS8UAKR/+Nn5PWUkvOyiCFASKt4ue1YKhrpftRAIO21huAoVAYhcv0gj3f+gQbZsDLOFA7/2HZOlxcge7aig+vE85Flk0aumqgHuQhj0aRy59YtjWdcNKlui0TKvlXYdpe+itqTuY0inh1DUAdWslBRZJTnOgsLjVICowMLOZXXYshg4qG2uoB4lZjNxXXmwge/i+CdhE0QxyiO1phrO/owkEK2pkXGS8Wb2zDd4XiIoR8nvCo0M5nsYWDGBq9npJvNdtNjE4+kok+JclHAuj3CDDuNUOUcoBKHZ2ok0egZF2aOVpF6Ov3MQDTNHVvgKO0PGfKuzVSCstCl5fAPXInxn9dl2uoXbO2UXQdq/TBo8otMvOfP9ohh1M/1xMvN3//llTsWQFjlhXmcUfRvSEY53a3DXbj1D72QlbrJGgxbDfcDBiYWtGmJfwSzpLozDHp/Tnmgj069wde97nNGOr1yqsbkW/T3r2LBMBs/fkm3xpTOmW/LDJuaw7XRqrvJZxFoyrRWCWBUHaJsJA3GbFzo92x+uADs4k7NhhvEhDw+Gi9oCj/yAloZ9202pzLuRFJrGViGWNdvZkuuhJ2uNRQypSKtmDm7/qZZxE5rl8MsdP3kI/yme8Y02voClyi6UV6duWab2fkbBv5kYSyHB1e1o183uO6uoxtPrNG/kaSCZ98TXF5to8xMOPfvhGVE8EHV0UBiRAzFOljMPHs5JIJQxXe2e+ssSHzge1qFgzihl7xrxp2U4ljI/0V+c7igEzLw+9/FC/+B4oHx+RhuSGyuCHFSdoa/u03/W8hHH7j07473NQXuApfY0CVP2JKeLS68hNUq90IACmbVOlW330IebZ+6ed2hukZl3RXqnF9a9t4thhq95AxvY2i9Mz2E2O+TuYBdP90CjsgfF6IcVR1fHT2AW+m3UIeLDdPMp7/oUyMxVBOEK30Xo723G8Umrr6f5X+vEFk4MlVJl6up5p/YTzzuJfk")
                .iv("mJITQJzWHqXkZLWScaC8xqw")
                .build();
    }

    @Override
    public ConfirmEnrollEntityResDto confirmEnrollEntity(ConfirmEnrollEntityReqDto confirmEnrollEntityReqDto) {
        return ConfirmEnrollEntityResDto.builder()
                .txId("b86855ad-6793-4e15-bd1c-d44c01a87ee8")
                .build();
    }
}
