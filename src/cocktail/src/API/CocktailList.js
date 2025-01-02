export const findRecipeByBaseWine = async(baseWines)=>{
    try{
        const response = await fetch (`/lastwine/getRecipesByBaseWine?baseWine=${encodeURIComponent(baseWines.join(","))}`,{
            method:'GET',
            headers:{'Content-Type':'application/json'},
        });
        if(!response.ok){
            throw new Error("error")
        }
        return await response.json();
    }catch (error){
        console.log("錯誤: ", error);
        throw error;
    }
}

export const findAllRecipe = async () => {
    try {
        const response = await fetch(`/lastwine/getAllRecipe`, {
            method: "GET",
            headers: { "Content-Type": "application/json" },
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        if (!Array.isArray(data)) {
            console.error("後端回傳錯誤，不是陣列");
            return [];
        }
        return data;
    } catch (error) {
        console.error("錯誤: ", error);
        throw error;
    }
};

export const searchRecipeByKeyword = async (keyword) => {
    const response = await fetch(`/lastwine/search?keyword=${encodeURIComponent(keyword)}`);
    if (!response.ok) {
        throw new Error("搜尋失敗");
    }
    return response.json();
};