import {request} from "./request.js";

export const logoutMember = () =>
    request("/lastwine/logout", {method: "POST", credentials: "include"});
