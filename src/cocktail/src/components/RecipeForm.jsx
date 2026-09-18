import {useEffect, useState} from "react";
import Swal from "sweetalert2";
import {BASE_WINES} from "../constants/options.js";

const emptyMaterial = () => ({material_name: "", material_quantity: ""});

/**
 * 新增與編輯酒譜共用的表單
 *
 * @param heading        標題文字
 * @param submitLabel    送出按鈕文字
 * @param classPrefix    樣式前綴（Add / Edit）
 * @param initialValues  編輯時帶入的初始值
 * @param imageRequired  圖片是否必填（新增必填、編輯可留空表示不換圖）
 * @param resetOnSuccess 送出成功後是否清空表單
 * @param onSubmit       送出處理，接到 (recipe, image)
 */
const RecipeForm = ({
                        heading,
                        submitLabel,
                        classPrefix,
                        initialValues,
                        imageRequired = false,
                        resetOnSuccess = false,
                        onSubmit,
                    }) => {

    const [enTitle, setEnTitle] = useState("");
    const [zhTitle, setZhTitle] = useState("");
    const [baseWines, setBaseWines] = useState([]);
    const [method, setMethod] = useState("");
    const [image, setImage] = useState(null);
    const [materials, setMaterials] = useState([emptyMaterial(), emptyMaterial(), emptyMaterial()]);

    // 編輯時資料是非同步載入的，到了才填進表單
    useEffect(() => {
        if (!initialValues) {
            return;
        }
        setEnTitle(initialValues.enTitle || "");
        setZhTitle(initialValues.zhTitle || "");
        setBaseWines(initialValues.baseWines || []);
        setMethod(initialValues.method || "");
        setMaterials(initialValues.materials?.length ? initialValues.materials : [emptyMaterial()]);
    }, [initialValues]);

    const handleBaseWineChange = (event) => {
        const {value, checked} = event.target;
        setBaseWines((prev) => checked ? [...prev, value] : prev.filter((wine) => wine !== value));
    };

    const handleMaterialChange = (index, field, value) => {
        setMaterials((prev) => prev.map((material, i) =>
            i === index ? {...material, [field]: value} : material));
    };

    //新增空的輸入框
    const addMaterialField = () => setMaterials((prev) => [...prev, emptyMaterial()]);

    //移除索引為 index 的元素
    const removeMaterialField = (index) =>
        setMaterials((prev) => prev.filter((_, i) => i !== index));

    const resetForm = () => {
        setEnTitle("");
        setZhTitle("");
        setBaseWines([]);
        setMethod("");
        setImage(null);
        setMaterials([emptyMaterial(), emptyMaterial(), emptyMaterial()]);
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        if (!enTitle || !zhTitle) {
            Swal.fire({title: "標題不能為空！", icon: "error", confirmButtonText: "返回"});
            return;
        }
        const recipe = {
            enTitle,
            zhTitle,
            method,
            baseWines,
            // 過濾沒填名稱的空白列
            materials: materials.filter((material) => material.material_name.trim() !== ""),
        };
        try {
            await onSubmit(recipe, image);
        } catch {
            // 送出失敗時保留使用者填的內容，錯誤提示由呼叫端負責
            return;
        }
        if (resetOnSuccess) {
            resetForm();
        }
    };

    return (
        <main id="RecipeContent">
            <div className="recipe-form-container">
                <h2>{heading}</h2>
                <form onSubmit={handleSubmit}>
                    {/* 標題 */}
                    <div className="form-group">
                        <label htmlFor="enTitle">調酒英文名</label>
                        <input
                            type="text"
                            id="enTitle"
                            value={enTitle}
                            onChange={(e) => setEnTitle(e.target.value)}
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
                            onChange={(e) => setZhTitle(e.target.value)}
                            placeholder="請輸入中文名"
                            required
                        />
                    </div>

                    {/* 勾選基酒 */}
                    <div className="form-group">
                        <label>使用的基酒</label>
                        <div className="checkbox-group">
                            {BASE_WINES.map((wine) => (
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

                    {/* 材料：名稱與份量分開兩格 */}
                    <div className="form-group">
                        <label>材料</label>
                        {materials.map((material, index) => (
                            <div key={index} className="material-input">
                                <input
                                    type="text"
                                    value={material.material_name}
                                    onChange={(e) => handleMaterialChange(index, "material_name", e.target.value)}
                                    placeholder={`材料 ${index + 1}`}//因為陣列從0開始，+1可以讓顯示為材料1、材料2
                                />
                                <input
                                    type="text"
                                    value={material.material_quantity}
                                    onChange={(e) => handleMaterialChange(index, "material_quantity", e.target.value)}
                                    placeholder="份量 (EX:20ml)"
                                />
                                <div className={`button${classPrefix}Delete`} type="button"
                                     onClick={() => removeMaterialField(index)}>
                                    <i className="fa-solid fa-trash"></i>
                                </div>
                            </div>
                        ))}
                        <div className={`button${classPrefix}Plus`} type="button"
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
                               onChange={(e) => setImage(e.target.files[0])}
                               accept="image/*"//限制只能選.jpg、.png、.gif等圖片格式
                               required={imageRequired}
                        />
                    </div>

                    {/* 提交按鈕 */}
                    <button className={`button${classPrefix}Submit`} type="submit">{submitLabel}</button>
                </form>
            </div>
        </main>
    );
};

export default RecipeForm;
