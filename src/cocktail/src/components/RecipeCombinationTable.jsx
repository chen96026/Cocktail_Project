/**
 * 後台的調酒與組合對應表格
 *
 * @param recipes      調酒與目前組合
 * @param combinations 可選的四維度組合
 * @param onAssign     選擇組合時觸發，接到 (recipeId, combinationId)
 * @param onSubmit     按下確定分配時觸發
 */
const RecipeCombinationTable = ({recipes, combinations, onAssign, onSubmit}) => {

    const describe = (combination) =>
        `${combination.mood} - ${combination.taste} - ${combination.tone} - ${combination.drunk}`;

    return (
        <section className="admin-recipe-combinations">
            <h2>調酒與組合</h2>
            {recipes.length > 0 ? (
                <table>
                    <thead>
                    <tr>
                        <th>調酒名稱</th>
                        <th>目前的組合</th>
                        <th>選擇組合</th>
                    </tr>
                    </thead>
                    <tbody>
                    {recipes.map((recipe) => (
                        <tr key={recipe.recipeId}>
                            <td>
                                {recipe.zhTitle} ({recipe.enTitle})
                            </td>
                            <td>
                                {recipe.combinationId ? describe(recipe) : "尚未分配"}
                            </td>
                            <td>
                                <select
                                    defaultValue=""
                                    onChange={(e) => onAssign(recipe.recipeId, e.target.value)}
                                >
                                    <option value="" disabled>
                                        選擇組合
                                    </option>
                                    {combinations.map((combination) => (
                                        <option
                                            key={combination.combinationId}
                                            value={combination.combinationId}
                                        >
                                            {describe(combination)}
                                        </option>
                                    ))}
                                </select>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            ) : (
                <p>目前沒有調酒數據。</p>
            )}
            <button className="admin-button" onClick={onSubmit}>
                確定分配
            </button>
        </section>
    );
};

export default RecipeCombinationTable;
