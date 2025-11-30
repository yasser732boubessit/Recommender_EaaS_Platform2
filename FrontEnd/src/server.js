// server/server.js
const express = require('express');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.urlencoded({ extended: true }));
app.use(express.json());

app.post('/api/login', (req, res) => {
  const { username, password } = req.body;
  console.log('Received:', username, password);
  res.json({ message: 'Login received' });
});

app.listen(5000, () => {
  console.log('Server running on http://localhost:5000');
});
