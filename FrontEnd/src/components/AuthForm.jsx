import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './AuthForm.css';

const AuthForm = () => {
  const navigate = useNavigate();
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({ email: '', password: '', username: '' });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.email || !formData.password || (!isLogin && !formData.username)) {
      alert("❌ Veuillez remplir tous les champs.");
      return;
    }

    const authPayload = {
      email: formData.email,
      password: formData.password,
      username: !isLogin ? formData.username : undefined
    };

    const url = isLogin ? 'http://localhost:8049/auth/login' : 'http://localhost:8049/auth/signup';

    console.log("📤 Envoi des données:", JSON.stringify(authPayload, null, 2));

    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(authPayload)
      });

      const responseText = await response.text();
      console.log("Status:", response.status);
      console.log("Response Text:", responseText);

      if (response.ok) {
        alert('✅ Connexion / Inscription réussie.');
        if (isLogin) {
          navigate('/experiment');
        }
      } else {
        alert(`❌ Erreur HTTP ${response.status}: ${response.statusText}\nMessage: ${responseText}`);
      }
    } catch (error) {
      console.error("Erreur lors de l'envoi:", error);
      alert("❌ Échec de l'opération: " + error.message);
    }
  };

  return (
    <div className="auth-container">
      <div className="form-container">
        {/* Tabs */}
        <div className="tabs">
          <button
            className={isLogin ? 'active' : ''}
            onClick={() => setIsLogin(true)}
          >
            Se connecter
          </button>
          <button
            className={!isLogin ? 'active' : ''}
            onClick={() => setIsLogin(false)}
          >
            Créer un compte
          </button>
        </div>

        {/* Form */}
        <h2>{isLogin ? 'Bienvenue' : 'Créer un compte'}</h2>
        <form onSubmit={handleSubmit}>
          {!isLogin && (
            <input
              name="username"
              placeholder="Nom d'utilisateur"
              onChange={handleChange}
              required
            />
          )}
          <input
            name="email"
            type="email"
            placeholder="Adresse e-mail"
            onChange={handleChange}
            required
          />
          <input
            name="password"
            type="password"
            placeholder="Mot de passe"
            onChange={handleChange}
            required
          />
          <button type="submit">
            {isLogin ? 'Se connecter' : "S'inscrire"}
          </button>
        </form>

        {/* Forgot password link */}
        {isLogin && (
          <div className="forgot-password">
            <a href="#">Mot de passe oublié ?</a>
          </div>
        )}

        {/* Switch link */}
        <div className="signup-link">
          <p>
            {isLogin ? "Pas encore de compte ?" : 'Vous avez déjà un compte ?'}{' '}
            <span onClick={() => setIsLogin(!isLogin)}>
              {isLogin ? 'Créer un compte' : 'Se connecter'}
            </span>
          </p>
        </div>
      </div>
    </div>
  );
};

export default AuthForm;
