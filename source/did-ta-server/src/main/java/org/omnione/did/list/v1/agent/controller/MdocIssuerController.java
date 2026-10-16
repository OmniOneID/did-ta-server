package org.omnione.did.list.v1.agent.controller;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.list.v1.agent.dto.mdoc.MdocIssuerListResDto;
import org.omnione.did.list.v1.agent.service.MdocIssuerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Public (no auth) mDoc trusted issuer list for mDoc readers. */
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlConstant.List.AGENT_V1 + UrlConstant.List.MDOC_ISSUERS)
public class MdocIssuerController {
    private final MdocIssuerService service;

    @GetMapping
    public MdocIssuerListResDto findActive() {
        return service.findActive();
    }
}
