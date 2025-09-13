//TODO: Switch to HttpCoolie for prod

const BASE = import.meta.env.VITE_API_BASE;
const ACCESS_KEY = "bt_access"
const REFRESH_KEY = "bt_refresh";

export const setTokens = ({accessToken, refreshToken}) => {
    if (accessToken) sessionStorage.setItem(ACCESS_KEY, accessToken);
    if (refreshToken) sessionStorage.setItem(REFRESH_KEY, refreshToken);
    console.log(`Tokens successfully set: access = ${accessToken}, refresh = ${refreshToken}`);
    console.log(`Tokens successfully set: access = ${sessionStorage.getItem(ACCESS_KEY)}, refresh = ${sessionStorage.getItem(REFRESH_KEY)}`);
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
  const res = await fetch(`${BASE}${path}`, { ...opts, headers: withAuthHeaders(opts.headers) });
  return res;
}

export async function login({ user_name, password }) {
  const res = await fetch(`${BASE}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ user_name, password })
  });

  if (!res.ok) {
    const msg = await res.text().catch(() => "");
    throw new Error(msg || `Login failed: ${res.status}`);
  }

  const data = await res.json();
  const access  = data.access  ?? data.access_token  ?? data.Authorization;
  const refresh = data.refresh ?? data.refresh_token ?? data.Refresh;
  if (!access || !refresh) throw new Error("Missing tokens in response");

  setTokens({ access, refresh });
  return data; // may include role
}

export async function logout() {
  clearTokens();
  // optionally call `${BASE}/auth/logout` if backend has it
}