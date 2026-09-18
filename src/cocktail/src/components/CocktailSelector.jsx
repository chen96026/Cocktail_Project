import {useState} from "react";
import Swal from "sweetalert2";
import SelectorForm from "./SelectorForm.jsx";
import SelectorResult from "./SelectorResult.jsx";
import {getCocktailSelector, getCocktailDetail} from "../API/CocktailSelector.js";

const EMPTY_SELECTION = {mood: null, taste: null, tone: null, drunk: null};

const CocktailSelector = () => {

    const [showForm, setShowForm] = useState(false);
    const [selection, setSelection] = useState(EMPTY_SELECTION);
    const [result, setResult] = useState(null);
    const [detail, setDetail] = useState(null);

    const handleChange = (key, option) => setSelection((prev) => ({...prev, [key]: option}));

    const handleSelect = async () => {
        if (Object.values(selection).some((option) => !option)) {
            Swal.fire({
                icon: "warning",
                title: "請挑選所有選項！",
                confirmButtonText: "確定",
            });
            return;
        }
        try {
            Swal.fire({
                title: "載入中...",
                text: "正在為您尋找最適合的調酒",
                allowOutsideClick: false,
                didOpen: () => Swal.showLoading(),
            });
            //打取得選擇的調酒的API，把選到的 option 攤成後端要的四個字串
            const cocktails = await getCocktailSelector({
                mood: selection.mood.value,
                taste: selection.taste.value,
                tone: selection.tone.value,
                drunk: selection.drunk.value,
            });
            Swal.close();
            if (cocktails.length === 0) {
                Swal.fire({
                    icon: "error",
                    title: "未找到匹配的調酒",
                    confirmButtonText: "確定",
                });
                return;
            }
            setResult(cocktails[0]);
            //隱藏選擇表單
            setShowForm(false);
        } catch (error) {
            console.error("調用 API 失敗: ", error);
            Swal.fire({
                icon: "error",
                title: "系統錯誤，請稍後再試",
                confirmButtonText: "確定",
            });
        }
    };

    const handleReset = () => {
        setSelection(EMPTY_SELECTION);
        setResult(null);
        setShowForm(true);
    };

    const handleClose = () => {
        setShowForm(false);
        setResult(null);
    };

    const openDetail = async (recipeId) => {
        try {
            Swal.fire({
                title: "載入中...",
                text: "正在載入詳細資料",
                allowOutsideClick: false,
                didOpen: () => Swal.showLoading(),
            });
            //打取得詳細資料的API
            setDetail(await getCocktailDetail(recipeId));
            Swal.close();
        } catch (error) {
            console.error("取得詳細資料失敗: ", error);
            Swal.close();
            Swal.fire({
                icon: "error",
                title: "無法獲取詳細資料",
                confirmButtonText: "確定",
            });
        }
    };

    return (
        <main id="allContent">
            <h3 id="middleTitle">幫我決定調酒</h3>
            <section id="background-bigSqare">
                {!showForm && !result && (
                    <div id="background-recommandWord">
                        <h2>五秒找出最適合的調酒!</h2>
                        <div id="recommandWord">
                            <p>對酒當歌，人生幾何。</p>
                            <p>譬如朝露，去日苦多。</p>
                            <p>慨當以慷，憂思難忘。</p>
                            <p>何以解憂，惟有杜康。</p>
                        </div>
                        <button onClick={() => setShowForm(true)} className="cocktail_button">Get Start</button>
                    </div>
                )}

                {showForm && (
                    <SelectorForm
                        selection={selection}
                        onChange={handleChange}
                        onSubmit={handleSelect}
                        onClose={handleClose}
                    />
                )}

                {result && (
                    <SelectorResult
                        result={result}
                        detail={detail}
                        onOpenDetail={openDetail}
                        onCloseDetail={() => setDetail(null)}
                        onReset={handleReset}
                        onClose={handleClose}
                    />
                )}
            </section>
        </main>
    );
};

export default CocktailSelector;
