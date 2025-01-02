import React, {useState, useEffect} from "react";
import {
    addCombination,
    getAllTheCombinations,
    getAllRecipeCombinations,
    assignCombinations,
} from "../API/Admin.js";

const Admin = () => {
    const [recipes, setRecipes] = useState([]); // 調酒主表格數據
    const [loading, setLoading] = useState(false); // 加載狀態
    const [error, setError] = useState(null);
    const [combinations, setCombinations] = useState([]); // 初始化空數組
    const [assignments, setAssignments] = useState([]); // 儲存分配結果
    const [newCombination, setNewCombination] = useState({
        mood: "",
        taste: "",
        tone: "",
        drunk: "",
    });

    const GetAllTheCombinations = async () => {
        setLoading(true);
        setError(null);
        try {
            const data = await getAllTheCombinations();
            const uniqueCombinations = data.filter(
                (comb, index, self) =>
                    index ===
                    self.findIndex(
                        (c) =>
                            c.mood === comb.mood &&//c.mood：來自 self 陣列中的某個元素，comb.mood：來自當前正在處理的 comb 對象
                            c.taste === comb.taste &&
                            c.tone === comb.tone &&
                            c.drunk === comb.drunk
                    )
            );
            setCombinations(uniqueCombinations);
        } catch (error) {
            console.error("獲取所有組合失敗: ", error);
            setError("無法獲取組合數據，請稍後重試。");
        } finally {
            setLoading(false);
        }
    };

    const GetAllRecipeCombinations = async () => {
        setLoading(true);
        setError(null);
        try {
            const data = await getAllRecipeCombinations();
            setRecipes(data);
        } catch (err) {
            setError("無法獲取調酒數據，請稍後重試。");
        } finally {
            setLoading(false);
        }
    };

    const AddCombination = async () => {
        try {
            await addCombination(newCombination);
            setNewCombination({mood: "", taste: "", tone: "", drunk: ""});
            GetAllTheCombinations();
        } catch (error) {
            console.error("新增組合失敗: ", error);
        }
    };

    //根據表單輸入的變化更新對象的狀態
    const handleNewCombinationChange = (e) => {
        const {name, value} = e.target;
        setNewCombination((prev) => ({...prev, [name]: value}));
    };

    const handleAssignmentChange = (recipeId, combinationId) => {
        if (!recipeId || !combinationId) {
            return; // 防止無效的數據進入狀態
        }
        setAssignments((prevAssignments) => {
            const updatedAssignments = [...prevAssignments];//展開舊的狀態
            const index = updatedAssignments.findIndex((a) => a.recipeId === recipeId);//使用 findIndex 查找分配中是否已經有該 recipeId
            //findIndex 方法中，返回 -1 表示「沒有找到符合條件的項目」，當 index !== -1 時，表示找到 recipeId，更新該項目的 combinationId
            if (index !== -1) {
                // 更新現有分配
                updatedAssignments[index].combinationId = parseInt(combinationId, 10); // 強制轉換為數字
            } else {
                // 新增分配
                updatedAssignments.push({recipeId, combinationId: parseInt(combinationId, 10)}); // 強制轉換為數字
            }
            return updatedAssignments;
        });
    };

    const renderCombinationOptions = () => {
        return combinations
            .filter((combination) => !recipes.some((r) => r.currentCombination?.combinationId === combination.combinationId))
            .map((combination) => (
                <option
                    key={combination.combinationId}
                    value={combination.combinationId}
                >
                    {combination.mood} - {combination.taste} - {combination.tone} - {combination.drunk}
                </option>
            ));
    };

    const submitAssignments = async () => {
        try {
            const payload = assignments.map(({recipeId, combinationId}) => ({
                fkRecipeId: {recipe_id: recipeId}, // 包裝成對象，符合後端需求
                fkCombinationId: {combinationId: combinationId}, // 包裝成對象，符合後端需求
            }));
            // 使用批量 API 提交整個 payload
            await assignCombinations(payload);
            // 清空 assignments 並刷新數據
            setAssignments([]);
            GetAllRecipeCombinations();
        } catch (error) {
            console.error("提交分配時發生錯誤: ", error);
        }
    };

    useEffect(() => {
        GetAllTheCombinations(); // 獲取所有組合
        GetAllRecipeCombinations(); // 獲取所有調酒與組合
    }, []);

    useEffect(() => {
        console.log("獲取的調酒數據: ", recipes); // 檢查 recipes 結構
    }, [recipes]);

    return (
        <div className="admin-page">
            <h1 className="admin-pageH1">後台管理系統</h1>
            {loading && <p>數據加載中...</p>}
            {error && <p className="error">{error}</p>}
            {/* 新增組合的表格 */}
            <section className="admin-new-combination-section">
                <h2>新增組合</h2>
                <form
                    className="admin-new-combination-form"
                    onSubmit={(e) => {
                        e.preventDefault();
                        AddCombination();
                    }}
                >
                    <section>
                    <div>
                        <label htmlFor="mood">心情：</label>
                        <select
                            id="mood"
                            name="mood"
                            value={newCombination.mood}
                            onChange={handleNewCombinationChange}
                            required
                        >
                            <option value="" disabled>
                                選擇心情
                            </option>
                            <option value="愉悅">愉悅</option>
                            <option value="普通">普通</option>
                            <option value="煩悶">煩悶</option>
                            <option value="沉重">沉重</option>
                        </select>
                    </div>
                    <div>
                        <label htmlFor="taste">味道：</label>
                        <select
                            id="taste"
                            name="taste"
                            value={newCombination.taste}
                            onChange={handleNewCombinationChange}
                            required
                        >
                            <option value="" disabled>
                                選擇味道
                            </option>
                            <option value="酸">酸</option>
                            <option value="甜">甜</option>
                            <option value="苦">苦</option>
                        </select>
                    </div>
                    <div>
                        <label htmlFor="tone">色調：</label>
                        <select
                            id="tone"
                            name="tone"
                            value={newCombination.tone}
                            onChange={handleNewCombinationChange}
                            required
                        >
                            <option value="" disabled>
                                選擇色調
                            </option>
                            <option value="冷">冷</option>
                            <option value="暖">暖</option>
                        </select>
                    </div>
                    <div>
                        <label htmlFor="drunk">醉相：</label>
                        <select
                            id="drunk"
                            name="drunk"
                            value={newCombination.drunk}
                            onChange={handleNewCombinationChange}
                            required
                        >
                            <option value="" disabled>
                                選擇醉相
                            </option>
                            <option value="微酣">微酣</option>
                            <option value="酩酊大醉">酩酊大醉</option>
                        </select>
                    </div>
                    </section>
                    <button
                        type="submit"
                    >
                        新增組合
                    </button>
                </form>
            </section>

            {/* 調酒與組合主表格 */}
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
                                    {recipe.combinationId ? (
                                        `${recipe.mood || ""} - ${recipe.taste || ""} - ${recipe.tone || ""} - ${recipe.drunk || ""}`
                                    ) : (
                                        "尚未分配"
                                    )}
                                </td>
                                <td>
                                    <select
                                        onChange={(e) => {
                                            handleAssignmentChange(recipe.recipeId, e.target.value);
                                        }}
                                    >
                                        <option value="" disabled>
                                            選擇組合
                                        </option>
                                        {renderCombinationOptions()}
                                    </select>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                ) : (
                    <p>目前沒有調酒數據。</p>
                )}
            <button className="admin-button"
                onClick={async () => {
                    try {
                        await submitAssignments();
                    } catch (error) {
                        console.error("提交分配時發生錯誤: ", error);
                    }
                }}
            >
                確定分配
            </button>
            </section>
        </div>
    );
};

export default Admin;
