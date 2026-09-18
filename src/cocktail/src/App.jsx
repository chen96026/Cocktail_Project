import './App.css';
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
import AuthProvider from "./context/AuthProvider.jsx";

function App() {
    const location = useLocation();
    // 登入跟註冊頁面不顯示header跟footer，pathname會返回目前的瀏覽器地址欄中的路徑部分
    const hideLayout = location.pathname === "/Login" || location.pathname === "/Regist";

    return (
        <div>
            {!hideLayout && <Header/>}
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/Login" element={<Login/>}/>
                <Route path="/Cocktail" element={<Cocktail/>}/>
                <Route path="/AddRecipeForm" element={<AddRecipeForm/>}/>
                <Route path="/EditRecipeForm/:recipe_id" element={<EditRecipeForm/>}/>
                <Route path="/BaseWine/*" element={<BaseWineText/>}/>
                <Route path="/Regist" element={<Regist/>}/>
                <Route path="/Admin" element={<Admin/>}/>
            </Routes>
            {!hideLayout && <Footer/>}
        </div>
    );
}

export default function WrappedApp() {
    return (
        <Router>
            <AuthProvider>
                <App/>
            </AuthProvider>
        </Router>
    );
}
