import { getData, postData, uploadData, deleteData } from "../utils/api";

const API_BASE_URL = "/tas/admin/v1";

export const fetchEntities = async (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `entities/list?${params.toString()}`);
};

export const getEntityInfo = async (id: number) => {
    return getData(API_BASE_URL, `entities?id=${id}`);
}

export const registerEntity = async (data: FormData) => {
    return uploadData(API_BASE_URL, 'entities', data);
}

export const verifyEntityNameUnique = async (name: string) => {
    return getData(API_BASE_URL, `entities/check-name?name=${name}`);
}

export const registerEntitiesSimple = async () => {
    return postData(API_BASE_URL, `entities/register-simple`, null);
}

export const approveEntityDid = async (data: any) => {
    return postData(API_BASE_URL, `entities/approve-did`, data);
}

export const deleteEntity = async (id: number) => {
    return deleteData(API_BASE_URL, `entities?id=${id}`);
}