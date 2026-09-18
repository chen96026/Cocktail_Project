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
                const recipe = await findByRecipeId(recipe_id);
                setInitialValues({
                    enTitle: recipe.enTitle,
                    zhTitle: recipe.zhTitle,
                    method: recipe.method,
                    baseWines: recipe.baseWines.map((wine) => wine.name),
                    materials: recipe.materials.map((material) => ({
                        materialName: material.materialName,
                        materialQuantity: material.materialQuantity,
                    })),
                });
            } catch (error) {
                console.error("載入酒譜失敗：", error);
                Swal.fire("載入失敗", "無法載入調酒資料", "error");
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
            Swal.fire("更新失敗", "請稍後再試", "error");
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
