import { getData, postData } from "../utils/api";

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

export const getEmailTemplate = async (serverType: string, templateType: string) => {
    return getData(API_BASE_URL, `templates?serverType=${serverType}&templateType=${templateType}`);
}

export const registerEmailTemplate = async (data: any) => {
    return postData(API_BASE_URL, `templates`, data);
}

export const getNotificationServerStatus = async () => {
    return getData(API_BASE_URL, `servers/status`);
}

export const registerPushServerInfo = async (data: any) => {
    return postData(API_BASE_URL, `servers/push`, data);
};