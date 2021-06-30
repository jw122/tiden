import React from 'react';
import './App.css';
import {BrowserRouter as Router, Link, Route, Switch} from "react-router-dom";

function App() {
  return (
      <div className="App">
        <header className="App-header">
          <p>
            Welcome to Tiden
          </p>
          <Router>
            <div>
              <nav>
                <ul>
                  <li>
                    <Link to="/">Home</Link>
                  </li>
                  <li>
                    <Link to="/users">Users</Link>
                  </li>
                </ul>
              </nav>
              <div>
                <Calculator left={1} operator="+" right={2} />
              </div>
              {/* A <Switch> looks through its children <Route>s and
            renders the first one that matches the current URL. */}
              <Switch>
                <Route path="/users">
                  <Users/>
                </Route>
                <Route path="/">
                  <Home/>
                </Route>
              </Switch>
            </div>
          </Router>
        </header>
      </div>
  );
}

function Home() {
  return <h2>Home</h2>;
}

function Users() {
  return <h2>Users</h2>;
}

const operations = {
  '+': (left: number, right: number): number => left + right,
  '-': (left: number, right: number): number => left - right,
  '*': (left: number, right: number): number => left * right,
  '/': (left: number, right: number): number => left / right,
}
type CalculatorProps = {
  left: number
  operator: keyof typeof operations
  right: number
}
function Calculator({left, operator, right}: CalculatorProps) {
  const result = operations[operator](left, right)
  return (
      <div>
        <code>
          {left} {operator} {right} = <output>{result}</output>
        </code>
      </div>
  )
}

export default App;
