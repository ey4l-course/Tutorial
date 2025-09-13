import React from "react";
import { useState } from "react";
import { checkPasswordRules, validateUsername } from "../utilities/Validator.js";
import '../assets/Register.css'
import { setTokens } from "../api/authClient.js";

export const Credentials = ({onClickNext}) => {
    const [userName, setUserName] = useState("");
    const [password, setPassword] = useState("");
    const [userNameValid, setUserNameValid] = useState (false);
    const [trackPwdRules, setTrackPwdRules] = useState(
        {capital:false, small:false, digit: false, symbol: false, length:false}
    );
    const url = "http://51.4.105.38/auth";
    const [pending, setPending] = useState(false);
    const [error, setError] = useState("");
    const trackUserName = (val) => setUserNameValid(validateUsername(val));
    const allPwdRulesOk = Object.values(trackPwdRules).every(Boolean);
    const validatePassword = (val) => {
        setPassword(val);
        setTrackPwdRules(checkPasswordRules(val));
    };

    const handleNext = async(e) => {
        e.preventDefault();
        setPending(true);
        setError("");
        try {
            const res = await fetch (url, {
                method: "POST", 
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    "userName": userName, 
                    "password": password
                })
            });
            if (!res.ok){
                console.error(res.status);
                const msg = await res.text();
                throw new Error(msg);
            }
            const result = await res.json();
            setTokens(result);
            onClickNext();
            setPassword("");
            setUserName("");
            setUserNameValid(false);
            setTrackPwdRules({
                capital:false, 
                small:false, 
                digit: false, 
                symbol: false, 
                length:false
            });
        }
        catch (err){
            setError(err.message);
        }finally{
            setPending(false);
        }
    };
    return (
        <form onSubmit={handleNext}>
            <div className="form-fields">
                <input type="text"
                    placeholder="e.g. My_user12"
                    value={userName}
                    onChange={e => {const val = e.target.value; setUserName(val); trackUserName(val), setError("")}}
                    className= {userName.length === 0 ? "" : (userNameValid ? "valid-field" : "invalid-field")}
                     />
            </div>
            <div className="form-fields">
                <input type="password"
                    placeholder="Password"
                    value={password}
                    onChange={e => {validatePassword(e.target.value), setError("")}}
                    className= {password.length === 0 ? "" : (allPwdRulesOk ? "valid-field" : "invalid-field")} />
            </div>
            <div className="buttons">
                <button type="submit" disabled = {!(allPwdRulesOk && userNameValid && !pending && error === "")}>
                    {pending ? "Proccessing..." : "Next"}
                </button>
            </div>
            {error && <div className="form-error" role="alert">{error}</div>}
        </form>
    )
}