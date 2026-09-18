import {useState} from "react";
import {useNavigate} from "react-router-dom";
import Swal from "sweetalert2";
import RecipeDetailModal from "./RecipeDetailModal.jsx";
import {deletedRecipe} from "../API/RecipeAPI";

const CocktailList = ({cocktails}) => {
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

    return (
        <section id="cocktaillistSection">
            {cocktails.map((cocktail) => (
                <div id="cocktaillistDiv" key={cocktail.recipeId}>
                    <img id="cocktaillistImg" src={cocktail.image} alt={cocktail.enTitle}/>
                    <p id="cocktaillistp"
                       onClick={() => setSelectedCocktail(cocktail)}>{cocktail.enTitle} ({cocktail.zhTitle})
                    </p>
                    <div className="buttonEditDeleteDiv">
                        <div className="buttonListEdit" onClick={() => handleEdit(cocktail.recipeId)}>
                            <i className="fa-solid fa-pen-to-square"></i></div>
                        <div className="buttonListDelete" onClick={() => handleDelete(cocktail.recipeId)}>
                            <i className="fa-solid fa-trash"></i>
                        </div>
                    </div>
                </div>
            ))}
            <RecipeDetailModal
                isOpen={Boolean(selectedCocktail)}
                onClose={() => setSelectedCocktail(null)}
                recipe={selectedCocktail}
            />
        </section>
    );
};

export default CocktailList;
