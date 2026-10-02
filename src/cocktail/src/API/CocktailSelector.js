import {request} from "./request.js";

/**
 * @param selector 心情／味道／色調／醉相 {mood, taste, tone, drunk}
 */
export const getCocktailSelector = (selector) =>
    request(`/lastwine/selector?${new URLSearchParams(selector)}`);

/**
 * @param recipeId 酒譜 ID
 */
export const getCocktailDetail = (recipeId) =>
    request(`/lastwine/recipes/${encodeURIComponent(recipeId)}/detail`);
