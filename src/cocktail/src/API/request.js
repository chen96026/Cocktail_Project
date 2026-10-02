// 所有 API 共用的 fetch 包裝：檢查狀態碼、解析 JSON 回應，失敗時帶出後端的錯誤訊息

/**
 * @param url     API 路徑
 * @param options fetch 選項
 * @returns 解析後的 JSON 回應內容
 * @throws Error 狀態碼不是 2xx 時，message 優先用後端 ApiError 的 message
 */
export const request = async (url, options = {}) => {
    const response = await fetch(url, options);
    if (!response.ok) {
        // 後端錯誤統一回 ApiError {message}；body 不是 JSON（例如 proxy 錯誤頁）就用預設訊息
        const body = await response.json().catch(() => null);
        throw new Error(body?.message ?? `HTTP error! status: ${response.status}`);
    }
    if (response.status === 204) {
        return null;
    }
    // 後端所有端點都回 JSON（含新增／更新／刪除的 {message}）
    return response.json();
};

/**
 * @param url  API 路徑
 * @param body 會被序列化成 JSON 的請求內容
 */
export const requestJson = (url, body, options = {}) =>
    request(url, {
        method: "POST",
        ...options,
        headers: {"Content-Type": "application/json", ...(options.headers || {})},
        body: JSON.stringify(body),
    });
