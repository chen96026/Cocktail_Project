import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import Swal from "sweetalert2";
import RecipeForm from "./RecipeForm.jsx";
import {findByRecipeId, updatedRecipe} from "../API/RecipeAPI.js";

const EditRecipeForm = () => {
    const {recipe_id} = useParams();
    const [initialValues, setInitialValues] = useState(null);

    // 載入酒譜原本的內容
    useEffect(() => {
        const fetchRecipeData = async () => {
            try {
                // 後端回 RecipeDTO：baseWines 是基酒名稱字串陣列，materials 是 {materialName, materialQuantity}
                const recipe = await findByRecipeId(recipe_id);
                setInitialValues({
                    enTitle: recipe.enTitle,
                    zhTitle: recipe.zhTitle,
                    method: recipe.method,
                    baseWines: recipe.baseWines,
                    materials: recipe.materials.map(({materialName, materialQuantity}) => ({
                        materialName,
                        materialQuantity,
                    })),
                });
            } catch (error) {
                console.error("載入酒譜失敗：", error);
                // 顯示後端的錯誤訊息，例如 404 時是「找不到該酒譜」
                Swal.fire("載入失敗", error.message, "error");
            }
        };
        fetchRecipeData();
    }, [recipe_id]);

    const handleSubmit = async (recipe, image) => {
        try {
            Swal.fire({
                title: "更新中...",
                allowOutsideClick: false,
                didOpen: () => Swal.showLoading(),
            });
            await updatedRecipe(recipe_id, recipe, image);
            Swal.fire("更新成功", "", "success");
        } catch (error) {
            console.error("更新酒譜失敗：", error);
            Swal.fire("更新失敗", error.message, "error");
            throw error;
        }
    };

    return (
        <RecipeForm
            heading="編輯酒譜"
            submitLabel="更新酒譜"
            classPrefix="Edit"
            initialValues={initialValues}
            onSubmit={handleSubmit}
        />
    );
};

export default EditRecipeForm;
