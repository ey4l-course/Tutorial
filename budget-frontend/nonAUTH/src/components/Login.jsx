import React from "react";
import { useState } from "react";
import "../assets/Login.css"
import { Link, useLocation, useNavigate } from "react-router-dom";
import { login } from "../api/authClient";
import { Register } from "./Register";

const App = () => {
  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const [pending, setPending] = useState(false)
  const [error, setError] = useState("");
  const nav = useNavigate();
  const HandleOnSubmit = async (e) => {
    e.preventDefault();
    setPending(true);
    setError("");
    if (!userName){
      const fd = new FormData(e.currentTarget);
      setUserName(fd.get("userName").toString());
    }
    try{
      const res = await login ({userName, password});
      switch (res){
        // case 200 : window.location.replace("/app"); break;
        case 200 : console.log("Login success")
        case 403 : nav("/register?show=PersonalDetails"); break;
        case 401 : setError("Invalid credentials"); break;
        case 500 : setError("Server error"); break;
      }
    }catch (e){
      setError("Network error");
      console.error(e);
    }finally{
      setPending(false);
    }
  }

  return (
    <div className="main">
      <div className="title">
        <h1>Budget tracker</h1>
        <h3>Control your expenses</h3>
      </div>
      <form onSubmit={HandleOnSubmit}>
        <div className="FormFields">
          <label htmlFor="userName">User name:</label>
          <input type="text"
          name="userName"
          onChange={e => {setUserName(e.target.value); setError("")}}/>
        </div>
        <div className="formFields">
          <label htmlFor="Password">Password:</label>
          <input type="password"
          name="Password"
          onChange={e => {setPassword(e.target.value); setError("")}}/>
        </div>
        <div className="buttons">
          <button type="submit" disabled={pending || userName.length < 4 || password.length < 8 || !error === ""}>
            {pending ? "Signing you in..." : "Login"}
          </button>
        </div>
        {error && <div className="form-error" role="alert">{error}</div>}
        <footer>Not register? <span><Link to='/register'>Register new account</Link></span></footer>
      </form>
    </div>
  )
}
export default App;