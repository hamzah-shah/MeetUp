import React, { useEffect, useState } from "react";
import Form from "react-bootstrap/Form";
import Button from "./Button";
import axios from 'axios';
import ReactSession from 'react-client-session';
import { Redirect } from "react-router-dom";
import { BrowserRouter as Switch, Route, BrowserRouter } from "react-router-dom";
import Home from './Home';
import {Link} from 'react-router-dom';
import { useAppContext } from './ContextUtil';

function Signup() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [redirect, setRedirect] = useState(false)
  const { userHasAuthenticated } = useAppContext();
  function validateForm() {
    return isValidEmail(email) && isValidPassword(password);
  }
  function isValidEmail(acct) {
      const emailRegex = RegExp('^[a-zA-Z0-9._%+-]+@brown+\.edu$')
      return emailRegex.test(acct)
  }
  function isValidPassword(pass) {
      return pass.length > 0;
  }

  function handleSubmit(event) {
    event.preventDefault();
    requestAuthentication(email, password);
      // if true, then we wanna redirect to another page (home) and save user in session 
  }
  const requestAuthentication = () => {
    console.log('reached')
    const toSend = {
        email: email,
        pass: password,
    }
    let config = {
        headers: {
          "Content-Type": "application/json",
          'Access-Control-Allow-Origin': '*',
          }
        }
        axios.post('http://localhost:4567/signup', toSend, config)
        .then(response => {
            if(response.data) {
              localStorage.setItem("user", toSend.email);
              setEmail(toSend.email);
              window.location.reload(true);
              // TODO 
              // ReactSession.setStoreType("localStorage");
              // ReactSession.set("username", email);
              // setRedirect(true);
              // window.location.href = "/";
            console.log("success");
              console.log(localStorage.getItem("user"))
            } else {
                // rerender page, with alert
                setRedirect(false);
                console.log("failure");
            }
        })
        .catch(function (error) {
          console.log(error);
        });
      }
      console.log(localStorage.getItem("user"));
      if (localStorage.getItem("user") !== null) {
        return(
                <Redirect
                to={{
                    pathname: "/home"
                }}
                />
              );}
      else{
  return (
    <div className="margins1">
      <p className="meet-up"> MeetUp!</p>
      <div className="login-div">
        <h1 className="login">
        Sign Up
      </h1>
      <Form onSubmit={handleSubmit}>
        <Form.Group size="lg" controlId="email">
          <Form.Label>Email: </Form.Label>
          <Form.Control
            autoFocus
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </Form.Group>
        <br/>
        <Form.Group size="lg" controlId="password">
          <Form.Label>Password: </Form.Label>
          <Form.Control
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </Form.Group>
        <br/>
              <Button class="login-button" type="submit" text="Sign Up" disabled={!validateForm()}>
              Signup
              </Button>
      </Form>
      <p className="signup-text"> Already have an account?   
      <a class="signup-button" href="/">
              <Button type="submit" text="Login">
                 Login
              </Button>
              </a>
              </p>
      </div>
    </div>
  );
      }
}
export default Signup;