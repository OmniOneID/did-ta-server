import { getData } from "../utils/api";

export const getTaInfo = async () => {
    return getData("ta/info");
}

export const helthCheck = async () => {
    return getData("health");
}