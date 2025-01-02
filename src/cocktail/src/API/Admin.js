export const addCombination = async (newCombination) => {
    try {
        const response = await fetch('/lastwine/addFourCombination', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(newCombination),
        });
        if (!response.ok) {
            throw new Error("上傳失敗");
        }
        // 根據返回的 Content-Type 處理回應
        const contentType = response.headers.get("Content-Type");
        if (contentType && contentType.includes("application/json")) {
            return await response.json(); // 如果是 JSON，解析 JSON
        } else {
            return await response.text(); // 如果是純文字，返回文字
        }
    } catch (error) {
        console.error("錯誤: ", error);
        throw error;
    }
};

export const getAllTheCombinations = async () => {
    try {
        const response = await fetch('/lastwine/getAllCombinations', {
            method: 'GET',
            headers: {'Content-Type': 'application/json'},
        });
        if (!response.ok) {
            throw new Error("獲取調酒組合失敗");
        }
        return await response.json(); // 返回 JSON 數據
    } catch (error) {
        console.error("錯誤: ", error);
        throw error;
    }
};

export const getAllRecipeCombinations = async () => {
    try {
        const response = await fetch('/lastwine/allCombinations', {
            method: 'GET',
            headers: {'Content-Type': 'application/json'},
        });
        if (!response.ok) {
            throw new Error("獲取所有調酒與組合失敗");
        }
        return await response.json(); // 返回 JSON 數據
    } catch (error) {
        console.error("錯誤: ", error);
        throw error;
    }
};

export const assignCombinations = async (payload) => {
    try {
        const response = await fetch('/lastwine/assignCombinations', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload), // 傳遞 JSON 陣列
        });

        console.log("請求的 JSON:", JSON.stringify(payload, null, 2));

        if (!response.ok) {
            throw new Error("分配失敗");
        }

        return await response.json();
    } catch (error) {
        console.error("錯誤: ", error);
        throw error;
    }
};