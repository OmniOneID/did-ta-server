package org.omnione.did.list.v1.admin.controller;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.base.db.constant.Oid4vciIssuerStatus;
import org.omnione.did.list.v1.admin.dto.oid4vci.*;
import org.omnione.did.list.v1.admin.service.Oid4vciIssuerManagementService;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlConstant.List.ADMIN_V1 + UrlConstant.List.OID4VCI_ISSUERS)
public class Oid4vciIssuerManagementController {
    private final Oid4vciIssuerManagementService service;

    @GetMapping("/list")
    public Page<Oid4vciIssuerDto> search(@RequestParam(required = false) String searchValue,
                                         @RequestParam(required = false) Oid4vciIssuerStatus status,
                                         Pageable pageable) {
        return service.search(searchValue, status, pageable);
    }

    @GetMapping
    public Oid4vciIssuerDto findById(@RequestParam Long id) {
        return service.findById(id);
    }

    @PutMapping
    public EmptyResDto update(@RequestBody UpdateOid4vciIssuerReqDto request) {
        return service.update(request);
    }

    @PatchMapping("/{id}/status")
    public EmptyResDto changeStatus(@PathVariable Long id,
                                    @RequestBody ChangeOid4vciIssuerStatusReqDto request) {
        return service.changeStatus(id, request);
    }

    @GetMapping("/{id}/status-history")
    public List<Oid4vciIssuerStatusHistoryDto> history(@PathVariable Long id) {
        return service.history(id);
    }
}
