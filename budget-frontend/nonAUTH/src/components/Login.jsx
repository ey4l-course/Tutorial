import React from "react";
import { useState } from "react";
import "../assets/Login.css"
import { Link } from "react-router-dom";
import { mockLogin } from "../temp/MockServer";

const App = () => {
  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const [pending, setPending] = useState(false)
  const [error, setError] = useState("");

  const HandleOnSubmit = async(e) => {
    e.preventDefault();
    setPending(true);
    setError("");

    try {
      const { role } = await mockLogin({user_name: userName, password});
      window.location.replace(role === "admin" ? "/admin/" : "/user/");
    }catch (err) {
      setError(err.message);
    }finally {
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
          <input type="text" name="userName" onChange={e => setUserName(e.target.value)}/>
        </div>
        <div className="formFields">
          <label htmlFor="Password">Password:</label>
          <input type="password" name="Password" onChange={e => setPassword(e.target.value)}/>
        </div>
        <button type="submit" disabled={pending || userName.length < 4 || password.length < 8 || !error === ""}>
          {pending ? "Signing you in..." : "Login"}
        </button>
        {error && <div className="form-error" role="alert">{error}</div>}
        <footer>Not register? <span><Link to='/register'>Register new account</Link></span></footer>
        
      </form>
    </div>
  )
}

export default App;