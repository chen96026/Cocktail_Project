import {request} from "./request.js";

/**
 * 酒譜列表，基酒與關鍵字一起送，後端兩個條件取交集
 * 基酒要全部符合；空陣列或含 All 表示不篩基酒，空白關鍵字表示不篩關鍵字
 *
 * @param baseWines 已勾選的基酒名稱陣列
 * @param keyword   中英文名稱關鍵字
 */
export const findRecipes = ({baseWines = [], keyword = ""} = {}) => {
    const params = new URLSearchParams();
    baseWines.forEach((baseWine) => params.append("baseWine", baseWine));
    if (keyword.trim()) {
        params.append("keyword", keyword.trim());
    }
    const query = params.toString();
    // 後端固定回陣列；失敗時 request 會直接丟錯，由呼叫端處理
    return request(query ? `/lastwine/recipes?${query}` : "/lastwine/recipes");
};
