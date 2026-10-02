import {request} from "./request.js";

/**
 * 組出新增／更新酒譜需要的 multipart 內容
 * recipe part 走 JSON，圖片另走 image part
 *
 * @param recipe 酒譜內容（enTitle / zhTitle / method / baseWines / materials）
 * @param image  圖片檔，未選擇則不帶
 */
const toRecipeFormData = (recipe, image) => {
    const formData = new FormData();
    formData.append("recipe", new Blob([JSON.stringify(recipe)], {type: "application/json"}));
    if (image) {
        formData.append("image", image);
    }
    return formData;
};

/**
 * @param recipe_id 酒譜 ID，來自網址參數，先編碼避免組出別的路徑
 */
const recipeUrl = (recipe_id) => `/lastwine/recipes/${encodeURIComponent(recipe_id)}`;

// 成功時後端回 201，request 一樣當成功處理
export const addRecipe = (recipe, image) =>
    request("/lastwine/recipes", {method: "POST", body: toRecipeFormData(recipe, image)});

/**
 * @param recipe_id 酒譜 ID
 */
export const updatedRecipe = (recipe_id, recipe, image) =>
    request(recipeUrl(recipe_id), {
        method: "PUT",
        body: toRecipeFormData(recipe, image),
    });

export const deletedRecipe = (recipe_id) => request(recipeUrl(recipe_id), {method: "DELETE"});

export const findByRecipeId = (recipe_id) => request(recipeUrl(recipe_id));
