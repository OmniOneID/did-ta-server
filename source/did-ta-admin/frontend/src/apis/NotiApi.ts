import { getData, postData } from "../utils/api";
toString

const API_BASE_URL = "/noti/admin/v1";

export const getEmailServerInfo= async () => {
    return getData(API_BASE_URL, `servers/email`);
};

export const registerEmailServerInfo = async (data: any) => {
    return postData(API_BASE_URL, `servers/email`, data);
};

export const sendTestEmail = async (data: any) => {
    return postData(API_BASE_URL, `servers/email/test`, data);
};