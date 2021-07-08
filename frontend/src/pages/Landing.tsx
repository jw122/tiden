import logo from "../logo.svg";
import { Col, Container, Row, Image, Button } from "react-bootstrap";
import { useHistory } from "react-router-dom";

import "./Landing.css";

const Landing = () => {
  const history = useHistory();
  const handleClick = () => history.push("/sign-up");

  return (
    <div>
      <Container className="Landing-main-container">
        <embed src={logo} className="App-logo" />
        <Row className="Landing-main-container">
          <h1>Seamless payments with credit cards and crypto</h1>
          <br />
          <h5>
            Take payments with credit cards or crypto, and earn interest on your
            balances immediately.
          </h5>
        </Row>

        <Row className="cta-button-row">
          <Button variant="warning" size="lg" onClick={handleClick}>
            Start Earning Now
          </Button>
        </Row>

        <Row>
          <Col sm>
            <Image
              src={process.env.PUBLIC_URL + "/img/screenshot_shibainu.png"}
              fluid
            />
          </Col>
          <Col sm>
            <Image
              src={process.env.PUBLIC_URL + "/img/screenshot_merchant_dash.png"}
              fluid
            />
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default Landing;
