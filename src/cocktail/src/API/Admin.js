import {request, requestJson} from "./request.js";

/**
 * @param newCombination 要新增的四維度組合
 */
export const addCombination = (newCombination) =>
    requestJson("/lastwine/addFourCombination", newCombination);

export const getAllTheCombinations = () => request("/lastwine/getAllCombinations");

export const getAllRecipeCombinations = () => request("/lastwine/allCombinations");

/**
 * @param payload 調酒與組合的對應陣列
 */
export const assignCombinations = (payload) =>
    requestJson("/lastwine/assignCombinations", payload);
