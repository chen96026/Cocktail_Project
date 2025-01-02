export const registMember = async (formData) => {

    try {
        const response = await fetch(`/lastwine/regist`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(formData),
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status:${response.status}`);
        }
        const data = await response.json(); // 解析回應
        if (data.token) {
            localStorage.setItem("token", data.token); // 儲存 token
        }
        return data; // 返回解析後的資料
    } catch (error) {
        console.error("錯誤: ", error);
        throw error;
    }
};