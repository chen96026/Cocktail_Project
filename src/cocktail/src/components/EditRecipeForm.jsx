import React, {useState, useEffect} from "react";
import {useParams} from "react-router-dom";
import {findByRecipeId, updatedRecipe} from "../API/RecipeAPI.js";
import Swal from "sweetalert2";

const EditRecipeForm = () => {
    const {recipe_id} = useParams();
    const [enTitle, setEntitle] = useState("");
    const [zhTitle, setZhtitle] = useState("");
    const [baseWines, setBaseWines] = useState([]);
    const [method, setMethod] = useState("");
    const [image, setImage] = useState("");
    const [materials, setMaterials] = useState([""]);

    // 加載酒譜數據
    useEffect(() => {
        const fetchRecipeData = async () => {
            try {
                const recipeData = await findByRecipeId(recipe_id);
                setEntitle(recipeData.en_title);
                setZhtitle(recipeData.zh_title);
                setBaseWines(recipeData.baseWines.map((wine) => wine.name));
                setMethod(recipeData.method);
                setMaterials(recipeData.materials.map((mat) => `${mat.material_name}:${mat.material_quantity}`));
            } catch (error) {
                Swal.fire("載入失敗", "無法載入調酒資料", "error");
            }
        };
        fetchRecipeData();
    }, [recipe_id]);

    // 處理基酒選擇
    const handleBaseWineChange = (event) => {
        const {value, checked} = event.target;
        if (checked) {
            setBaseWines([...baseWines, value]);
        } else {
            setBaseWines(baseWines.filter((wine) => wine !== value));
        }
    };

    // 處理圖片上傳
    const handleImageChange = (event) => {
        setImage(event.target.files[0]);
    };

    // 處理材料輸入變更
    const handleMaterialChange = (index, value) => {
        const updatedMaterials = [...materials];
        updatedMaterials[index] = value;
        setMaterials(updatedMaterials);
    };

    // 新增材料輸入框
    const addMaterialField = () => {
        setMaterials([...materials, ""]);
    };

    // 刪除材料輸入框
    const removeMaterialField = (index) => {
        const updatedMaterials = materials.filter((_, i) => i !== index);
        setMaterials(updatedMaterials);
    };

    // 提交表單
    const handleSubmit = async (event) => {
        event.preventDefault();

        const updatedData = new FormData();
        updatedData.append("en_title", enTitle);
        updatedData.append("zh_title", zhTitle);
        updatedData.append("method", method);
        if (image) updatedData.append("image", image);
        baseWines.forEach((wine) => updatedData.append("base_wine_list", wine));
        materials
            .filter((mat) => mat.trim() !== "")
            .forEach((mat) => updatedData.append("material", mat));

        try {
            Swal.fire({
                title: "更新中...",
                allowOutsideClick: false,
                didOpen: () => Swal.showLoading(),
            });

            const response = await updatedRecipe(recipe_id, updatedData);

            // 根據回傳的 message 顯示提示
            if (response.message === "更新成功") {
                Swal.fire("更新成功", "", "success");
            } else {
                Swal.fire("更新失敗", response.message, "error");
            }
        } catch (error) {
            Swal.fire("更新失敗", "請稍後再試", "error");
        }
    };

    return (
        <main id="RecipeContent">
            <div className="recipe-form-container">
                <h2>編輯酒譜</h2>
                <form onSubmit={handleSubmit}>
                    {/* 標題 */}
                    <div className="form-group">
                        <label htmlFor="enTitle">調酒英文名</label>
                        <input
                            type="text"
                            id="enTitle"
                            value={enTitle}
                            onChange={(e) => setEntitle(e.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="zhTitle">調酒中文名</label>
                        <input
                            type="text"
                            id="zhTitle"
                            value={zhTitle}
                            onChange={(e) => setZhtitle(e.target.value)}
                            required
                        />
                    </div>

                    {/* 基酒選擇 */}
                    <div className="form-group">
                        <label>使用的基酒</label>
                        <div className="checkbox-group">
                            {[
                                {label: "伏特加 Vodka", value: "Vodka"},
                                {label: "白蘭地 Brandy", value: "Brandy"},
                                {label: "琴酒 Gin", value: "Gin"},
                                {label: "威士忌 Whisky", value: "Whisky"},
                                {label: "龍舌蘭 Tequila", value: "Tequila"},
                                {label: "蘭姆酒 Rum", value: "Rum"},
                                {label: "其他 Others", value: "Others"},
                            ].map((wine) => (
                                <label key={wine.value}>
                                    <input style={{cursor: "pointer"}}
                                           type="checkbox"
                                           value={wine.value}
                                           checked={baseWines.includes(wine.value)}
                                           onChange={handleBaseWineChange}
                                    />
                                    {wine.label}
                                </label>
                            ))}
                        </div>
                    </div>

                    {/* 材料 */}
                    <div className="form-group">
                        <label htmlFor="method">材料 (EX:伏特加:20ml)</label>
                        {materials.map((mat, index) => (
                            <div key={index} className="material-input">
                                <input
                                    type="text"
                                    value={mat}
                                    onChange={(e) => handleMaterialChange(index, e.target.value)}
                                    placeholder={`材料 ${index + 1}`}
                                />
                                <div className="buttonEditDelete" type="button"
                                     onClick={() => removeMaterialField(index)}>
                                    <i className="fa-solid fa-trash"></i>
                                </div>
                            </div>
                        ))}
                        <div className="buttonEditPlus" type="button"
                             onClick={addMaterialField}>
                            <i className="fa-solid fa-circle-plus"></i>
                        </div>
                    </div>

                    {/* 介紹 */}
                    <div className="form-group">
                        <label htmlFor="method">介紹</label>
                        <textarea style={{height: "25vh"}}
                                  id="method"
                                  value={method}
                                  onChange={(e) => setMethod(e.target.value)}
                        />
                    </div>

                    {/* 上傳圖片 */}
                    <div className="form-group">
                        <label htmlFor="image">上傳圖片</label>
                        <input style={{cursor: "pointer"}} type="file" id="image" onChange={handleImageChange}
                               accept="image/*"/>
                    </div>

                    {/* 提交按鈕 */}
                    <button className="buttonEditSubmit" type="submit">更新酒譜</button>
                </form>
            </div>
        </main>
    );
};

export default EditRecipeForm;
