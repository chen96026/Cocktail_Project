import {request} from "./request.js";

/**
 * 組出 addRecipe / updateRecipe 需要的 multipart 內容
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

export const addRecipe = (recipe, image) =>
    request("/lastwine/addRecipe", {method: "POST", body: toRecipeFormData(recipe, image)});

/**
 * @param recipe_id 酒譜 ID
 */
export const updatedRecipe = (recipe_id, recipe, image) =>
    request(`/lastwine/updateRecipe/${recipe_id}`, {
        method: "PUT",
        body: toRecipeFormData(recipe, image),
    });

export const deletedRecipe = (recipe_id) =>
    request(`/lastwine/deleteRecipe/${recipe_id}`, {method: "DELETE"});

export const findByRecipeId = (recipe_id) => request(`/lastwine/findRecipeId/${recipe_id}`);
