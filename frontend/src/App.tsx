import "bootstrap/dist/css/bootstrap.min.css";
import React, { Component } from "react";
import { Route, Switch } from "react-router-dom";

import "./App.css";

import Landing from "./pages/Landing";
import MerchantProfile from "./pages/MerchantProfile";
import MainNavigation from "./components/layout/MainNavigation";
import Dashboard from "./pages/Dashboard";
import SignUp from "./pages/SignUp";
import Transfer from "./pages/Transfer";

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
          <Route path="/sign-up">
            <SignUp />
          </Route>
          <Route path="/transfer">
            <Transfer />
          </Route>
        </Switch>
      </div>
    );
  }
}

export default App;
