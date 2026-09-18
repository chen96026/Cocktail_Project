import {request, requestJson} from "./request.js";

/**
 * @param selector 心情／味道／色調／醉相
 */
export const getCocktailSelector = (selector) =>
    requestJson("/lastwine/getCocktailSelector", selector);

/**
 * @param recipeId 酒譜 ID
 */
export const getCocktailDetail = (recipeId) =>
    request(`/lastwine/getCocktailDetail/${recipeId}`);
