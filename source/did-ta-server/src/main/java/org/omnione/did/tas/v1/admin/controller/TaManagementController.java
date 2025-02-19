package org.omnione.did.tas.v1.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant.Tas;
import org.omnione.did.tas.v1.admin.dto.RequestTasInfoResDto;
import org.omnione.did.tas.v1.admin.service.TaManagementService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Please explain the class!!
 *
 * @author : yklee0911
 * @fileName : TaManagementController
 * @since : 2/18/25
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = Tas.ADMIN_V1)
public class TaManagementController {
    private final TaManagementService taManagementService;

    /**
     * Request TA information.
     *
     * @return TA information
     */
    @RequestMapping(value = "/ta/info", method = RequestMethod.GET)
    public RequestTasInfoResDto requestTaInfo() {
        return taManagementService.requestTaInfo();
    };

    /**
     * Register TA with simple process. (1st development version)
     */
    @RequestMapping(value = "/ta/register-simple", method = RequestMethod.POST)
    public void registerTaSimple() {
        taManagementService.registerTaSimple();
    }
}
