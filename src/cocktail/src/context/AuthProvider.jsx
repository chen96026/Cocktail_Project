import {useCallback, useMemo, useState} from "react";
import {AuthContext} from "./authContext.js";

// 登入狀態集中管理，token 與 role 一律透過這裡讀寫，元件不再各自碰 localStorage
const AuthProvider = ({children}) => {
    const [auth, setAuth] = useState(() => ({
        token: localStorage.getItem("token"),
        role: localStorage.getItem("role"),
    }));

    const login = useCallback((token, role) => {
        localStorage.setItem("token", token);
        localStorage.setItem("role", role || "");
        setAuth({token, role});
    }, []);

    const logout = useCallback(() => {
        localStorage.removeItem("token");
        localStorage.removeItem("role");
        setAuth({token: null, role: null});
    }, []);

    const value = useMemo(() => ({
        isLogin: Boolean(auth.token),
        role: auth.role,
        login,
        logout,
    }), [auth, login, logout]);

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export default AuthProvider;
