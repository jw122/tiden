import React, { Component } from "react";

import logo from "./logo.svg";

import "./App.css";
import { async } from "q";

// TODO: move to env
const SERVER_URL = "http://localhost:8080";
class App extends Component {
  state = {
    message: "YOOO",
  };

  componentDidMount() {
    this.callApi()
      .then((res) => this.setState({ message: res.message }))
      .catch((err) => console.log(err));
  }

  callApi = async () => {
    const response = await fetch(SERVER_URL + "/api/hello", {
      method: "GET",
      mode: "cors",
    });
    console.log("response from server: ", response);
    const body = await response.json();
    console.log("got response from api: ", body);

    return body;
  };

  render() {
    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h2>Welcome to Tiden</h2>
          <p>{this.state.message}</p>
        </header>
      </div>
    );
  }
}

export default App;
