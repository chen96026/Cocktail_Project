export const getCocktailSelector = async(selector)=>{
    try{
        const response = await fetch (`/lastwine/getCocktailSelector`,{
            method:'POST',
            headers:{'Content-Type':'application/json'},
            body: JSON.stringify(selector),
        });
        console.log("發送的選擇參數: ", selector);
        if(!response.ok){
            throw new Error("無法獲取調酒數據")
        }
        return await response.json();
    }catch (error){
        console.log("錯誤: ", error);
        throw error;
    }
}

export const getCocktailDetail = async(recipeId)=>{
    try{
        const response = await fetch (`/lastwine/getCocktailDetail/${recipeId}`,{
            method:'GET',
            headers:{'Content-Type':'application/json'},
        });
        console.log("獲得的選擇參數: ", response);
        if(!response.ok){
            throw new Error("無法獲取調酒數據")
        }
        return await response.json();
    }catch (error){
        console.log("錯誤: ", error);
        throw error;
    }
}