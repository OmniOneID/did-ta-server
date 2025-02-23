import { getData, postData, deleteData, putData } from "../utils/Api";

export const getTaInfo = async () => {
    return getData("ta/info");
}

export const helthCheck = async () => {
    return getData("health");
}