import './App.css';
import {BrowserRouter as Router, Routes, Route} from "react-router-dom";
import Header from './components/Header';
import Footer from './components/Footer';
import Cocktail from './pages/Cocktail';
import AddRecipeForm from './components/AddRecipeForm.jsx';
import EditRecipeForm from './components/EditRecipeForm.jsx';
import Home from './pages/Home';
import BaseWineText from './pages/BaseWineText';
import Admin from "./pages/Admin.jsx";

function App() {
    return (
        <div>
            <Header/>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/Cocktail" element={<Cocktail/>}/>
                <Route path="/AddRecipeForm" element={<AddRecipeForm/>}/>
                <Route path="/EditRecipeForm/:recipe_id" element={<EditRecipeForm/>}/>
                <Route path="/BaseWine/*" element={<BaseWineText/>}/>
                <Route path="/Admin" element={<Admin/>}/>
            </Routes>
            <Footer/>
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
