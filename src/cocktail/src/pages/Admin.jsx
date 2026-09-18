import {useCallback, useEffect, useState} from "react";
import {
    addCombination,
    getAllTheCombinations,
    getAllRecipeCombinations,
    assignCombinations,
} from "../API/Admin.js";
import CombinationForm from "../components/CombinationForm.jsx";
import RecipeCombinationTable from "../components/RecipeCombinationTable.jsx";

// 同樣的四維度組合只留一筆
const distinctCombinations = (data) => data.filter(
    (comb, index, self) =>
        index === self.findIndex(
            (c) => c.mood === comb.mood && c.taste === comb.taste
                && c.tone === comb.tone && c.drunk === comb.drunk
        )
);

const Admin = () => {
    const [recipes, setRecipes] = useState([]); // 調酒主表格數據
    const [combinations, setCombinations] = useState([]);
    const [assignments, setAssignments] = useState([]); // 尚未送出的分配結果
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const loadCombinations = useCallback(async () => {
        try {
            setCombinations(distinctCombinations(await getAllTheCombinations()));
        } catch (err) {
            console.error("獲取所有組合失敗: ", err);
            setError("無法獲取組合數據，請稍後重試。");
        }
    }, []);

    const loadRecipeCombinations = useCallback(async () => {
        try {
            setRecipes(await getAllRecipeCombinations());
        } catch (err) {
            console.error("獲取調酒與組合失敗: ", err);
            setError("無法獲取調酒數據，請稍後重試。");
        }
    }, []);

    useEffect(() => {
        const load = async () => {
            setLoading(true);
            setError(null);
            await Promise.all([loadCombinations(), loadRecipeCombinations()]);
            setLoading(false);
        };
        load();
    }, [loadCombinations, loadRecipeCombinations]);

    const handleAddCombination = async (newCombination) => {
        try {
            await addCombination(newCombination);
            await loadCombinations();
        } catch (err) {
            console.error("新增組合失敗: ", err);
            setError("新增組合失敗，請稍後重試。");
        }
    };

    const handleAssignmentChange = (recipeId, combinationId) => {
        if (!recipeId || !combinationId) {
            return; // 防止無效的數據進入狀態
        }
        const assignment = {recipeId, combinationId: parseInt(combinationId, 10)};
        setAssignments((prev) => {
            const exists = prev.some((a) => a.recipeId === recipeId);
            // 同一杯調酒只保留最後一次選擇
            return exists
                ? prev.map((a) => (a.recipeId === recipeId ? assignment : a))
                : [...prev, assignment];
        });
    };

    const submitAssignments = async () => {
        try {
            // 包裝成後端需要的結構
            const payload = assignments.map(({recipeId, combinationId}) => ({
                fkRecipeId: {recipe_id: recipeId},
                fkCombinationId: {combinationId},
            }));
            await assignCombinations(payload);
            setAssignments([]);
            await loadRecipeCombinations();
        } catch (err) {
            console.error("提交分配時發生錯誤: ", err);
            setError("分配失敗，請稍後重試。");
        }
    };

    return (
        <div className="admin-page">
            <h1 className="admin-pageH1">後台管理系統</h1>
            {loading && <p>數據加載中...</p>}
            {error && <p className="error">{error}</p>}
            <CombinationForm onSubmit={handleAddCombination}/>
            <RecipeCombinationTable
                recipes={recipes}
                combinations={combinations}
                onAssign={handleAssignmentChange}
                onSubmit={submitAssignments}
            />
        </div>
    );
};

export default Admin;
