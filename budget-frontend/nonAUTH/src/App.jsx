import { useEffect, useState } from "react";
import { BrowserRouter, Routes, Route, Navigate, useNavigate } from "react-router-dom";
import Login from "./components/Login.jsx";
import { Register } from "./components/Register.jsx";
import "./assets/Login.css";

function Shell() {
  const nav = useNavigate();
  const [checking, setChecking] = useState(true);
  const BASE = import.meta.env.VITE_API_BASE;

  useEffect(()=>{
    const a = sessionStorage.getItem("bt_access");
    const r = sessionStorage.getItem("bt_refresh");
    if (a && r)
      window.location.replace("/app");
    setChecking(false);
  }, [])

  if (checking) {
    return <div style={{ textAlign: "center", marginTop: 24 }}>Checking session…</div>;
  }

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
  return (
    <BrowserRouter>
      <Shell />
    </BrowserRouter>
  );
}
