import './App.css';
import React, {useState, useEffect} from 'react';
import {BrowserRouter as Router, Routes, Route, useLocation} from "react-router-dom";
import Header from './components/Header';
import Footer from './components/Footer';
import Login from './components/Login';
import Cocktail from './pages/Cocktail';
import AddRecipeForm from './components/AddRecipeForm.jsx';
import EditRecipeForm from './components/EditRecipeForm.jsx';
import Home from './pages/Home';
import BaseWineText from './pages/BaseWineText';
import Regist from "./components/Regist";
import Admin from "./pages/Admin.jsx";

function App() {
    const [isLogin, setIslogin] = useState(false);
    const [userRole, setUserRole] = useState();
    const location = useLocation();
    const handleLogout = () => {
        setIslogin(false);
        setUserRole(null); // 清除角色狀態
        localStorage.removeItem("token"); // 清除 localStorage 中的 Token
        localStorage.removeItem("role");
    };

    // 初始化時檢查 localStorage 中是否有 Token
    useEffect(() => {
        const token = localStorage.getItem("token");
        const role = localStorage.getItem("role");
        //如果 token 存在，將登入狀態設置為 true
        if (token) {
            setIslogin(true); // 如果 Token 存在，設置為已登入
        }
        //如果role存在，將角色儲存在狀態中，以便其他地方使用
        if (role) {
            setUserRole(role); // 保存角色到狀態中
        }
    }, [isLogin, userRole]);

    return (
        <div>
            {/*登入跟註冊頁面不顯示header跟footer，pathname會返回目前的瀏覽器地址欄中的路徑部分*/}
            {/*handleLogout不需要再定義usestate，因為他沒有要管理狀態*/}
            {location.pathname !== "/Login" && location.pathname !== "/Regist" &&
                <Header isLogin={isLogin} onLogout={handleLogout} userRole={userRole}/>}
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/Login" element={<Login setIsLogin={setIslogin}/>}/>
                <Route path="/Cocktail" element={<Cocktail/>}/>
                <Route path="/AddRecipeForm" element={<AddRecipeForm/>}/>
                <Route path="/EditRecipeForm/:recipe_id" element={<EditRecipeForm/>}/>
                <Route path="/BaseWine/*" element={<BaseWineText/>}/>
                <Route path="/Regist" element={<Regist setIsLogin={setIslogin}/>}/>
                <Route path="/Admin" element={<Admin/>}/>
            </Routes>
            {location.pathname !== "/Login" && location.pathname !== "/Regist" && <Footer/>}
        </div>
    );
}

export default function WrappedApp() {
    return (
        <Router>
            <App/>
        </Router>
    );
}
