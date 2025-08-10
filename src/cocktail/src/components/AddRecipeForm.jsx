import {useState, useEffect} from "react";
import {fetchWithAuth} from "../API/LoginAPI.js";
import Swal from "sweetalert2";

const AddRecipeForm = () => {

    const [enTitle, setEntitle] = useState("");
    const [zhTitle, setZhtitle] = useState("");
    const [baseWines, setBaseWines] = useState([]);
    const [method, setMethod] = useState("");
    const [image, setImage] = useState("");
    const [materials, setInmaterials] = useState(["", "", ""]); // 初始三個輸入框

    const handleBaseWineChange = (event) => {
        const {value, checked} = event.target;
        if (checked) {
            setBaseWines([...baseWines, value]);
        } else {
            setBaseWines(baseWines.filter((wine) => wine !== value));
        }
    };

    const handleImageChange = (event) => {
        setImage(event.target.files[0]);
    };

    const handleMaterialChange = (index, value) => {
        const updatedMaterials = [...materials];
        updatedMaterials[index] = value;
        setInmaterials(updatedMaterials);
    };

    //新增空的輸入框
    const addMaterialField = () => {
        setInmaterials([...materials, ""]);
    };

    //移除索引為 index 的元素
    const removeMaterialField = (index) => {
        const updatedMaterials = materials.filter((_, i) => i !== index);
        setInmaterials(updatedMaterials);
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        if (!enTitle || !zhTitle) {
            Swal.fire({
                title: "標題不能為空！",
                icon: "error",
                confirmButtonText: "返回"
            });
            return;
        }
        // 模擬提交資料
        const formData = new FormData()
        formData.append("en_title", enTitle);
        formData.append("zh_title", zhTitle);
        formData.append("method", method);
        formData.append("image", image);
        baseWines.forEach((wine) => formData.append("base_wine_list", wine))
        materials
            .filter(mat => mat.trim() !== "") // 過濾空字串
            .forEach((mat) => {
                formData.append('material', mat)
            })

        try {
            Swal.fire({
                title: "上傳中...",
                text: "請稍候，正在處理您的請求。",
                allowOutsideClick: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });

            const response = await fetchWithAuth("/lastwine/addRecipe", {
                method: "POST",
                body: formData,
            });

            if (!response.ok) {
                const errorText = await response.text(); // 錯誤時解析文字
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const result = await response.json();
            Swal.fire({
                title: "上傳成功",
                icon: "success",
                confirmButtonText: "確定"
            });
            // 清空表單
            setEntitle("");
            setZhtitle("");
            setBaseWines([]);
            setMethod("");
            setImage("");
            setInmaterials(["", "", ""])
        } catch (error) {
            console.error("上傳失敗的錯誤：", error);
            Swal.fire({
                title: "上傳失敗",
                text: error.message, // 顯示詳細的錯誤訊息
                icon: "error",
                confirmButtonText: "返回"
            })
        }
    };

    return (
        <main id="RecipeContent">
            <div className="recipe-form-container">
                <h2>新增調酒</h2>
                <form onSubmit={handleSubmit}>
                    {/* 標題 */}
                    <div className="form-group">
                        <label htmlFor="enTitle">調酒英文名</label>
                        <input
                            type="text"
                            id="enTitle"
                            value={enTitle}
                            onChange={(e) => setEntitle(e.target.value)}
                            placeholder="請輸入英文名"
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
                            placeholder="請輸入中文名"
                            required
                        />
                    </div>

                    {/* 勾選基酒 */}
                    <div className="form-group">
                        <label>使用的基酒</label>
                        <div className="checkbox-group">
                            {/*
                            label：用於顯示在前端界面上的名稱，例如「伏特加 Vodka」
                            value：用於標識基酒的值，會提交到後端，並可能存儲到資料庫中
                            */}
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
                                    placeholder={`材料 ${index + 1}`}//因為陣列從0開始，+1可以讓顯示為材料1、材料2
                                    required
                                />
                                <div className="buttonAddDelete" type="button"
                                     onClick={() => removeMaterialField(index)}>
                                    <i className="fa-solid fa-trash"></i>
                                </div>
                            </div>
                        ))}
                        <div className="buttonAddPlus" type="button"
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
                                  required
                        />
                    </div>

                    {/* 上傳圖片 */}
                    <div className="form-group">
                        <label htmlFor="image">上傳圖片</label>
                        <input style={{cursor: "pointer"}}
                               type="file"
                               id="image"
                               onChange={handleImageChange}
                               accept="image/*"//限制只能選.jpg、.png、.gif等圖片格式
                               required
                        />
                    </div>

                    {/* 提交按鈕 */}
                    <button className="buttonAddSubmit" type="submit">新增酒譜</button>
                </form>
            </div>
        </main>
    );
};

export default AddRecipeForm;
