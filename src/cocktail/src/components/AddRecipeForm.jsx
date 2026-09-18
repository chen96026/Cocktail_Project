import Swal from "sweetalert2";
import RecipeForm from "./RecipeForm.jsx";
import {addRecipe} from "../API/RecipeAPI.js";

const AddRecipeForm = () => {

    const handleSubmit = async (recipe, image) => {
        try {
            Swal.fire({
                title: "上傳中...",
                text: "請稍候，正在處理您的請求。",
                allowOutsideClick: false,
                didOpen: () => Swal.showLoading(),
            });
            await addRecipe(recipe, image);
            Swal.fire({title: "上傳成功", icon: "success", confirmButtonText: "確定"});
        } catch (error) {
            console.error("上傳失敗的錯誤：", error);
            Swal.fire({
                title: "上傳失敗",
                text: error.message, // 顯示詳細的錯誤訊息
                icon: "error",
                confirmButtonText: "返回",
            });
            // 讓表單知道這次沒成功，不要清空使用者填的內容
            throw error;
        }
    };

    return (
        <RecipeForm
            heading="新增調酒"
            submitLabel="新增酒譜"
            classPrefix="Add"
            imageRequired
            resetOnSuccess
            onSubmit={handleSubmit}
        />
    );
};

export default AddRecipeForm;
