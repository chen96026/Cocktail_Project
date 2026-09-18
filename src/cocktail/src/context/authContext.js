import {createContext, useContext} from "react";

export const AuthContext = createContext(null);

/**
 * @returns 登入狀態與 login / logout 方法
 */
export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error("useAuth 必須在 AuthProvider 內使用");
    }
    return context;
};
