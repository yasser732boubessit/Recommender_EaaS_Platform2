import React, { useState } from 'react';

const CreerExperimentation = () => {
  const [experiment, setExperiment] = useState({
    selectedAlgos: [],
    dataset: '',
    evaluationType: '',
    windowSize: '',
    topK: ''
  });

  const handleExperimentChange = (e) => {
    setExperiment({ ...experiment, [e.target.name]: e.target.value });
  };

  const handleCheckboxChange = (name) => {
    setExperiment((prev) => {
      const alreadySelected = prev.selectedAlgos.includes(name);
      return {
        ...prev,
        selectedAlgos: alreadySelected
          ? prev.selectedAlgos.filter((a) => a !== name)
          : [...prev.selectedAlgos, name]
      };
    });
  };

  const handleSendItems = async () => {
    if (
      !experiment.selectedAlgos.length ||
      !experiment.dataset ||
    
      !experiment.windowSize ||
      !experiment.topK
    ) {
      alert("❌ يرجى ملء جميع الحقول قبل إرسال البيانات.");
      return;
    }

    const payload = {
      algorithm: Array.isArray(experiment.selectedAlgos)
        ? experiment.selectedAlgos
        : [experiment.selectedAlgos],
      dataset: experiment.dataset,
      evaluationType: experiment.evaluationType,
      windowSize: experiment.windowSize,
      topK: experiment.topK
    };

    console.log('✅ Payload envoyé au backend:', JSON.stringify(payload, null, 2));

    try {
      const response = await fetch('http://localhost:8049/interaction/send-items', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
      });

      const responseText = await response.text();
      console.log('Status:', response.status);
      console.log('Response Text:', responseText);

      if (response.ok) {
        alert("✅ Succès: " + responseText);
      } else {
        alert(`❌ Erreur HTTP ${response.status}: ${response.statusText}\nMessage: ${responseText}`);
      }
    } catch (error) {
      console.error('Détails de l’erreur:', error);
      alert("❌ Erreur lors de l'envoi: " + error.message);
    }
  };

  return (
    <>
      <h2>Créer une expérimentation</h2>
      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1rem' }}>
        {['CoOccurrence', 'ItemItemCF', 'MostPopular', 'MostRecent', 'Random', 'RecentlyClicked', 'RecentlyPopular'].map(algo => (
          <label key={algo} style={{ width: '45%' }}>
            <input
              type="checkbox"
              onChange={() => handleCheckboxChange(algo)}
              checked={experiment.selectedAlgos.includes(algo)}
            />
            {algo}
          </label>
        ))}
      </div>

      <select name="dataset" value={experiment.dataset} onChange={handleExperimentChange}>
        <option value="">-- Sélectionner dataset --</option>
        <option value="6H">plista418 6 heures</option>
        <option value="1J">plista418 1 jour</option>
        <option value="1W">plista418 1 semaine</option>
        <option value="1M">plista418 1 mois</option>
      </select>

  

      <input
        name="windowSize"
        placeholder="Fenêtre d'évaluation (min)"
        value={experiment.windowSize}
        onChange={handleExperimentChange}
      />

      <input
        name="topK"
        placeholder="Top-k"
        value={experiment.topK}
        onChange={handleExperimentChange}
      />

      <button onClick={handleSendItems}>Lancer l’expérimentation</button>

      
    </>
  );
};

export default CreerExperimentation;
