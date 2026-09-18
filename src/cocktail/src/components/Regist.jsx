import {useNavigate} from "react-router-dom";
import Swal from "sweetalert2";
import AuthForm from "./AuthForm.jsx";
import {registMember} from "../API/RegistAPI.js";
import {useAuth} from "../context/authContext.js";

const Regist = () => {
    const navigate = useNavigate();
    const {login} = useAuth();

    const handleRegist = async (formData) => {
        try {
            const data = await registMember(formData);
            // 註冊完直接視為已登入，role 一併保存
            login(data.token, data.role);
            Swal.fire({
                title: "註冊成功",
                icon: "success",
                confirmButtonText: "確定",
            });
            navigate("/");
        } catch (error) {
            console.error("註冊失敗，錯誤信息：", error);
            Swal.fire({
                title: "註冊失敗",
                icon: "error",
                confirmButtonText: "確定",
            });
        }
    };

    return (
        <AuthForm
            submitLabel="註冊"
            footerText="已經有帳號?"
            footerLinkTo="/Login"
            footerLinkLabel="點我登入"
            onSubmit={handleRegist}
        />
    );
};

export default Regist;
