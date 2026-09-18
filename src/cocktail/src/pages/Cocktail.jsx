import React, {useState, useEffect} from "react";
import CocktailList from "../components/CocktailList";
import FilterSidebar from "../components/FilterSidebar";
import {findAllRecipe, findRecipeByBaseWine, searchRecipeByKeyword} from "../API/CocktailList.js";

const CocktailsPage = () => {
    const [cocktails, setCocktails] = useState([]);// 調酒列表
    const [filters, setFilters] = useState(["All"]);// 基酒類型
    const [loading, setLoading] = useState(false); // 加載狀態
    const [searchQuery, setSearchQuery] = useState(""); // 搜尋關鍵字

    const loadMoreCocktails = async () => {
        setLoading(true);
        let data = [];
        try {
            if (searchQuery) {
                data = await searchRecipeByKeyword(searchQuery); // 搜尋酒譜
            }
            //filters.length=>EX:["Vodka", "Gin"]
            else if (filters.length === 0 || filters.includes("All")) {
                data = await findAllRecipe(); // 全部酒譜
            } else {
                data = await findRecipeByBaseWine(filters);
            }
            setCocktails(data); // 更新酒譜列表
        } catch (error) {
            console.error("無法加載", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        // 搜尋框每打一個字就打一次 API 太吵，等使用者停 300ms 再送
        const timer = setTimeout(loadMoreCocktails, 300);
        return () => clearTimeout(timer);
    }, [filters, searchQuery]);

    return (
        <section id="cocktailSection">
            <section id="cocktail-background-img"></section>
            <section id="cocktaillistSection">
                <div id="cocktailFilter">
                    <FilterSidebar filters={filters} setFilters={setFilters}/>
                    <div id="searchBarDiv">
                        <i className="fa-solid fa-magnifying-glass"></i>
                        <input
                            type="text"
                            placeholder="搜尋名稱"
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            id="searchBar"
                        />
                    </div>

                </div>
                <div id="cocktailDiv">
                    {loading ? (
                        <p>載入中...</p>
                    ) : (
                        <CocktailList cocktails={cocktails}/>
                    )}
                </div>
            </section>
        </section>
    );
};

export default CocktailsPage;
