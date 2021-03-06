import { Button, ButtonGroup, Col, Container, Row } from "react-bootstrap";
import { Link } from "react-router-dom";
import "./Dashboard.css";
import React from "react";
import axiosInstance from "../utils/Http";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faChartLine, faCalendar } from "@fortawesome/free-solid-svg-icons";

const Dashboard = () => {
  const [merchantBalance, setMerchantBalance] = React.useState(null);
  // TODO: get merchant it after login and accounts are implmented
  const merchantId = 1;

  React.useEffect(() => {
    console.log("component mounted, making an API call");

    axiosInstance
      .get(`/merchant/balance/${merchantId}`)
      .then((response) => {
        console.log(
          "Received successful response for merchant balance",
          response
        );
        setMerchantBalance(response.data.amount);
      })
      .catch((error) => {
        console.log(error);
      });
  }, []);

  return (
    <Container className="Dashboard-main-container">
      <Row>
        <Col>
          <h4>30 Day Volume</h4>
          <h2>$47,273.58</h2>
          <p>
            <FontAwesomeIcon icon={faCalendar}></FontAwesomeIcon>{" "}
            {new Date().toDateString()}
          </p>
        </Col>
        <Col>
          <h4>USDC Balance</h4>
          <h2>${merchantBalance}</h2>
          <p>
            <FontAwesomeIcon icon={faChartLine}></FontAwesomeIcon> Earning 5.4%
            APY
          </p>
        </Col>
        <Col>
          <h4>Total Payouts</h4>
          <h2>$51,280.00</h2>
        </Col>
      </Row>
      <Row>
        <br />
        <br />
        <br />
      </Row>
      <Row>
        <h2>Operations</h2>
        <br />

        <ButtonGroup vertical className="mr-2" size="lg">
          <Button variant="dark" type="submit">
            <Link
              style={{ color: "white", textDecoration: "none" }}
              to="/transfer"
            >
              Transfer balance to another wallet
            </Link>
          </Button>
          <br />
          <Button variant="dark" type="submit">
            Payout to bank
          </Button>
        </ButtonGroup>
      </Row>
    </Container>
  );
};

export default Dashboard;
