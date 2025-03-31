import { getData } from "../utils/api-util";

const API_BASE_URL = "/tas/admin/v1";

export const getTaInfo = async () => {
    return getData(API_BASE_URL, "ta/info");
}