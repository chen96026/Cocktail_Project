import React, {useState} from "react";
import {useNavigate} from "react-router-dom";
import Swal from "sweetalert2";
import {Link} from "react-router-dom";
import {loginMember} from '../API/LoginAPI.js';

const Login = ({setIsLogin}) => {

    const [userAccount, setUseraccount] = useState("");
    const [userPassword, setUserpassword] = useState("");
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        if (!userAccount || !userPassword) {
            Swal.fire({
                title: "請輸入帳號與密碼",
                icon: "warning",
                confirmButtonText: "確定",
            });
            return;
        }
        try {
            //將使用者的帳號和密碼組裝成一個物件formData
            const formData = {
                account: userAccount,
                password: userPassword,
            };
            const data = await loginMember(formData)
            if (data.token) {
                localStorage.setItem("token", data.token);
                localStorage.setItem("role", data.role);
                // 更新登入狀態
                setIsLogin(true);
                Swal.fire({
                    title: "登入成功",
                    icon: "success",
                    confirmButtonText: "確定",
                });
                navigate('/');
            } else {
                throw new Error("Token 缺失，請聯繫管理員");
            }
        } catch (error) {
            // 錯誤提示
            Swal.fire({
                title: "帳號或密碼錯誤",
                icon: "error",
                confirmButtonText: "返回",
            });
        }
    };

    const handleKeyDown = (e) => {
        if (e.key === "Enter") {
            handleLogin(e);
        }
    }

    return (
        <section id="loginBackground">
            <div id="loginDiv">
                <Link to="/">
                    <div id="loginTitle">Last Wine</div>
                </Link>
                <div className="member_group_button">
                    <button className="member_google-btn" id="member_login_google_button">
                        <img
                            src="https://www.gstatic.com/images/branding/product/1x/gsa_64dp.png"
                            alt="Google Logo" className="member_google-logo"/> 使用 Google 登入
                    </button>
                </div>
                <p className="loginOr">或</p>
                <input type="text" placeholder="&nbsp;帳號" value={userAccount}
                       onChange={(e) => setUseraccount(e.target.value)}
                       onKeyDown={handleKeyDown}/>
                <input type="password" placeholder="&nbsp;密碼" value={userPassword}
                       onChange={(e) => setUserpassword(e.target.value)}
                       onKeyDown={handleKeyDown}/>
                <button id="loginButton" onClick={handleLogin}>登入</button>
                <div>還沒有帳號?</div>
                <Link to="/Regist">
                    <div id="registButton">點我註冊</div>
                </Link>
            </div>
        </section>
    )
}

export default Login;