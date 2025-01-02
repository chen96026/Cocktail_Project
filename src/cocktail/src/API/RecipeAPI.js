export const addRecipe = async (formData) => {
    try {
        const response = await fetch(`/lastwine/addRecipe`, {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer <your-token>',
            },
            body: formData,
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status:${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.log("錯誤: ", error)
        throw error;
    }
}

export const updatedRecipe = async (recipe_id, updatedData) => {
    try {
        // 從 localStorage 獲取 Token
        const token = localStorage.getItem("token");
        if (!token) {
            throw new Error("尚未登入");
        }
        const response = await fetch(`/lastwine/updateRecipe/${recipe_id}`, {
            method: 'PUT',
            headers: {
                Authorization: `Bearer ${token}` // 添加 Authorization 標頭
            },
            body: updatedData
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status:${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.log("錯誤: ", error)
        throw error;
    }
}

export const deletedRecipe = async (recipe_id) => {
    try {
        // 從 localStorage 獲取 Token
        const token = localStorage.getItem("token");
        if (!token) {
            throw new Error("未登入或授權 Token 丟失");
        }
        const response = await fetch(`/lastwine/deleteRecipe/${recipe_id}`, {
            method: 'DELETE',
            headers: {
                Authorization: `Bearer ${token}`, // 添加 Authorization 標頭
            },
        });
        if (!response.ok) {
            const errorResponse = await response.text(); // 解析錯誤訊息
            throw new Error(`HTTP error! status:${response.status}`)
        }
        return await response.text();
    } catch (error) {
        console.log("刪除酒譜失敗：", error.message);
        throw error;
    }
}

export const findByRecipeId = async (recipe_id) => {
    try {
        const response = await fetch(`/lastwine/findRecipeId/${recipe_id}`, {
            method: 'GET',
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status:${response.status}`)
        }
        return await response.json();
    } catch (error) {
        console.log("錯誤: ", error);
        throw error;
    }
}



