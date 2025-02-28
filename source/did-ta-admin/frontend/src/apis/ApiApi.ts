import { getData, postData } from "../utils/api";

export const getExpirationSettingInfo = async () => {
    return getData(`apis/expiration`);
}

export const registerExpirationSettingInfo = async (data: any) => {
    return postData(`apis/expiration`, data);
}