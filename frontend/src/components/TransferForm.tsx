import { Form, Row, Button, Col, Card, Alert } from "react-bootstrap";
import React, { useRef } from "react";
import axiosInstance from "../utils/Http";
import "./TransferForm.css";

function TransferForm() {
  const [txHash, setTransactionHash] = React.useState("");
  const [amountToSend, setAmountToSend] = React.useState("");
  const [destinationAddress, setDestAddress] = React.useState("");

  // TODO: get merchant wallet id after login/accounts are implemented

  const merchantWalletId = "1000130251";

  const sleep = (milliseconds: number) => {
    const start = Date.now();
    while (Date.now() - start < milliseconds);
  };

  const makeTransfer = async (e: React.SyntheticEvent) => {
    e.preventDefault();
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
        sleep(15000);

        axiosInstance.get(`/transfer/${responseData.id}`).then((response) => {
          console.log("got transfer status: ", response.data.data);
          // set the tx hash url
          const txHash = response.data.data.transactionHash;
          setTransactionHash(
            "Success! You can now view your transaction at https://ropsten.etherscan.io/tx/" +
              txHash
          );
        });
      })
      .catch((error) => {
        console.log(error);
      });
  };
  // form submission makes an api call to make the transfer. Currently defaulting to USD and ETH
  return (
    <div className="transferForm">
      <div className="my-5">
        <p>
          <b>{txHash}</b>
        </p>
      </div>

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
