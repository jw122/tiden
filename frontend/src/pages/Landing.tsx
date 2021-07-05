import logo from "../logo.svg";
import {Col, Container, Row, Image, Button} from "react-bootstrap";
import { useHistory } from 'react-router-dom';

import "./Landing.css";


const Landing = () => {
    // this should eventually come from the server
    const apy = 5;

    const history = useHistory();
    const handleClick = () => history.push('/sign-up');

    return (
        <div>
            <Container className="Landing-main-container">
                <Row className="Landing-main-container">
                    <embed src={logo} className="App-logo"/>
                    <h1>Easy payments with credit cards and crypto</h1>
                    Take payments from credit cards or crypto, and earn {apy}% interest on your balances.
                </Row>
                <br/><br/><br/>
                <Row>
                    <Button onClick={handleClick}> Start earning now </Button>
                </Row>
                <br/><br/><br/>
                <Row>
                    <Col sm>
                        <Image src={process.env.PUBLIC_URL + '/img/screenshot_shibainu.png'} fluid />
                    </Col>
                    <Col sm>
                        <Image src={process.env.PUBLIC_URL + '/img/screenshot_merchant_dash.png'} fluid />
                    </Col>
                </Row>

            </Container>
        </div>
    );
}

export default Landing;
