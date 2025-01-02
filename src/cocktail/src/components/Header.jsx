import React from "react";
import { Link } from "react-router-dom";
import Swal from "sweetalert2";
import {logoutMember} from "../API/LogoutAPI.js";
import { useNavigate } from "react-router-dom";

const Header = ({ isLogin, onLogout, userRole}) => {
  const navigate = useNavigate();

  const handleOutalert = () => {
    Swal.fire({
      title: "確定要登出嗎?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "確定",
      cancelButtonText: "取消",
    })
    .then(async (result) => {
      if (result.isConfirmed) {
        try{
          const response = await logoutMember();
          await onLogout();
          Swal.fire({
            title:"登出成功",
            icon:"success",
            confirmButtonText:"確定",
          });
          navigate("/")
        }catch(error){
          console.error("登出失敗：", error);
          Swal.fire({
            title:"登出失敗",
            icon:"error",
            confirmButtonText:"返回",
          });
        }
      }
    });
  };

  return (
    <header>
      <section id="logo">
        <Link to="/">Last Wine</Link>
      </section>

      <nav>
        <section className="drop-down-menu">
          <div>
            <Link to="/Cocktail"><i className="fa-solid fa-wine-glass"></i> Cocktail</Link>
          </div>
          <div>
            <Link to="/AddRecipeForm"><i className="fa-solid fa-book"></i> Wine Recipe</Link>
          </div>
          {isLogin && userRole === "ADMIN" && (
              <div>
                <Link to="/Admin"><i className="fa-solid fa-user-shield"></i> Admin</Link>
              </div>
          )}
          {isLogin ? (
            <div onClick={handleOutalert}>
              <i className="fa-solid fa-right-to-bracket"></i> Logout
            </div>
          ) : (
            <div>
              <Link to="/Login"><i className="fa-solid fa-right-to-bracket"></i> Login</Link>
            </div>
          )}
        </section>
      </nav>
    </header>
  );
};

export default Header;
