import {useNavigate} from "react-router-dom";
import Swal from "sweetalert2";
import AuthForm from "./AuthForm.jsx";
import {loginMember} from "../API/LoginAPI.js";
import {useAuth} from "../context/authContext.js";

const Login = () => {
    const navigate = useNavigate();
    const {login} = useAuth();

    const handleLogin = async (formData) => {
        try {
            const data = await loginMember(formData);
            // 更新登入狀態（token 與 role 由 AuthProvider 保存）
            login(data.token, data.role);
            Swal.fire({
                title: "登入成功",
                icon: "success",
                confirmButtonText: "確定",
            });
            navigate("/");
        } catch (error) {
            console.error("登入失敗：", error);
            Swal.fire({
                title: "帳號或密碼錯誤",
                icon: "error",
                confirmButtonText: "返回",
            });
        }
    };

    return (
        <AuthForm
            submitLabel="登入"
            footerText="還沒有帳號?"
            footerLinkTo="/Regist"
            footerLinkLabel="點我註冊"
            onSubmit={handleLogin}
        />
    );
};

export default Login;
