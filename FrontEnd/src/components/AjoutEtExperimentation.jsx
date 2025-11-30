import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AjouterAlgorithme from './AjouterAlgorithme';
import CreerExperimentation from './CreerExperimentation';
import './AjoutEtExperimentation.css';

const AjoutEtExperimentation = () => {
  const navigate = useNavigate();
  const [isAddingAlgo, setIsAddingAlgo] = useState(true);
  // حالة وبيانات التاريخ يمكن الإبقاء عليها هنا أو نقلها حسب حاجتك
  const [historiqueData, setHistoriqueData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // دالة لتحميل البيانات وعرضها (يمكن نقلها أيضاً حسب التنظيم)
  const handleFetchHistorique = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch('http://localhost:8049/interaction/send-Historique');
      if (!response.ok) {
        throw new Error(`Erreur ${response.status}: ${response.statusText}`);
      }
      const data = await response.json();
      setHistoriqueData(data);
    } catch (error) {
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <div className="form-container">
        {/* Tabs */}
        <div className="tabs">
          <button
            className={isAddingAlgo ? 'active' : ''}
            onClick={() => setIsAddingAlgo(true)}
          >
            Ajouter Algorithme
          </button>
          <button
            className={!isAddingAlgo ? 'active' : ''}
            onClick={() => setIsAddingAlgo(false)}
          >
            Créer Expérimentation
          </button>
        </div>

       {/* Content */}
{isAddingAlgo ? (
  <AjouterAlgorithme />
) : (
  <>
    <CreerExperimentation />

    {/* Show this only when in "Créer Expérimentation" mode */}
    <button
      onClick={() => navigate('/historique')}
      className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
    >
      Vérifier les données de l'historique
    </button>

    {/* Display loading or error message */}
    {loading && <p>Chargement des données...</p>}
    {error && <p style={{ color: 'red' }}>Erreur: {error}</p>}

    {/* Display the fetched data */}
    {historiqueData.length > 0 && (
      <div>
        <h3>Historique:</h3>
        <pre>{JSON.stringify(historiqueData, null, 2)}</pre>
      </div>
    )}
  </>
)}
      </div>
    </div>
  );
};

export default AjoutEtExperimentation;
