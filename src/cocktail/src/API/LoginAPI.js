import {requestJson} from "./request.js";

/**
 * @param formData 帳號與密碼
 * @returns 後端回應（含 token 與 role），token 的保存由 AuthProvider 負責
 */
export const loginMember = async (formData) => {
    const data = await requestJson("/lastwine/login", formData);
    if (!data || !data.token) {
        throw new Error("缺少token");
    }
    return data;
};
