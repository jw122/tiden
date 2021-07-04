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

import "./App.css";

import Landing from "./pages/Landing";
import MerchantProfile from "./pages/MerchantProfile";
import Dashboard from "./pages/Dashboard";
import MainNavigation from "./components/layout/MainNavigation";

// TODO: change to function() declaration
class App extends Component {
  render() {
    return (
      <div className="App">
        <MainNavigation />
        <Switch>
          <Route path="/" exact={true}>
            <Landing />
          </Route>
          {/* TODO: update to no longer hard code when user management is in place */}
          <Route path="/shibainu">
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
