import "bootstrap/dist/css/bootstrap.min.css";
import {
  Form,
  Row,
  Button,
  Col,
  Navbar,
  Container,
  Card,
} from "react-bootstrap";
import React, { Component } from "react";
import { Route, Switch } from "react-router-dom";

import logo from "./logo.svg";
import "./App.css";

import Landing from "./pages/Landing";
import MerchantProfile from "./pages/MerchantProfile";
import Dashboard from "./pages/Dashboard";

// TODO: change to function() declaration
class App extends Component {
  render() {
    return (
      <div className="App">
        <Navbar bg="dark" variant="dark">
          <Container>
            <Navbar.Brand href="#home">
              <img
                alt=""
                src={logo}
                width="30"
                height="30"
                className="d-inline-block align-top"
              />{" "}
              Tiden
            </Navbar.Brand>
          </Container>
        </Navbar>
        <Switch>
          <Route path="/" exact={true}>
            <Landing />
          </Route>
          {/* TODO: update to no longer hard code when user management is in place */}
          <Route path="/satoshi">
            <MerchantProfile />
          </Route>
          <Route path="/dashboard">
            <Dashboard />
          </Route>
        </Switch>
      </div>
    );
  }
}

export default App;
