import React, {useState} from "react";
import Modal from "../components/Modal.jsx";
import {useNavigate} from "react-router-dom";
import Swal from "sweetalert2";
import {deletedRecipe} from "../API/RecipeAPI";

const CocktailList = ({cocktails}) => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedCocktail, setSelectedCocktail] = useState(null);
    const navigate = useNavigate();

    const handleEdit = (recipeId) => {
        navigate(`/EditRecipeForm/${recipeId}`);
    };

    const handleDelete = async (recipeId) => {
        // 顯示刪除確認框
        const result = await Swal.fire({
            icon: "warning",
            title: "確定刪除這杯調酒嗎？",
            showCancelButton: true,
            confirmButtonText: "確定",
            cancelButtonText: "取消",
        });

        if (result.isConfirmed) {
            try {
                await deletedRecipe(recipeId);
                Swal.fire({
                    icon: "success",
                    title: "刪除成功",
                    confirmButtonText: "確定",
                });
            } catch (error) {
                Swal.fire({
                    icon: "error",
                    title: "刪除失敗",
                    text: error.message,
                    confirmButtonText: "確定",
                });
            }
        }
    };

    const openModal = (cocktail) => {
        setSelectedCocktail(cocktail);
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setSelectedCocktail(null);
    };

    return (
        <section id="cocktaillistSection">
            {cocktails.map((cocktail, index) => (
                // 用英文名稱+index作為key
                <div id="cocktaillistDiv" key={`${cocktail.entitle}-${index}`}>
                    <img id="cocktaillistImg" src={cocktail.image} alt={cocktail.enTitle}/>
                    <p id="cocktaillistp"
                       onClick={() => openModal(cocktail)}>{cocktail.en_title} ({cocktail.zh_title})
                    </p>
                    <div className="buttonEditDeleteDiv">
                        <div className="buttonListEdit" onClick={() => handleEdit(cocktail.recipe_id)}>
                            <i className="fa-solid fa-pen-to-square"></i></div>
                        <div className="buttonListDelete" onClick={() => handleDelete(cocktail.recipe_id)}>
                            <i className="fa-solid fa-trash"></i>
                        </div>
                    </div>
                </div>
            ))}
            <Modal isOpen={isModalOpen} onClose={closeModal}>
                {selectedCocktail && (
                    <div className="modalSmallDiv">
                        <img src={selectedCocktail.image}/>
                        <h2>{selectedCocktail.en_title} ({selectedCocktail.zh_title})</h2>
                        <section className="modalMaterial">
                            <div className="materialDiv">材料:</div>
                            <section className="materialSection">
                                {selectedCocktail.materials.map((material, index) => (
                                    <div key={index} className="materialSectionDiv">
                                        {material.material_name}: {material.material_quantity}
                                    </div>
                                ))}
                            </section>
                        </section>
                        <p>
                            {/*\n代表換行，分割成每組元素*/}
                            {selectedCocktail.method.split('\n').map((line, index) => (
                                <React.Fragment key={index}>
                                    {line}
                                    <br/>
                                </React.Fragment>
                            ))}
                        </p>
                    </div>
                )}
            </Modal>
        </section>
    );
};

export default CocktailList;
