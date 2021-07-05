import {Button, ButtonGroup, Col, Container, Row} from "react-bootstrap";
import "./Dashboard.css";
import React from "react";
import axiosInstance from "../utils/Http";

const Dashboard = () => {

    const [merchantBalance, setMerchantBalance] = React.useState(null);
    // TODO: get merchant it after login and accounts are implmented
    const merchantId = 1;

    React.useEffect(() => {
        console.log("component mounted, making an API call");

        axiosInstance.get(`/merchant/balance/${merchantId}`)
            .then((response) => {

                console.log('Received successful response for merchant balance', response);
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
                        <h4>Gross Volume last 30 days</h4>
                        <h2>$128,000.00</h2>
                         {new Date().toDateString()}
                    </Col>
                    <Col>
                        <h4>USDC Balance</h4>
                        <h2>${merchantBalance}</h2>
                    </Col>
                    <Col>
                        <h4>Payouts</h4>
                        <h2>$128,000.00</h2>
                    </Col>
                </Row>
                <Row>
                    <br/><br/><br/>
                </Row>
                <Row>
                    <h2>Operations</h2>
                    <br/>
                    <ButtonGroup vertical className="mr-2" size='lg'>
                        <Button variant="dark" type="submit">Transfer balance to another
                            wallet</Button>
                        <br />
                        <Button variant="dark" type="submit">Payout to bank</Button>
                    </ButtonGroup>
                </Row>
            </Container>
    );
}

export default Dashboard;
