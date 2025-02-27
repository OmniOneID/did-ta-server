import { getData, postData } from "../utils/api";

export const getKycInfo = async () => {
    return getData(`kycs`);
}

export const registerKycInfo = async (data: any) => {
    return postData(`kycs`, data);
}