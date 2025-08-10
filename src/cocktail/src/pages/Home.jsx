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
    const feelingOption = [
        { value: "愉悅", label: "愉悅" },
        { value: "普通", label: "普通" },
        { value: "煩悶", label: "煩悶" },
        { value: "沉重", label: "沉重" },
    ];
    const tastingOption = [
        { value: "酸", label: "酸" },
        { value: "甜", label: "甜" },
        { value: "苦", label: "苦" },
    ];
    const toneingOption = [
        { value: "冷", label: "冷" },
        { value: "暖", label: "暖" },
    ];
    const drunkingOption = [
        { value: "微酣", label: "微酣" },
        { value: "酩酊大醉", label: "酩酊大醉" },
    ];
    //這邊套用react select套件再改寫樣式
    const customStyles = {
        control: (provided) => ({
            ...provided,
            backgroundColor: "#f9f9f9",//淺灰色
            borderRadius: "8px",
            border: "none",
            padding: "1vh",
            boxShadow: "none",
            cursor: "pointer",
            "&:hover": {
                borderColor: "black",
            },
        }),
        menu: (provided) => ({
            ...provided,
            borderRadius: "8px",
            zIndex: 10,
        }),
        option: (provided, state) => ({
            ...provided,
            backgroundColor: state.isFocused ? "black" : "#fff",
            color: state.isFocused ? "#fff" : "#333",
            cursor: "pointer",
            "&:active": {
                backgroundColor: "black",
                color: "#fff",
            },
        }),
        placeholder: (provided) => ({
            ...provided,
            color: "#999",
            fontSize: "1vw",
        }),
    };

    return (
        <main id="allContent">
            {/*左邊的image是自己定義的變數名要傳給子元件Carouse.js，右邊的image是上面定義的陣列*/}
            <Carousel images={images} />
            <BaseWineSection baseWines={baseWines} />
            <CocktailSelector
                feelingOption={feelingOption}
                tastingOption={tastingOption}
                toneingOption={toneingOption}
                drunkingOption={drunkingOption}
                customStyles={customStyles}
            />
        </main>
    );
};

export default Home;
