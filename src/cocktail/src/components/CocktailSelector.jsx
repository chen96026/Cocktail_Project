import React, {useState} from "react";
import Select from "react-select";
import Swal from "sweetalert2";
import Modal from "../components/Modal";
import {getCocktailSelector, getCocktailDetail} from "../API/CocktailSelector.js"

const CocktailSelector = ({
                              feelingOption,
                              tastingOption,
                              toneingOption,
                              drunkingOption,
                              customStyles
                          }) => {

    const [showForm, setshowForm] = useState(false);
    const [showImage, setshowImage] = useState(false);
    const [feeling, setFeeling] = useState(null);
    const [tasting, setTasting] = useState(null);
    const [toneing, setToneing] = useState(null);
    const [drunking, setDrunking] = useState(null);
    const [result, setResult] = useState({name: '', image: '', link: ''});
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [modalData, setModalData] = useState(null);

    const handleSelect = async () => {
        if (!feeling || !tasting || !toneing || !drunking) {
            Swal.fire({
                icon: "warning",
                title: "請挑選所有選項！",
                confirmButtonText: "確定",
            });
            return;
        }
        // 組裝選擇的參數
        const selector = {
            mood: feeling.value,
            taste: tasting.value,
            tone: toneing.value,
            drunk: drunking.value,
        };
        try {
            Swal.fire({
                title: "載入中...",
                text: "正在為您尋找最適合的調酒",
                allowOutsideClick: false,
                didOpen: () => Swal.showLoading(),
            });
            //打取得選擇的調酒的API
            const cocktails = await getCocktailSelector(selector);
            Swal.close();
            console.log("返回的調酒數據: ", cocktails);
            if (cocktails.length > 0) {
                setResult(cocktails[0]);
                //隱藏選擇表單
                setshowForm(false);
                setshowImage(true);
            } else {
                Swal.fire({
                    icon: "error",
                    title: "未找到匹配的調酒",
                    confirmButtonText: "確定",
                });
            }
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
        setFeeling(null);
        setTasting(null);
        setToneing(null);
        setDrunking(null);
        setResult({name: '', image: '', link: ''});
        setshowForm(true);
        setshowImage(false);
    };
    const handleClose = () => {
        setshowForm(false);
        setshowImage(false);
    };
    const openModal = async (recipeId) => {
        try {
            Swal.fire({
                title: "載入中...",
                text: "正在載入詳細資料",
                allowOutsideClick: false,
                didOpen: () => Swal.showLoading(),
            });
            //打取得詳細資料的API
            const detail = await getCocktailDetail(recipeId);
            Swal.close();
            console.log("詳細的回應數據:", detail)
            setModalData(detail);
            setIsModalOpen(true);
        } catch (error) {
            Swal.close();
            Swal.fire({
                icon: "error",
                title: "無法獲取詳細資料",
                confirmButtonText: "確定",
            });
        }
    };
    const closeModal = () => {
        setIsModalOpen(false);
        setModalData(null);
    };

    return (
        <main id="allContent">
            <h3 id="middleTitle">幫我決定調酒</h3>
            <section id="background-bigSqare">
                {!showForm && !showImage && (
                    <div id="background-recommandWord">
                        <h2>五秒找出最適合的調酒!</h2>
                        <div id="recommandWord">
                            <p>對酒當歌，人生幾何。</p>
                            <p>譬如朝露，去日苦多。</p>
                            <p>慨當以慷，憂思難忘。</p>
                            <p>何以解憂，惟有杜康。</p>
                        </div>
                        <button onClick={() => setshowForm(true)} className="cocktail_button">Get Start</button>
                    </div>)}

                {showForm && (
                    <section id="background-sqareForm">
                        <div className="closeButton"><i onClick={handleClose} className="fa-solid fa-xmark"></i></div>
                        <section id="background-sqareTop">
                            <div id="feel">
                                <p>心情</p>
                                {/*這邊使用套件*/}
                                <Select
                                    options={feelingOption}
                                    value={feeling}
                                    onChange={(option) => setFeeling(option)}
                                    styles={customStyles}
                                    placeholder="請選擇心情"/>
                            </div>
                            <div id="taste">
                                <p>味道</p>
                                <Select
                                    options={tastingOption}
                                    value={tasting}
                                    onChange={(option) => setTasting(option)}
                                    styles={customStyles}
                                    placeholder="請選擇味道"
                                />
                            </div>
                            <div id="tone">
                                <p>色調</p>
                                <Select
                                    options={toneingOption}
                                    value={toneing}
                                    onChange={(option) => setToneing(option)}
                                    styles={customStyles}
                                    placeholder="請選擇色調"
                                />
                            </div>
                            <div id="drunk">
                                <p>醉相</p>
                                <Select
                                    options={drunkingOption}
                                    value={drunking}
                                    onChange={(option) => setDrunking(option)}
                                    styles={customStyles}
                                    placeholder="請選擇醉相"
                                />
                            </div>
                        </section>
                        <button onClick={handleSelect} className="cocktail_button" id="submit_button">Submit</button>
                    </section>
                )}

                {showImage && (
                    <section className="background-sqareImage">
                        <div id="print">
                            <p>{result.enTitle}</p>
                            <p>({result.zhTitle})</p>
                        </div>
                        <section>{result.image && <img id="printImg" src={result.image} alt={result.enTitle}
                        />}
                        </section>
                        <div id="print2">
                            <p id="print2" onClick={() => openModal(result.recipeId)}>詳細介紹</p>
                        </div>
                        <Modal isOpen={isModalOpen} onClose={closeModal}>
                            {modalData && (
                                <div className="modalSmallDiv">
                                    <img src={modalData.image}/>
                                    <h2>{modalData.enTitle} ({modalData.zhTitle})</h2>
                                    <section className="modalMaterial">
                                        <div className="materialDiv">材料:</div>
                                        <section className="materialSection">
                                            {modalData.materials.map((material, index) => (
                                                <div key={index}
                                                     className="materialSectionDiv">{material.material_name}: {material.material_quantity}</div>
                                            ))}
                                        </section>
                                    </section>
                                    <p>
                                        {modalData.method.split("\n").map((line, index) => (
                                            // React.Fragment只是一個不會生成額外 HTML 標籤的容器
                                            <React.Fragment key={index}>
                                                {line}
                                                <br/>
                                            </React.Fragment>
                                        ))}
                                    </p>
                                </div>
                            )}
                        </Modal>
                        <div id="newcloseButton">
                            <button onClick={handleReset} className="cocktail_button" id="onemoreButton">重新選擇
                            </button>
                            <button onClick={handleClose} className="cocktail_button" id="closeButton">Close</button>
                        </div>
                    </section>
                )}
            </section>
        </main>
    );
};

export default CocktailSelector;
