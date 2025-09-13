// src/api/mock.js
export function mockLogin({ user_name, password }) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (user_name === "demo" && password === "Demo123!") resolve({ role: "user" });
      else reject({ status: 403, message: "Invalid credentials" });
    }, 500);
  });
}

export function mockRegisterCreds(payload) {
  return new Promise(res => setTimeout(() => res({ ok: true }), 500));
}

export function mockPersonalDetails(payload) {
  return new Promise(res => setTimeout(() => res({ ok: true }), 500));
}

export function mockWhoAmI() {
  return new Promise(res => setTimeout(() => res({ role: null }), 200)); // null = unauth
}
