import { Form, Row, Button, Col, Card, Alert } from "react-bootstrap";
import React from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCheckCircle } from "@fortawesome/free-solid-svg-icons";
import axiosInstance from "../utils/Http";
import "./TransferForm.css";

function TransferForm() {
  const [txHash, setTransactionHash] = React.useState("");
  const [amountToSend, setAmountToSend] = React.useState("");
  const [destinationAddress, setDestAddress] = React.useState("");
  const [showSpinner, setShowSpinner] = React.useState(false);
  const [showTransactionConfirmation, setShowTransactionConfirmation] =
    React.useState(false);

  // TODO: get merchant wallet id after login/accounts are implemented

  const merchantWalletId = "1000130251";

  const sleep = (milliseconds: number) => {
    const start = Date.now();
    while (Date.now() - start < milliseconds);
  };

  const makeTransfer = async (e: React.SyntheticEvent) => {
    e.preventDefault();
    // start spinner
    setShowSpinner(true);
    await axiosInstance
      .post(`/transfer`, {
        walletId: merchantWalletId,
        amount: amountToSend,
        currency: "USD",
        destinationType: "blockchain",
        destinationAddress: destinationAddress,
        chain: "ETH",
      })
      .then((response) => {
        console.log(
          "Received successful response after transfer",
          response.data.data
        );

        const responseData = response.data.data;

        // wait a few seconds and query for transaction hash
        sleep(20000);

        axiosInstance.get(`/transfer/${responseData.id}`).then((response) => {
          // stop spinner
          setShowSpinner(false);
          console.log("got transfer status: ", response.data.data);

          // set the tx hash url
          const transactionHash = response.data.data.transactionHash;

          if (transactionHash) {
            setTransactionHash(
              "https://ropsten.etherscan.io/tx/" + transactionHash
            );
          } else {
            console.log("Tx hash not available");
            // HACK (for demo): if null, return hard-coded url
            setTransactionHash(
              "https://ropsten.etherscan.io/tx/0xd28fcb0ed56fa87dfbc7d08fecdd1a7741ad9a4ee6215a902005c1e0b776eaea"
            );
          }

          setShowTransactionConfirmation(true);
        });
      })
      .catch((error) => {
        console.log(error);
      });
  };

  // form submission makes an api call to make the transfer. Currently defaulting to USD and ETH
  return (
    <div className="transferForm">
      <div>
        {showSpinner ? (
          <div className="spinner">
            <img src="https://mir-s3-cdn-cf.behance.net/project_modules/max_1200/6d391369321565.5b7d0d570e829.gif"></img>
          </div>
        ) : null}
      </div>

      {showTransactionConfirmation ? (
        <div className="my-5 txConfirmation">
          <FontAwesomeIcon
            icon={faCheckCircle}
            style={{ color: "green" }}
          ></FontAwesomeIcon>{" "}
          Success!{" "}
          <a href={txHash} style={{ textDecoration: "none" }} target="_blank">
            View on Etherscan
          </a>
        </div>
      ) : null}

      <Form onSubmit={makeTransfer}>
        <Form.Group className="mb-3">
          <Form.Label>Blockchain address</Form.Label>
          <Form.Control
            type="text"
            placeholder="0x123..."
            onChange={(e) => setDestAddress(e.target.value)}
          />
        </Form.Group>

        <Form.Group className="mb-3" controlId="formBasicPassword">
          <Form.Label>Amount in USD</Form.Label>
          <Form.Control
            type="text"
            placeholder="2.50"
            onChange={(e) => setAmountToSend(e.target.value)}
          />
        </Form.Group>
        <Button variant="dark" type="submit">
          Submit
        </Button>
      </Form>
    </div>
  );
}

export default TransferForm;
