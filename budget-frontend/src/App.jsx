import React from "react";
import { useState } from "react";
import "./App.css"

const testUser = {"userName": "test_user", "hashedPassword": "Hashed V@lidP@ssw0rd"}

const App = () => {
  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const passwordHashHandler = (rawPassword) => {
    return "Hashed " + rawPassword;
  }
  const HandleOnSubmit = (e) => {
    e.preventDefault();
    if (userName === testUser.userName && passwordHashHandler(password) === testUser.hashedPassword)
      console.log("Successful login")
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
        <button type="submit">Login</button>
        <footer>Not register? <span>Register new account</span></footer>
        
      </form>
    </div>
  )
}

export default App;