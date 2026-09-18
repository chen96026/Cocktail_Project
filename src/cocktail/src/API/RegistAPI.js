import {requestJson} from "./request.js";

/**
 * @param formData 帳號與密碼
 * @returns 後端回應（含 token 與 role）
 */
export const registMember = async (formData) => {
    const data = await requestJson("/lastwine/regist", formData);
    if (!data || !data.token) {
        throw new Error("缺少token");
    }
    return data;
};
