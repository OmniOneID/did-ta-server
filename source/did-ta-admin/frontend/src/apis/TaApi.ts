import { getData, postData, deleteData, putData } from "../utils/api";

export const getTaInfo = async () => {
    return getData("/ta/info");
}