export const loginMember = async (formData) => {
    try {
        const response = await fetch(`/lastwine/login`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(formData),
        });
        // 檢查 HTTP 狀態
        if (!response.ok) {
            console.error(`HTTP error! status: ${response.status}`);
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        // 確保回應的 JSON 結構正確
        const data = await response.json();
        if (!data || !data.token) {
            throw new Error("缺少token");
        }
        // 保存 Token 到 localStorage
        localStorage.setItem("token", data.token);
        return data;
    } catch (error) {
        console.error("帳號或密碼錯誤")
        throw error;
    }
};

// 設置：附帶 Authorization Header
export const fetchWithAuth = async (url, options = {}) => {
    const token = localStorage.getItem("token");
    // 檢查是否存在 Token
    if (!token) {
        throw new Error("未找到Token");
    }
    const headers = {
        Authorization: `Bearer ${token}`, // 附加 Token
        ...options.headers, // 合併自定義標頭
    };
    try {
        const response = await fetch(url, {...options, headers});

        if (!response.ok) {
            throw new Error(`HTTP error! status:${response.status}`);
        }

        return await response;
    } catch (error) {
        console.error("請求失敗:", error.message);
        throw error;
    }
}