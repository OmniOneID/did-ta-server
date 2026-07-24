import { deleteData, getData, patchData, postData, putData } from "../utils/api";
import type { Oid4vciIssuerForm, Oid4vciIssuerStatus } from "./models/Oid4vciIssuer";

const API_BASE_URL = "/list/admin/v1";

export const fetchAllowedCaLIst = async (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `allowed-cas/list?${params.toString()}`);
};

export const getAllowedCaInfo = async (id: number) => {
    return getData(API_BASE_URL, `allowed-cas?id=${id}`);
}

export const registerAllowedCa = async (data: any) => {
    return postData(API_BASE_URL, 'allowed-cas', data);
}

export const updateAllowedCa = async (data: any) => {
    return putData(API_BASE_URL, 'allowed-cas', data);
}

export const verifyWalletIdUnique = async (walletId: string) => {
    return getData(API_BASE_URL, `allowed-cas/check-wallet-id?walletId=${walletId}`);
}

export const deleteAllowedCa = async (id: number) => {  
    return deleteData(API_BASE_URL, `allowed-cas?id=${id}`);
}

export const fetchVcSchemaList = async (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `vc-schemas/list?${params.toString()}`);
};

export const getVcSchemaInfo = async (id: number) => {
    return getData(API_BASE_URL, `vc-schemas?id=${id}`);
}

export const fetchVcPlanList = async (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `vc-plans/list?${params.toString()}`);
}

export const getVcPlanInfo = async (id: number) => {
    return getData(API_BASE_URL, `vc-plans?id=${id}`);
}


export const fetchCredentialSchemaList = async (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `credential-schemas/list?${params.toString()}`);
};

export const getCredentialSchemaInfo = async (id: number) => {
    return getData(API_BASE_URL, `credential-schemas?id=${id}`);
}


export const fetchCredentialDefinitionList = async (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `credential-definitions/list?${params.toString()}`);
};

export const getCredentialDefinitionInfo = async (id: number) => {
    return getData(API_BASE_URL, `credential-definitions?id=${id}`);
}

export const fetchCertificateVcList = async (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `certificate-vc/list?${params.toString()}`);
};

export const getCertificateVcInfo = async (id: number) => {
    return getData(API_BASE_URL, `certificate-vc?id=${id}`);
}

export const fetchOid4vciIssuerList = async (
    page: number,
    size: number,
    searchValue: string | null,
    status: Oid4vciIssuerStatus | null,
    sort = "createdAt,desc",
) => {
    const params = new URLSearchParams({ page: String(page), size: String(size), sort });
    if (searchValue) params.set("searchValue", searchValue);
    if (status) params.set("status", status);
    return getData(API_BASE_URL, `oid4vci/issuers/list?${params.toString()}`);
};

export const getOid4vciIssuer = (id: number) =>
    getData(API_BASE_URL, `oid4vci/issuers?id=${id}`);

export const updateOid4vciIssuer = (id: number, data: Oid4vciIssuerForm) =>
    putData(API_BASE_URL, "oid4vci/issuers", { id, ...data });

export const changeOid4vciIssuerStatus = (
    id: number,
    status: Oid4vciIssuerStatus,
    reason?: string,
) => patchData(API_BASE_URL, `oid4vci/issuers/${id}/status`, { status, reason });

export const getOid4vciIssuerStatusHistory = (id: number) =>
    getData(API_BASE_URL, `oid4vci/issuers/${id}/status-history`);
