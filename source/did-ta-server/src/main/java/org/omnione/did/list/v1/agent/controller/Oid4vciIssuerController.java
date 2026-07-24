package org.omnione.did.list.v1.agent.controller;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.list.v1.agent.dto.oid4vci.Oid4vciIssuerPublicDto;
import org.omnione.did.list.v1.agent.dto.oid4vci.Oid4vciIssuerListResDto;
import org.omnione.did.list.v1.agent.dto.oid4vci.Oid4vciIssuerRegistrationResDto;
import org.omnione.did.list.v1.agent.dto.oid4vci.RegisterOid4vciIssuerReqDto;
import org.omnione.did.list.v1.agent.service.Oid4vciIssuerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlConstant.List.AGENT_V1 + UrlConstant.List.OID4VCI_ISSUERS)
public class Oid4vciIssuerController {
    private final Oid4vciIssuerService service;

    @PostMapping
    public Oid4vciIssuerRegistrationResDto register(@RequestBody RegisterOid4vciIssuerReqDto request) {
        return service.register(request);
    }

    @GetMapping
    public Oid4vciIssuerListResDto findActive() {
        return service.findActive();
    }

    @GetMapping("/detail")
    public Oid4vciIssuerPublicDto findActive(@RequestParam String credentialIssuer) {
        return service.findActive(credentialIssuer);
    }
}
