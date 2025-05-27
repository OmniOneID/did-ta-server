import { getData, postData } from "../utils/api";

const API_BASE_URL = "/tas/admin/v1";

export const getTaInfo = async () => {
    return getData(API_BASE_URL, "ta/info");
}

export const validateTaSecret = async (data: any) => {
    return postData(API_BASE_URL, `ta/validate-ta-secret`, data);
};

export const registerTaInfo = async (data: any) => {
    return postData(API_BASE_URL, `ta/register-ta-info`, data);
};

export const generateTaDidDocument = async () => {
    return postData(API_BASE_URL, `ta/generate-did-auto`, undefined);
}

export const registerTaDidDocument = async (data: any) => {
    return postData(API_BASE_URL, `ta/register-ta-did`, data);
}

export const generateTaCertificate = async (data: any) => {
    return postData(API_BASE_URL, `ta/generate-certificate`, data);
}

export const getTaCertificate = async () => {
    return getData(API_BASE_URL, "ta/certificate");
}

export const registerTaCertificate = async (data: any) => {
    return postData(API_BASE_URL, `ta/certificate`, data);
}

