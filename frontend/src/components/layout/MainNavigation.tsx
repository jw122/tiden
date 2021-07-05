import logo from "../../logo.svg";

import { Navbar, Container, Nav } from "react-bootstrap";

function MainNavigation() {
  return (
    <header>
      <Navbar>
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
          <Nav.Link href="/dashboard" style={{ color: "grey" }}>
            Dashboard
          </Nav.Link>
        </Container>
      </Navbar>
    </header>
  );
}

export default MainNavigation;
