import { getData, postData } from "../utils/api";

export const verifyServerUrl = async (body: any) => {
    return postData(`servers/ping`, body);
}