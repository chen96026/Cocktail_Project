import {Routes, Route} from "react-router-dom";
import BaseWine from "../components/BaseWine";
import {BASE_WINE_TEXT} from "../constants/baseWineText.jsx";

const BaseWineText = () => {
    return (
        //根據一個物件動態產生多個基酒介紹頁的路由，新增基酒資料時，不用再手動加 <Route>
        <Routes>
            {Object.entries(BASE_WINE_TEXT).map(([key, data]) => (
                <Route key={key} path={`/${key}`} element={<BaseWine {...data} />} />
            ))}
        </Routes>
    );
};

export default BaseWineText;
