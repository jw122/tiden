import TransferForm from "../components/TransferForm";

function Transfer() {
  return (
    <div style={{ marginTop: "5%" }}>
      <h1 style={{ textAlign: "center" }}>Make a Transfer</h1>
      <h5 style={{ textAlign: "center" }}>
        Enter the address of any blockchain wallet on Ethereum.
      </h5>
      <TransferForm />
    </div>
  );
}

export default Transfer;
