import { getData, postData } from "../utils/api";

const API_BASE_URL = "/tas/admin/v1";

export const getKycInfo = async () => {
    return getData(API_BASE_URL, `kycs`);
}

export const registerKycInfo = async (data: any) => {
    return postData(API_BASE_URL, `kycs`, data);
}