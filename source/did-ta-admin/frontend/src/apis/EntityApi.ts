import { getData, postData, uploadData } from "../utils/api";

export const fetchEntities = async (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(`entities/list?${params.toString()}`);
};

export const getEntityInfo = async (id: number) => {
    return getData(`entities?id=${id}`);
}

export const registerEntity = async (data: FormData) => {
    return uploadData('entities', data);
}

export const verifyEntityNameUnique = async (name: string) => {
    return getData(`entities/check-name?name=${name}`);
}

export const registerEntitiesSimple = async () => {
    return postData(`entities/register-simple`, null);
}