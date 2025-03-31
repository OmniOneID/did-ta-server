import { postData } from "../utils/api-util";

const API_BASE_URL = "/tas/admin/v1";

export const verifyServerUrl = async (body: any) => {
    return postData(API_BASE_URL, `servers/ping`, body);
}