import React, { Component } from "react";
import * as openpgp from "openpgp";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCoffee,
  faCreditCard,
  faCheckCircle,
} from "@fortawesome/free-solid-svg-icons";

import { Form, Row, Button, Col, Card, Alert } from "react-bootstrap";
import classes from "./PaymentForm.module.css";

// TODO: move to env
const SERVER_URL = "http://localhost:8080";

// TODO: change to function() declaration
class PaymentForm extends Component {
  state = {
    message: "",
    name: "",
    responseToPost: "",
    showPaymentResults: false,
    // payment fields (TODO: move into its own group?)
    amount: "10.50",
    description: "gift money",
    email: "hello@test.com",
    phoneNumber: "+12025550180",
    cardNumber: "5102420000000006",
    cvv: "123",
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

  // Gets public key and keyId from server
  getPCIPublicKey = async () => {
    const response = await fetch(SERVER_URL + "/encryption");

    const responseBody = await response.json();

    return {
      keyId: responseBody.data.keyId,
      publicKey: responseBody.data.publicKey,
    };
  };

  // TODO: move all API calls to a Client module/component
  encryptCredentials = async (cardCredentials: {
    number: string;
    cvv: string;
  }) => {
    const pciEncryptionKey = await this.getPCIPublicKey();

    console.log("Obtained key. Now encrypting credentials");
    const decodedPublicKey = atob(pciEncryptionKey.publicKey);
    const options = {
      message: openpgp.message.fromText(JSON.stringify(cardCredentials)),
      publicKeys: (await openpgp.key.readArmored(decodedPublicKey)).keys,
    };

    return openpgp.encrypt(options).then((ciphertext) => {
      return {
        encryptedCredentials: btoa(ciphertext.data),
        keyId: pciEncryptionKey.keyId,
      };
    });
  };

  handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    // Step 1: obtain the public key id and encrypt card credentials
    var cardCredentials = {
      number: this.state.cardNumber,
      cvv: this.state.cvv,
    };

    var encryptedData = await this.encryptCredentials(cardCredentials);

    // Step 2: Submit the payment
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
        cvv: encryptedData.encryptedCredentials,
        keyId: encryptedData.keyId,
        sourceType: "card",
        expirationMonth: this.state.expirationMonth,
        expirationYear: this.state.expirationYear,
        name: this.state.cardholderName,
        address: this.state.address,
        city: this.state.city,
        district: this.state.district,
        postalCode: this.state.postalCode,
        country: this.state.country,
        merchantId: "1",
      }),
    });

    const responseBody = await response.text();
    this.setState({ responseToPost: responseBody });
    this.setState({ showPaymentResults: true });
  };

  render() {
    return (
      <div>
        <header className="App-header">
          <div className="shadow-box-example z-depth-5"></div>
          <Card
            className={classes.merchantCard}
            style={{
              boxShadow: "1px 2px 7px 7px #D8D8D8",
              borderRadius: "10px",
              borderWidth: "0px",
            }}
          >
            <img
              src="https://res.cloudinary.com/sagacity/image/upload/c_crop,h_800,w_616,x_0,y_0/c_scale,w_640/v1419879339/iVegJ35_xfjlfu.gif"
              className={classes.merchantLogo}
              alt="logo"
            />
            <Card.Title className={classes.header}>Shiba Inu</Card.Title>
            <Card.Subtitle className="mb-2 text-muted">
              Minter of coins and memes. Thank you for worshipping me!
            </Card.Subtitle>
          </Card>
        </header>
        <Alert className={classes.tip} variant="warning">
          <FontAwesomeIcon icon={faCoffee} /> Send this creator or merchant some
          love, using your credit card of choice in <b>any currency</b>.
          <p>They will instantly receive your payment in USDC.</p>
        </Alert>

        {/* Supported cards */}
        <div className={classes.supportedCards}>
          <h5>
            <FontAwesomeIcon icon={faCreditCard} /> Supported Cards
          </h5>
          <img src="https://img.icons8.com/color/32/000000/visa.png" />
          <img src="https://img.icons8.com/color/32/000000/mastercard.png" />
        </div>

        {this.state.showPaymentResults ? (
          <Alert variant="success" className={classes.paymentStatus}>
            <p>
              <FontAwesomeIcon
                icon={faCheckCircle}
                style={{ marginRight: "2%" }}
              />
              {this.state.responseToPost}
            </p>
          </Alert>
        ) : null}
        <Form className={classes.paymentForm} onSubmit={this.handleSubmit}>
          <Form.Group className="m-4">
            <Form.Label>Amount</Form.Label>
            <Form.Control
              value={this.state.amount}
              onChange={(e) => this.setState({ amount: e.target.value })}
            />
          </Form.Group>

          <Form.Group className="m-4">
            <Form.Label>Description</Form.Label>
            <Form.Control
              value={this.state.description}
              onChange={(e) => this.setState({ description: e.target.value })}
            />
          </Form.Group>

          <Row className="m-2">
            <Form.Group as={Col}>
              <Form.Label>Email</Form.Label>
              <Form.Control
                type="email"
                value={this.state.email}
                onChange={(e) => this.setState({ email: e.target.value })}
              />
            </Form.Group>

            <Form.Group as={Col}>
              <Form.Label>Cardholder Name</Form.Label>
              <Form.Control
                type="text"
                value={this.state.cardholderName}
                onChange={(e) =>
                  this.setState({ cardholderName: e.target.value })
                }
              />
            </Form.Group>
          </Row>

          <Form.Group className="m-4">
            <Form.Label>Phone Number</Form.Label>
            <Form.Control
              value={this.state.phoneNumber}
              onChange={(e) => this.setState({ phoneNumber: e.target.value })}
            />
          </Form.Group>

          <Row className="m-2">
            <Form.Group as={Col}>
              <Form.Label>Card Number</Form.Label>
              <Form.Control
                type="text"
                value={this.state.cardNumber}
                onChange={(e) => this.setState({ cardNumber: e.target.value })}
              />
            </Form.Group>

            <Form.Group as={Col}>
              <Form.Label>CVV</Form.Label>
              <Form.Control
                type="text"
                value={this.state.cvv}
                onChange={(e) => this.setState({ cvv: e.target.value })}
              />
            </Form.Group>
          </Row>

          <Row className="m-2">
            <Form.Group as={Col}>
              <Form.Label>Expiration Month</Form.Label>
              <Form.Control
                type="number"
                value={this.state.expirationMonth}
                onChange={(e) =>
                  this.setState({ expirationMonth: e.target.value })
                }
              />
            </Form.Group>

            <Form.Group as={Col}>
              <Form.Label>Expiration Year</Form.Label>
              <Form.Control
                type="number"
                value={this.state.expirationYear}
                onChange={(e) =>
                  this.setState({ expirationYear: e.target.value })
                }
              />
            </Form.Group>
          </Row>

          <Form.Group className="m-4">
            <Form.Label>Address</Form.Label>
            <Form.Control
              value={this.state.address}
              onChange={(e) => this.setState({ address: e.target.value })}
            />
          </Form.Group>

          <Row className="m-2">
            <Form.Group as={Col}>
              <Form.Label>City</Form.Label>
              <Form.Control
                type="text"
                value={this.state.city}
                onChange={(e) => this.setState({ city: e.target.value })}
              />
            </Form.Group>

            <Form.Group as={Col}>
              <Form.Label>District</Form.Label>
              <Form.Control
                type="text"
                value={this.state.district}
                onChange={(e) => this.setState({ district: e.target.value })}
              />
            </Form.Group>
          </Row>

          <Row className="m-2">
            <Form.Group as={Col} controlId="formGridEmail">
              <Form.Label>Postal Code</Form.Label>
              <Form.Control
                type="text"
                value={this.state.postalCode}
                onChange={(e) => this.setState({ postalCode: e.target.value })}
              />
            </Form.Group>

            <Form.Group as={Col} controlId="formGridPassword">
              <Form.Label>Country</Form.Label>
              <Form.Control
                type="text"
                value={this.state.country}
                onChange={(e) => this.setState({ country: e.target.value })}
              />
            </Form.Group>
          </Row>

          <Button className="m-4" variant="warning" type="submit">
            Submit
          </Button>
        </Form>
      </div>
    );
  }
}

export default PaymentForm;
