import logo from "../../logo.svg";

import { Navbar, Container, Nav } from "react-bootstrap";

function MainNavigation() {
  return (
    <header>
      <Navbar bg="dark" variant="dark">
        <Container>
          <Navbar.Brand href="/">
            <img
              alt=""
              src={logo}
              width="30"
              height="30"
              className="d-inline-block align-top"
            />{" "}
            Tiden
          </Navbar.Brand>
          <Nav>
            <Nav.Link href="/dashboard">Dashboard</Nav.Link>
          </Nav>
        </Container>
      </Navbar>
    </header>
  );
}

export default MainNavigation;
