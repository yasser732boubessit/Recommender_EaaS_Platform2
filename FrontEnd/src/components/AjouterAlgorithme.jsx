import React, { useState, useEffect, useRef } from 'react';
import $ from 'jquery';
import 'datatables.net-dt/js/dataTables.dataTables';
import 'datatables.net-dt/css/dataTables.dataTables.css';
import './HistoriqueList.css';

const AjouterAlgorithme = () => {
  // تعريف الحالة algorithm مع الحقول المطلوبة
  const [algorithm, setAlgorithm] = useState({
    name: '',
    urlItems: '',
    urlEvents: '',
    urlReco: ''
  });

  const [algorithmsList, setAlgorithmsList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showTable, setShowTable] = useState(false);
  const tableRef = useRef(null);
  const dataTable = useRef(null);

  // Modal état
  const [showModal, setShowModal] = useState(false);
  const [editAlgo, setEditAlgo] = useState(null);
  const [originalName, setOriginalName] = useState('');

  // Handler لتحديث قيم الحقول في النموذج الإضافة
  const handleAlgoChange = (e) => {
    const { name, value } = e.target;
    setAlgorithm(prev => ({ ...prev, [name]: value }));
  };

  // دالة الإضافة مع التحقق من الحقول
  const handleAlgoSubmit = async () => {
    if (!algorithm.name || !algorithm.urlItems || !algorithm.urlEvents || !algorithm.urlReco) {
      alert("❌ يرجى ملء جميع الحقول.");
      return;
    }

    try {
      const response = await fetch("http://localhost:8049/algorithm/add-algorithm", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(algorithm)
      });

      if (response.ok) {
        alert("✅ Ajouté avec succès !");
        setAlgorithm({ name: '', urlItems: '', urlEvents: '', urlReco: '' });
        fetchAlgorithms();
      } else {
        const text = await response.text();
        alert(`❌ Erreur HTTP ${response.status} : ${text}`);
      }
    } catch (error) {
      alert("❌ Erreur : " + error.message);
    }
  };

  // تحميل قائمة الخوارزميات
  const fetchAlgorithms = async () => {
    try {
      const response = await fetch("http://localhost:8049/algorithm/all-algorithms");
      if (response.ok) {
        const data = await response.json();
        setAlgorithmsList(data);
        setLoading(false);
      } else {
        console.error("Erreur lors de la récupération :", response.statusText);
      }
    } catch (error) {
      console.error("Erreur :", error);
    }
  };

  useEffect(() => {
    fetchAlgorithms();
  }, []);

  // تهيئة DataTable عند ظهور الجدول ووجود بيانات
  useEffect(() => {
    if (!loading && showTable && algorithmsList.length > 0) {
      if ($.fn.DataTable.isDataTable(tableRef.current)) {
        $(tableRef.current).DataTable().destroy();
      }
      dataTable.current = $(tableRef.current).DataTable();
    }
  }, [loading, showTable, algorithmsList]);

  // حذف خوارزمية
  const handleDelete = async (name) => {
    if (window.confirm(`🗑️ Êtes-vous sûr de vouloir supprimer l'algorithme "${name}" ?`)) {
      try {
        const response = await fetch(`http://localhost:8049/algorithm/delete-algorithm/${encodeURIComponent(name)}`, {
          method: "DELETE"
        });
        if (response.ok) {
          alert("✅ Algorithme supprimé !");
          fetchAlgorithms();
        } else {
          alert("❌ Échec de la suppression.");
        }
      } catch (error) {
        alert("❌ Erreur : " + error.message);
      }
    }
  };

  // فتح المودال مع نسخة للتعديل
  const openEditModal = (algo) => {
    setEditAlgo({ ...algo });
    setOriginalName(algo.name);
    setShowModal(true);
  };

  // إغلاق المودال
  const closeModal = () => {
    setShowModal(false);
    setEditAlgo(null);
    setOriginalName('');
  };

  // تعديل بيانات المودال
  const handleEditChange = (e) => {
    const { name, value } = e.target;
    setEditAlgo(prev => ({ ...prev, [name]: value }));
  };

  // تحديث الخوارزمية بعد التعديل
  const handleUpdate = async () => {
    if (!editAlgo.name || !editAlgo.urlItems || !editAlgo.urlEvents || !editAlgo.urlReco) {
      alert("❌ Tous les champs sont requis !");
      return;
    }
    try {
      const response = await fetch(`http://localhost:8049/algorithm/update-algorithm/${encodeURIComponent(originalName)}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          name: editAlgo.name,
          urlItems: editAlgo.urlItems,
          urlEvents: editAlgo.urlEvents,
          urlReco: editAlgo.urlReco
        })
      });
      if (response.ok) {
        alert("✅ Algorithme mis à jour avec succès !");
        fetchAlgorithms();
        closeModal();
      } else {
        const text = await response.text();
        alert(`❌ Échec mise à jour: ${text}`);
      }
    } catch (error) {
      alert("❌ Erreur : " + error.message);
    }
  };

  return (
    <div className="historique-container">
      <div className="ajout-form">
        <input name="name" placeholder="Nom" value={algorithm.name} onChange={handleAlgoChange} />
        <input name="urlItems" placeholder="URL Items" value={algorithm.urlItems} onChange={handleAlgoChange} />
        <input name="urlEvents" placeholder="URL Événements" value={algorithm.urlEvents} onChange={handleAlgoChange} />
        <input name="urlReco" placeholder="URL Recommandation" value={algorithm.urlReco} onChange={handleAlgoChange} />
        <button onClick={handleAlgoSubmit}>Ajouter</button>
      </div>

      <div style={{ marginBottom: 20 }}>
        <button className="historique-button" onClick={() => setShowTable(!showTable)}>
          {showTable ? '🛑 Masquer la liste' : '📄 Afficher la liste des algorithmes'}
        </button>
      </div>

      {showTable && (
        <div className="historique-table-container">
          <table ref={tableRef} className="display historique-table" style={{ width: '100%' }}>
            <thead>
              <tr>
                <th>#</th>
                <th>Nom</th>
                <th>URL Items</th>
                <th>URL Événements</th>
                <th>URL Recommandation</th>
                <th>Modifier</th>
                <th>Supprimer</th>
              </tr>
            </thead>
            <tbody>
              {algorithmsList.map((algo, index) => (
                <tr key={algo.id || algo.name}>
                  <td>{index + 1}</td>
                  <td>{algo.name}</td>
                  <td>{algo.urlItems}</td>
                  <td>{algo.urlEvents}</td>
                  <td>{algo.urlReco}</td>
                  <td>
                    <button className="historique-button" onClick={() => openEditModal(algo)}>✏️ Modifier</button>
                  </td>
                  <td>
                    <button
                      className="historique-button"
                      style={{ backgroundColor: 'red' }}
                      onClick={() => handleDelete(algo.name)}
                    >
                      🗑️ Supprimer
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {showModal && editAlgo && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>Modifier l'algorithme</h3>
            <label>
              Nom :
              <input name="name" value={editAlgo.name} onChange={handleEditChange} />
            </label>
            <label>
              URL Items :
              <input name="urlItems" value={editAlgo.urlItems} onChange={handleEditChange} />
            </label>
            <label>
              URL Événements :
              <input name="urlEvents" value={editAlgo.urlEvents} onChange={handleEditChange} />
            </label>
            <label>
              URL Recommandation :
              <input name="urlReco" value={editAlgo.urlReco} onChange={handleEditChange} />
            </label>

            <div style={{ marginTop: 15 }}>
              <button className="historique-button" onClick={handleUpdate}>💾 Enregistrer</button>
              <button className="historique-button" style={{ marginLeft: 10 }} onClick={closeModal}>❌ Annuler</button>
            </div>
          </div>
        </div>
      )}

      {/* CSS modal */}
      <style>{`
        .modal-overlay {
          position: fixed;
          top:0; left:0; right:0; bottom:0;
          background: rgba(0,0,0,0.5);
          display: flex;
          justify-content:center;
          align-items:center;
          z-index: 9999;
        }
        .modal-content {
          background: white;
          padding: 20px;
          border-radius: 8px;
          width: 300px;
          box-shadow: 0 2px 10px rgba(0,0,0,0.3);
          display: flex;
          flex-direction: column;
        }
        .modal-content label {
          margin-bottom: 10px;
          font-weight: bold;
        }
        .modal-content input {
          width: 100%;
          padding: 5px;
          margin-top: 5px;
        }
        .historique-button {
          cursor: pointer;
          padding: 5px 10px;
          border: none;
          border-radius: 4px;
          background-color: #1976d2;
          color: white;
          font-weight: 600;
          transition: background-color 0.3s ease;
        }
        .historique-button:hover {
          background-color: #0d47a1;
        }
      `}</style>
    </div>
  );
};

export default AjouterAlgorithme;
