// 所有 API 共用的 fetch 包裝：統一帶 token、檢查狀態碼、解析回應

/**
 * @param url     API 路徑
 * @param options fetch 選項
 * @returns 解析後的回應內容（JSON 或純文字）
 */
export const request = async (url, options = {}) => {
    const response = await fetch(url, options);
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    if (response.status === 204) {
        return null;
    }
    // 後端有些端點回純文字（例如刪除酒譜），依 Content-Type 決定怎麼解析
    const contentType = response.headers.get("Content-Type") || "";
    return contentType.includes("application/json") ? response.json() : response.text();
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
