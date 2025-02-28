import { getData, postData } from "../utils/api";

export const getExpirationSettingInfo = async () => {
    return getData(`apis/expiration`);
}

export const registerExpirationSettingInfo = async (data: any) => {
    return postData(`apis/expiration`, data);
}

export const getKeyExchangePolicyInfo = async () => {
    return getData(`apis/key-exchange-policy`);
}

export const registerKeyExchangePolicyInfo = async (data: any) => {
    return postData(`apis/key-exchange-policy`, data);
}