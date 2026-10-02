// 後台分配組合用：算出每組組合「送出後」會被哪些調酒使用，一組組合只能分配給一杯酒

/**
 * 以目前已存的組合，加上尚未送出的分配，算出每組組合最後的使用者
 *
 * @param recipes     調酒與目前組合，[{recipeId, zhTitle, combinationId}]
 * @param assignments 尚未送出的分配，[{recipeId, combinationId}]
 * @returns Map，key 是組合 ID，value 是會使用這組組合的調酒陣列
 */
export const combinationOwners = (recipes, assignments) => {
    const pending = new Map(assignments.map((a) => [a.recipeId, a.combinationId]));
    const owners = new Map();
    recipes.forEach((recipe) => {
        const combinationId = pending.get(recipe.recipeId) ?? recipe.combinationId;
        if (combinationId != null) {
            owners.set(combinationId, [...(owners.get(combinationId) ?? []), recipe]);
        }
    });
    return owners;
};

/**
 * 只看這批有動到的組合，原本就重複的舊資料不擋，跟後端的檢查範圍一致
 *
 * @param recipes     調酒與目前組合
 * @param assignments 尚未送出的分配
 * @returns 會被分配給多杯酒的組合，每一項是共用那組組合的調酒陣列
 */
export const findSharedCombinations = (recipes, assignments) => {
    const touched = new Set(assignments.map((a) => a.combinationId));
    return [...combinationOwners(recipes, assignments)]
        .filter(([combinationId, owners]) => touched.has(combinationId) && owners.length > 1)
        .map(([, owners]) => owners);
};
