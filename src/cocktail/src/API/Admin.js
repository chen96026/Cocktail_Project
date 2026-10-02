import {request, requestJson} from "./request.js";

/**
 * 成功時後端回 201，重複的組合回 400
 *
 * @param newCombination 要新增的四維度組合
 */
export const addCombination = (newCombination) =>
    requestJson("/lastwine/combinations", newCombination);

export const getAllTheCombinations = () => request("/lastwine/combinations");

export const getAllRecipeCombinations = () => request("/lastwine/recipe-combinations");

/**
 * 只更新有帶到的調酒，所以用 PATCH
 *
 * @param payload 調酒與組合的對應陣列 [{recipeId, combinationId}]，任一 ID 缺漏後端回 400
 */
export const assignCombinations = (payload) =>
    requestJson("/lastwine/recipe-combinations", payload, {method: "PATCH"});
