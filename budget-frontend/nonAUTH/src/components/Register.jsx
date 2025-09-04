import React from "react";
import { useState } from "react";
import { Credentials } from "./Credentials"
import { Details } from "./Details";
import { Link } from "react-router-dom";
import '../assets/Register.css'

// const credentials = {"userName" : "", "Password": ""}; //Temp - will be replaced with POST
// const personalDetails = {"userId":"", "givenName":"","surename":"", "email":"", "mobile":""}; //Temp - will be replaced with POST

export const Register = () => {
    const [formStatus, setFormStatus] = useState("credentials")
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
                        OnClickButton={() => setFormStatus("credentials")}
                        />
                    )}
                <footer>Already have an account? <span><Link to='/Login'>Login</Link></span></footer>
            </div>
        </div>
    )
}