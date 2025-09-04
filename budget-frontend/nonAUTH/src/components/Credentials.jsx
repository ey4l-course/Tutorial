import React from "react";
import { useState } from "react";
import { checkPasswordRules, validateUsername } from "../utilities/Validator.js";
import '../assets/Register.css'

export const Credentials = ({onClickNext}) => {
    const [userName, setUserName] = useState("");
    const [password, setPassword] = useState("");
    const [userNameValid, setUserNameValid] = useState (false);
    const [trackPwdRules, setTrackPwdRules] = useState(
        {capital:false, small:false, digit: false, symbol: false, length:false}
    )
    const trackUserName = (val) => setUserNameValid(validateUsername(val));
    const allPwdRulesOk = Object.values(trackPwdRules).every(Boolean);
    const validatePassword = (val) => {
        setPassword(val);
        setTrackPwdRules(checkPasswordRules(val));
    };
    const handleNext = (e) => {
        e.preventDefault();
        console.log("fetch POST " + userName +", "+ password);
        //if (response.status === 201)
        onClickNext();
        setPassword("");
        setUserName("");
        setUserNameValid(false);
        setTrackPwdRules({capital:false, small:false, digit: false, symbol: false, length:false});
    }
    return (
        <form onSubmit={handleNext}>
            <div className="form-fields">
                <input type="text"
                    placeholder="e.g. My_user12"
                    value={userName}
                    onChange={e => {const val = e.target.value; setUserName(val); trackUserName(val)}}
                    className= {userName.length === 0 ? "" : (userNameValid ? "valid-field" : "invalid-field")}
                     />
            </div>
            <div className="form-fields">
                <input type="password"
                    placeholder="Password"
                    value={password}
                    onChange={e => validatePassword(e.target.value)}
                    className= {password.length === 0 ? "" : (allPwdRulesOk ? "valid-field" : "invalid-field")} />
            </div>
            <div className="buttons">
                <button type="submit" disabled = {!(allPwdRulesOk && userNameValid)}>Next</button>
            </div>
        </form>
    )
}