//TODO: Switch to HttpCoolie for prod

import { data } from "react-router-dom";

const BASE = import.meta.env.VITE_API_BASE;
const ACCESS_KEY = "bt_access"
const REFRESH_KEY = "bt_refresh";

export const setTokens = ({accessToken, refreshToken}) => {
    if (accessToken) sessionStorage.setItem(ACCESS_KEY, accessToken);
    if (refreshToken) sessionStorage.setItem(REFRESH_KEY, refreshToken);
}

export const clearTokens = () => {
    sessionStorage.removeItem(ACCESS_KEY);
    sessionStorage.removeItem(REFRESH_KEY);
}

export const setHeaders = (headers = {}) => {
    const h = new Headers(headers);
    h.set("Authorization", `Bearer ${sessionStorage.getItem(ACCESS_KEY)}`);
    h.set("Refresh", `Bearer ${sessionStorage.getItem(REFRESH_KEY)}`);
    return h;
}

export async function authorizedFetch(path, opts = {}) {
  const res = await fetch(`${BASE}${path}`, { ...opts, headers: setHeaders(opts.headers) });
  return res;
}

export async function login({ userName, password }) {
  let res;
  try {
    res = await fetch(`${BASE}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        "userName": userName,
        "password": password
      }),
    });
    //Cases where tokens are present:
    if (res.ok || res.status === 403){
      const data = await res.json();
      const accessToken  = data.accessToken;
      const refreshToken = data.refreshToken;
      if (!accessToken || !refreshToken) throw new Error("Missing tokens");
      setTokens({ accessToken, refreshToken });
    }else{
      console.error(await res.text().catch());
    }
    return res.status;
  } catch (e){
    throw new Error (`Network error: ${e.message || e}`);
  }
}

export async function logout() {
  clearTokens();
}