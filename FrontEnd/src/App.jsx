import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import AuthForm from './components/AuthForm';
import AjoutEtExperimentation from './components/AjoutEtExperimentation';
import HistoriqueList from './components/HistoriqueList';
import Historique from './components/Historique';

const App = () => (
  <Router>
    <Routes>
      <Route path="/" element={<AuthForm />} />
      <Route path="/experiment" element={<AjoutEtExperimentation />} />
      <Route path="/historique" element={<HistoriqueList />} />
      <Route path="/historique/:id" element={<Historique />} />
    </Routes>
  </Router>
);

export default App;
