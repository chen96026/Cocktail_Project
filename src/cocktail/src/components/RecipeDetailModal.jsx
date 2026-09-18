import {Fragment} from "react";
import Modal from "./Modal.jsx";

/**
 * 調酒詳細介紹的彈窗，調酒列表與首頁篩選器共用
 *
 * @param isOpen  是否開啟
 * @param onClose 關閉處理
 * @param recipe  調酒資料，未帶則不渲染內容
 */
const RecipeDetailModal = ({isOpen, onClose, recipe}) => {
    return (
        <Modal isOpen={isOpen} onClose={onClose}>
            {recipe && (
                <div className="modalSmallDiv">
                    <img src={recipe.image} alt={recipe.enTitle}/>
                    <h2>{recipe.enTitle} ({recipe.zhTitle})</h2>
                    <section className="modalMaterial">
                        <div className="materialDiv">材料:</div>
                        <section className="materialSection">
                            {recipe.materials.map((material, index) => (
                                <div key={index} className="materialSectionDiv">
                                    {material.materialName}: {material.materialQuantity}
                                </div>
                            ))}
                        </section>
                    </section>
                    <p>
                        {/*\n代表換行，分割成每組元素*/}
                        {recipe.method.split("\n").map((line, index) => (
                            // Fragment 只是一個不會生成額外 HTML 標籤的容器
                            <Fragment key={index}>
                                {line}
                                <br/>
                            </Fragment>
                        ))}
                    </p>
                </div>
            )}
        </Modal>
    );
};

export default RecipeDetailModal;
