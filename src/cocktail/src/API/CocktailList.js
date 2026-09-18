import {request} from "./request.js";

/**
 * @param baseWines 基酒名稱陣列
 */
export const findRecipeByBaseWine = (baseWines) =>
    request(`/lastwine/getRecipesByBaseWine?baseWine=${encodeURIComponent(baseWines.join(","))}`);

export const findAllRecipe = async () => {
    const data = await request("/lastwine/getAllRecipe");
    if (!Array.isArray(data)) {
        console.error("後端回傳錯誤，不是陣列");
        return [];
    }
    return data;
};

/**
 * @param keyword 中英文名稱關鍵字
 */
export const searchRecipeByKeyword = (keyword) =>
    request(`/lastwine/search?keyword=${encodeURIComponent(keyword)}`);
