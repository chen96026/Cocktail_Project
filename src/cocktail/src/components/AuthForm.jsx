import {useState} from "react";
import {Link} from "react-router-dom";
import Swal from "sweetalert2";

/**
 * 登入與註冊共用的表單
 *
 * @param submitLabel     送出按鈕文字
 * @param footerText      底部提示文字
 * @param footerLinkTo    底部連結路徑
 * @param footerLinkLabel 底部連結文字
 * @param onSubmit        送出處理，接到 {account, password}
 */
const AuthForm = ({submitLabel, footerText, footerLinkTo, footerLinkLabel, onSubmit}) => {

    const [userAccount, setUserAccount] = useState("");
    const [userPassword, setUserPassword] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!userAccount || !userPassword) {
            Swal.fire({
                title: "請輸入帳號與密碼",
                icon: "warning",
                confirmButtonText: "確定",
            });
            return;
        }
        //將使用者的帳號和密碼組裝成一個物件
        await onSubmit({account: userAccount, password: userPassword});
    };

    const handleKeyDown = (e) => {
        if (e.key === "Enter") {
            handleSubmit(e);
        }
    };

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
                       onChange={(e) => setUserAccount(e.target.value)}
                       onKeyDown={handleKeyDown}/>
                <input type="password" placeholder="&nbsp;密碼" value={userPassword}
                       onChange={(e) => setUserPassword(e.target.value)}
                       onKeyDown={handleKeyDown}/>
                <button id="loginButton" onClick={handleSubmit}>{submitLabel}</button>
                <div>{footerText}</div>
                <Link to={footerLinkTo}>
                    <div id="registButton">{footerLinkLabel}</div>
                </Link>
            </div>
        </section>
    );
};

export default AuthForm;
