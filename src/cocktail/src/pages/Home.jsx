import Carousel from "../components/Carousel";
import BaseWineSection from "../components/BaseWineSection";
import CocktailSelector from "../components/CocktailSelector";

const Home = () => {
    const images = [
        "/img/封面/首頁圖1.jpg",
        "/img/封面/首頁圖2.jpg",
        "/img/封面/首頁圖3.jpg",
        "/img/封面/首頁圖4.jpg",
    ];
    const baseWines = [
        { name: "伏特加 Vodka", path: "/BaseWine/vodka" },
        { name: "白蘭地 Brandy", path: "/BaseWine/brandy" },
        { name: "琴酒 Gin", path: "/BaseWine/gin" },
        { name: "威士忌 Whisky", path: "/BaseWine/whisky" },
        { name: "龍舌蘭 Tequila", path: "/BaseWine/tequila" },
        { name: "蘭姆酒 Rum", path: "/BaseWine/rum" },
    ];

    return (
        <main id="allContent">
            {/*左邊的image是自己定義的變數名要傳給子元件Carouse.js，右邊的image是上面定義的陣列*/}
            <Carousel images={images} />
            <BaseWineSection baseWines={baseWines} />
            <CocktailSelector/>
        </main>
    );
};

export default Home;
