import { getData, postData, uploadData, deleteData } from "../utils/api";

const API_BASE_URL = "/tas/admin/v1";

export const fetchUsers = async (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `users/list?${params.toString()}`);
};

export const getUserInfo = async (id: number) => {
    return getData(API_BASE_URL, `users?id=${id}`);
}

export const fetchApps = async (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `apps/list?${params.toString()}`);
};

export const getAppInfo = async (id: number) => {
    return getData(API_BASE_URL, `apps?id=${id}`);
}

export const fetchWallets = async (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `wallets/list?${params.toString()}`);
};

export const getWalletInfo = async (id: number) => {
    return getData(API_BASE_URL, `wallets?id=${id}`);
}