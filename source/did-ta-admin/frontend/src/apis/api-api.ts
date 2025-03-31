import { getData, postData } from "../utils/api";

const API_BASE_URL = "/tas/admin/v1";

export const getExpirationSettingInfo = async () => {
    return getData(API_BASE_URL, `apis/expiration`);
}

export const registerExpirationSettingInfo = async (data: any) => {
    return postData(API_BASE_URL, `apis/expiration`, data);
}

export const getKeyExchangePolicyInfo = async () => {
    return getData(API_BASE_URL, `apis/key-exchange-policy`);
}

export const registerKeyExchangePolicyInfo = async (data: any) => {
    return postData(API_BASE_URL, `apis/key-exchange-policy`, data);
}