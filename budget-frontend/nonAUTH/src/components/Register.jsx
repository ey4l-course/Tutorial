import React from "react";
import { useState } from "react";
import { Credentials } from "./Credentials"
import { Details } from "./Details";
import { Link, useSearchParams } from "react-router-dom";
import '../assets/Register.css'

export const Register = () => {
    const [sp] = useSearchParams();
    const initial = sp.get("show") ?? "credentials";
    const [formStatus, setFormStatus] = useState(initial);
    return(
        <div className="main">
            <div className="title">
                <h1>Register new user</h1>
            </div>
            <div className="registration-form">
                {formStatus === "credentials" ? (
                    <Credentials
                    onClickNext={() => setFormStatus("PersonalDetails")}
                    />) : (
                        <Details
                        OnClickBack={() => setFormStatus("credentials")}
                        />
                    )}
                <footer>Already have an account? <span><Link to='/Login'>Login</Link></span></footer>
            </div>
        </div>
    )
}