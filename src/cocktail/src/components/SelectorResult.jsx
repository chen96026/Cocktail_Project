import RecipeDetailModal from "./RecipeDetailModal.jsx";

/**
 * 篩選器的結果展示
 *
 * @param result      篩選出的調酒
 * @param detail      詳細資料，未載入則為 null
 * @param onOpenDetail 點「詳細介紹」時觸發
 * @param onCloseDetail 關閉詳細介紹
 * @param onReset     重新選擇
 * @param onClose     關閉結果
 */
const SelectorResult = ({result, detail, onOpenDetail, onCloseDetail, onReset, onClose}) => {
    return (
        <section className="background-sqareImage">
            <div id="print">
                <p>{result.enTitle}</p>
                <p>({result.zhTitle})</p>
            </div>
            <section>
                {result.image && <img id="printImg" src={result.image} alt={result.enTitle}/>}
            </section>
            <div id="print2">
                <p id="print2" onClick={() => onOpenDetail(result.recipeId)}>詳細介紹</p>
            </div>
            <RecipeDetailModal isOpen={Boolean(detail)} onClose={onCloseDetail} recipe={detail}/>
            <div id="newcloseButton">
                <button onClick={onReset} className="cocktail_button" id="onemoreButton">重新選擇</button>
                <button onClick={onClose} className="cocktail_button" id="closeButton">Close</button>
            </div>
        </section>
    );
};

export default SelectorResult;
