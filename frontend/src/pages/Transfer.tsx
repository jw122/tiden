import TransferForm from "../components/TransferForm";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCoffee, faMoneyBill } from "@fortawesome/free-solid-svg-icons";

function Transfer() {
  return (
    <div style={{ marginTop: "5%" }}>
      <h1 style={{ textAlign: "center" }}>Make a Transfer</h1>
      <h5 style={{ textAlign: "center" }}>
        <FontAwesomeIcon icon={faMoneyBill} />
      </h5>
      <h5 style={{ textAlign: "center" }}>
        Send your USDC to any blockchain wallet on Ethereum.
      </h5>
      <TransferForm />
    </div>
  );
}

export default Transfer;
