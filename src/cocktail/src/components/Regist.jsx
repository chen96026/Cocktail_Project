import React, {useState} from "react";
import {useNavigate} from "react-router-dom";
import Swal from "sweetalert2";
import {Link} from "react-router-dom";
import {registMember} from '../API/RegistAPI.js';

const Regist = ({setIsLogin}) => {

    const [userAccount, setUseraccount] = useState("");
    const [userPassword, setUserpassword] = useState("");
    const navigate = useNavigate();

    const handleRegist = async (e) => {
        e.preventDefault();
        if (!userAccount || !userPassword) {
            Swal.fire({
                title: "請輸入帳號及密碼",
                icon: 'warning',
                confirmButtonText: '確定',
            });
            return;
        }
        try {
            const formData = {
                account: userAccount,
                password: userPassword,
            };
            const response = await registMember(formData);

            Swal.fire({
                title: '註冊成功',
                icon: 'success',
                confirmButtonText: '確定'
            });
            setIsLogin(true);
            navigate('/')
        } catch (error) {
            console.error("註冊失敗，錯誤信息：", error);
            Swal.fire({
                title: '註冊失敗',
                icon: 'error',
                confirmButtonText: '確定'
            });
        }
    };

    const handleKeyDown = (e) => {
        if (e.key === "Enter") {
            handleRegist(e);
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
                       onChange={(e) => setUseraccount(e.target.value)} onKeyDown={handleKeyDown}/>
                <input type="password" placeholder="&nbsp;密碼" value={userPassword}
                       onChange={(e) => setUserpassword(e.target.value)} onKeyDown={handleKeyDown}/>
                <button id="loginButton" onClick={handleRegist}>註冊</button>
                <div>已經有帳號?</div>
                <Link to="/Login">
                    <div id="registButton">點我登入</div>
                </Link>
            </div>
        </section>
    )
}

export default Regist;