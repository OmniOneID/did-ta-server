import { deleteData, getData, postData, putData } from "../utils/api";

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