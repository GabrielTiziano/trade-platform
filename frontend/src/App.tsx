import { useState } from "react";

function App() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [document, setDocument] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

  function signup() {
    setMessage("success");
  }

  return (
    <div>
      <input
        className="input-name"
        value={name}
        onChange={(e) => setName(e.target.value)}
      />
      <input
        className="input-email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
      />
      <input
        className="input-document"
        value={document}
        onChange={(e) => setDocument(e.target.value)}
      />
      <input
        className="input-password"
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
      <button className="button-signup" onClick={signup}>
        Signup
      </button>
      <span className="span-message">{message}</span>
    </div>
  );
}

export default App;
