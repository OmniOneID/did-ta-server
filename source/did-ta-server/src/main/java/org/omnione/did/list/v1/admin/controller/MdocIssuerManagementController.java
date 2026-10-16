package org.omnione.did.list.v1.admin.controller;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.base.db.constant.MdocIssuerStatus;
import org.omnione.did.list.v1.admin.dto.mdoc.*;
import org.omnione.did.list.v1.admin.service.MdocIssuerManagementService;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlConstant.List.ADMIN_V1 + UrlConstant.List.MDOC_ISSUERS)
public class MdocIssuerManagementController {
    private final MdocIssuerManagementService service;

    @GetMapping("/list")
    public Page<MdocIssuerDto> search(@RequestParam(required = false) String searchValue,
                                      @RequestParam(required = false) MdocIssuerStatus status,
                                      Pageable pageable) {
        return service.search(searchValue, status, pageable);
    }

    @GetMapping
    public MdocIssuerDto findById(@RequestParam Long id) {
        return service.findById(id);
    }

    @GetMapping("/metadata-doc-types")
    public MdocIssuerMetadataDto loadMetadata(@RequestParam String metadataUri) {
        return service.loadMetadata(metadataUri);
    }

    @PostMapping
    public MdocIssuerDto register(@RequestBody RegisterMdocIssuerReqDto request) {
        return service.register(request);
    }

    @PutMapping
    public EmptyResDto update(@RequestBody UpdateMdocIssuerReqDto request) {
        return service.update(request);
    }

    @PatchMapping("/{id}/status")
    public EmptyResDto changeStatus(@PathVariable Long id,
                                    @RequestBody ChangeMdocIssuerStatusReqDto request) {
        return service.changeStatus(id, request);
    }

    @GetMapping("/{id}/status-history")
    public List<MdocIssuerStatusHistoryDto> history(@PathVariable Long id) {
        return service.history(id);
    }
}
