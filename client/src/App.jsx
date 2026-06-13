import { useState } from "react";

export default function App() {
  const [value, setValue] = useState(null);

  const fetchRandom = async () => {
    const res = await fetch("http://localhost:8080/api/random");
    const data = await res.json();
    setValue(data.value);
  };

  return (
    <div>
      <button onClick={fetchRandom}>Get random number</button>
      <span style={{ marginLeft: "10px" }}>
        {value !== null ? value : "No value yet"}
      </span>
    </div>
  );
}
