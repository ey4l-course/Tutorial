import { useEffect, useState } from "react";
import { BrowserRouter, Routes, Route, Navigate, useNavigate } from "react-router-dom";
import Login from "./components/Login.jsx"; // your form UI
import { Register } from "./components/Register.jsx";
import "./assets/Login.css";

function Shell() {
  const nav = useNavigate();
  const [checking, setChecking] = useState(true); // while we ask /auth/me once
  const baseUrl = "http://51.4.105.38/"
  useEffect(() => {
    (async () => {
      try {
        const res = await fetch("/auth/me", { credentials: "include" });
        if (res.ok) {
          const { role } = await res.json(); // { role: "admin" | "user" }
          nav(role === "admin" ? "/admin/" : "/app/", { replace: true });
          return;
        }
      } catch (err) {
        console.error("auth check failed", err);
      } finally {
        setChecking(false);
      }
    })();
  }, [nav]);

  if (checking) {
    // tiny non-blocking placeholder; no spinner needed
    return <div style={{ textAlign: "center", marginTop: 24 }}>Checking session…</div>;
  }

  // Not authenticated -> show auth routes
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}

export default function App() {
  // Top-level router for the nonAuth app
  return (
    <BrowserRouter /* basename="/" if deployed at root of this app */>
      <Shell />
    </BrowserRouter>
  );
}
