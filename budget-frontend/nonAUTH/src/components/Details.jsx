import React from "react";
import { useState } from "react";
import { useRef } from "react";
import { validateEmail, validateName } from "../utilities/Validator";
import '../assets/Register.css'
import { authorizedFetch } from "../api/authClient";

export const Details = ({OnClickButton}) => {
    const [givenName, setGivenName] = useState("");
    const [surname, setSurname] = useState("");
    const [mobile, setMobile] = useState("");
    const [email, setEmail] = useState("");
    const [givenNameValid, setGivenNameValid] = useState(false);
    const [surnameValid, setSurnameValid] = useState(false);
    const [mobileValid, setMobileValid] = useState(false);
    const [emailValid, setEmailValid] = useState(false);
    const mobileTimeout = useRef();

    const trackGivenName = (val) => {setGivenNameValid(validateName(val))};
    const trackSurname = (val) => {setSurnameValid(validateName(val))};
    const trackMobile = (val) => {
        setMobile(val);
        clearTimeout(mobileTimeout.current);
        mobileTimeout.current = setTimeout(() => {
            const cleanVal = val.replace(/\D/g, "");
            setMobile(cleanVal);
            setMobileValid(mobile.length >= 9 && mobile.length <= 10 ? true : false);
        }, 150)
    }
    const trackEmail = (val) => {setEmailValid(validateEmail(val))};
    const handleSubmit = (ev) => {
        console.log(`e = ${ev}, type = ${typeof(ev)}`)
        ev.preventDefault();
        authorizedFetch ("/auth/activate", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                "givenName": givenName,
                "surname": surname,
                "email": email,
                "mobile": mobile
            })            
        });
        OnClickButton();
    }
    return (
        <form onSubmit={handleSubmit}>
            <div className="form-fields">
                <input type="text"
                value = {givenName}
                placeholder="Given name"
                onChange={e => {const val = e.target.value; setGivenName(val); trackGivenName(val);}}
                className= {givenName.length === 0 ? "" : (givenNameValid ? "valid-field" : "invalid-field")} />
            </div>
            <div className="form-fields">
                <input type="text"
                value = {surname}
                placeholder="Surname"
                onChange={e => {const val = e.target.value; setSurname(val); trackSurname(val);}}
                className={surname.length === 0 ? "" : (surnameValid ? "valid-field" : "invalid-field")} />
            </div>
            <div className="form-fields">
                <input type="text"
                value={mobile}
                placeholder="Mobile (digits only)"
                onChange={e => trackMobile(e.target.value)}
                className={mobile.length === 0 ? "" : (mobileValid ? "valid-field" : "invalid-field")} />
            </div>
            <div className="form-fields">
                <input type="text"
                value = {email}
                placeholder="E-mail"
                onChange={e => {const val = e.target.value; setEmail(val); trackEmail(val);}}
                className={email.length === 0 ? "" : (emailValid ? "valid-field" : "invalid-field")} />
            </div>
            <div className="form-fields buttons">
                <button type="submit" disabled = {!(givenNameValid && surnameValid)}>Register</button>
                <button onClick={e => {e.preventDefault(); OnClickButton()}}>Back</button>
            </div>
        </form>
    )
}