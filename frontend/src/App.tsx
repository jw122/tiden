import "bootstrap/dist/css/bootstrap.min.css";
import React, { Component } from "react";

import logo from "./logo.svg";

import "./App.css";
import { async } from "q";

// TODO: move to env
const SERVER_URL = "http://localhost:8080";
class App extends Component {
  state = {
    message: "",
    name: "",
    responseToPost: "",
    // payment fields (TODO: move into its own group?)
    amount: "10.50",
    description: "gift money",
    email: "hello@test.com",
    phoneNumber: "+12025550180",
    cvv: "LS0tLS1CRUdJTiBQR1AgTUVTU0FHRS0tLS0tDQpWZXJzaW9uOiBPcGVuUEdQLmpzIHY0LjEwLjQNCkNvbW1lbnQ6IGh0dHBzOi8vb3BlbnBncGpzLm9yZw0KDQp3Y0JNQTBYV1NGbEZScFZoQVFnQXF6N01LMmJiZnpWODBkeHZEeE11WnFIcU9peFVSY2ZHS2lOM0FrVjINCkl1N1lRMXRsVlVvSzArUVBCVFFxQ0JCdmNFV0RYYktjZmpqbmluMEJkVWR1NVNqdkJKRytmcmJMb3VXZQ0KUk5TWUdoczA5cGRwUjdnc2hYNjhqaEpnRWdVZzF4ZUt3V0FReFJwZE5OTzhxY3pUQnRhVkdBQ1Y3UW00DQpTSEtvQTJrWklmK2dtQy95K3VMSUswdVZDRUs0TkNlck9LRUc1MlJEUlI1MXU0K0xnUHNpZVN5bkoxSFINCklYMktJbWpTZWZsZHNvUHp2TW5KUFY3U2tjRzVHa3I2cWE4TnpsM1dTY2srdS9MZmdRQVRYOFdRM1FzOA0KWDRod0t2U293TnkxaEdEWnEvY0g3RVhSMlpuQnM4ZDdlM25oWGgxMHhXT3pvNEpIQko3Z2JrQk9CTU9aDQpqTkpoQVpXWU9ZNjlBT2EwVHhYZ3NqYUhxcGlud3BtQnJOMlorRExpeWt1U1UyYTNQVm93clV6aXJTOFINCm9qYndnd1d4Szg1TzZ1Y0Y2Tlo5WFdQWXBNUVFEY3dnVkliQ3Q1L3YvTk5BTzhnZXhheWlVS2FBS28wUw0KbHdyQitoSWYwdDNyNkE9PQ0KPVpMZjQNCi0tLS0tRU5EIFBHUCBNRVNTQUdFLS0tLS0NCg==",
    expirationMonth: 1,
    expirationYear: 2025,
    cardholderName: "satoshi nakamoto",
    address: "Test",
    city: "Seattle",
    district: "WA",
    postalCode: "11111",
    country: "US",
  };

  componentDidMount() {
    this.callApi()
      .then((res) => this.setState({ message: res.message }))
      .catch((err) => console.log(err));
  }

  callApi = async () => {
    const response = await fetch(SERVER_URL + "/api/hello");
    console.log("response from server: ", response);
    const body = await response.json();
    console.log("got response from api: ", body);

    return body;
  };

  handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    console.log("submitting form!");
    event.preventDefault();
    const response = await fetch(SERVER_URL + "/payment", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        amount: this.state.amount,
        description: this.state.description,
        email: this.state.email,
        phoneNumber: this.state.phoneNumber,
        verificationMethod: "cvv",
        cvv: this.state.cvv,
        sourceType: "card",
        expirationMonth: this.state.expirationMonth,
        expirationYear: this.state.expirationYear,
        name: this.state.cardholderName,
        address: this.state.address,
        city: this.state.city,
        district: this.state.district,
        postalCode: this.state.postalCode,
        country: this.state.country,
      }),
    });

    const responseBody = await response.text();
    this.setState({ responseToPost: responseBody });
  };

  render() {
    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h2>Welcome to Tiden</h2>
          <p>{this.state.message}</p>
        </header>

        <p>{this.state.responseToPost}</p>

        <form className="payment-form" onSubmit={this.handleSubmit}>
          <div className="form-group">
            <label>Amount</label>
            <input
              type="text"
              className="form-control"
              value={this.state.amount}
              onChange={(e) => this.setState({ amount: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label>
              Description
              <input
                type="text"
                className="form-control"
                value={this.state.description}
                onChange={(e) => this.setState({ description: e.target.value })}
              />
            </label>
          </div>
          <div className="form-group">
            <label>
              Cardholder Name
              <input
                type="text"
                className="form-control"
                value={this.state.cardholderName}
                onChange={(e) =>
                  this.setState({ cardholderName: e.target.value })
                }
              />
            </label>
          </div>
          <div className="form-group">
            <label>
              Email
              <input
                type="text"
                className="form-control"
                value={this.state.email}
                onChange={(e) => this.setState({ email: e.target.value })}
              />
            </label>
          </div>
          <div className="form-group">
            <label>
              Phone Number
              <input
                type="text"
                className="form-control"
                value={this.state.phoneNumber}
                onChange={(e) => this.setState({ phoneNumber: e.target.value })}
              />
            </label>
          </div>
          <div className="form-group">
            <label>
              CVV
              <input
                type="text"
                className="form-control"
                value={this.state.cvv}
                onChange={(e) => this.setState({ cvv: e.target.value })}
              />
            </label>
          </div>
          <div className="form-group">
            <label>
              Expiration Month
              <input
                type="number"
                className="form-control"
                value={this.state.expirationMonth}
                onChange={(e) =>
                  this.setState({ expirationMonth: e.target.value })
                }
              />
            </label>
          </div>
          <div className="form-group">
            <label>
              Expiration Year
              <input
                type="number"
                className="form-control"
                value={this.state.expirationYear}
                onChange={(e) =>
                  this.setState({ expirationYear: e.target.value })
                }
              />
            </label>
          </div>
          <div className="form-group">
            <label>
              Address
              <input
                type="text"
                className="form-control"
                value={this.state.address}
                onChange={(e) => this.setState({ address: e.target.value })}
              />
            </label>
          </div>
          <div className="form-group">
            <label>
              City
              <input
                type="text"
                className="form-control"
                value={this.state.city}
                onChange={(e) => this.setState({ city: e.target.value })}
              />
            </label>
          </div>
          <div className="form-group">
            <label>
              District
              <input
                type="text"
                className="form-control"
                value={this.state.district}
                onChange={(e) => this.setState({ district: e.target.value })}
              />
            </label>
          </div>
          <div className="form-group">
            <label>
              Postal Code
              <input
                type="text"
                className="form-control"
                value={this.state.postalCode}
                onChange={(e) => this.setState({ postalCode: e.target.value })}
              />
            </label>
          </div>
          <div className="form-group">
            <label>
              Country
              <input
                type="text"
                className="form-control"
                value={this.state.country}
                onChange={(e) => this.setState({ country: e.target.value })}
              />
            </label>
          </div>
          <div className="form-group"></div>

          <button type="submit">Submit</button>
        </form>
      </div>
    );
  }
}

export default App;
