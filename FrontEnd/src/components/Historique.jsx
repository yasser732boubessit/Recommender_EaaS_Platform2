import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  AreaChart,
  Area,
  ResponsiveContainer
} from 'recharts';
import './Historique.css';

const Historique = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [historique, setHistorique] = useState(null);
  const [allHistoriques, setAllHistoriques] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [timeSeriesData, setTimeSeriesData] = useState([]);

  useEffect(() => {
    fetch('http://localhost:8049/interaction/send-Historique')
      .then((res) => {
        if (!res.ok) {
          throw new Error('Problème de réseau');
        }
        return res.json();
      })
      .then((data) => {
        const filteredData = data.filter(item => Object.keys(item).length > 0);
        setAllHistoriques(filteredData);
        const found = filteredData.find(
          (item) => String(item.historiqueId) === String(id)
        );
        setHistorique(found || null);
      })
      .catch((err) => {
        setError(err.message || 'Erreur inconnue');
      })
      .finally(() => {
        setLoading(false);
      });
  }, [id]);

  // 🔁 تحميل بيانات évolution temporelle من endpoint dynamique  http://localhost:8049/interaction/historique/1/metrics 
  useEffect(() => {
    fetch(`http://localhost:8049/interaction/historique/${id}/metrics`)
      .then((res) => {
        if (!res.ok) {
          throw new Error('Problème de réseau');
        }
        return res.json();
      })
      .then((data) => {
        const formattedData = data.map((item) => ({
          timestamp: item.timestamp,
          ...item.metrics
        }));
        setTimeSeriesData(formattedData);
      })
      .catch((err) => {
        console.error('Erreur lors du chargement des données temporelles:', err);
      });
  }, [id]);

  if (loading) return <div className="text-center p-4">Chargement en cours...</div>;
  if (error) return <div className="text-red-600 p-4">Erreur : {error}</div>;
  if (!historique) return <div className="text-center p-4">Aucune donnée disponible</div>;

  const { algorithm, tester,expérimentation, data, windowSize, startDate, endDate, metrics } = historique;

  const metricsData = metrics
    ? Object.entries(metrics).map(([name, value]) => ({ name, value }))
    : [];

  const comparaisonData = [];
  if (historique && allHistoriques.length > 0) {
    const sameTesterHistoriques = allHistoriques.filter(
      (item) => item.expérimentation === historique.expérimentation
    );
    const metricNames = historique.metrics ? Object.keys(historique.metrics) : [];

    metricNames.forEach((metricName) => {
      const entry = { metric: metricName };
      sameTesterHistoriques.forEach((hist) => {
        if (hist.metrics && hist.algorithm) {
          entry[hist.algorithm] = hist.metrics[metricName];
        }
      });
      comparaisonData.push(entry);
    });
  }

  const formatTimestamp = (ts) => {
    const date = new Date(ts);
    return date.toLocaleDateString();
  };

  return (
    <div className="container mx-auto px-4 py-6">
      <button
        onClick={() => navigate(-1)}
        className="mb-4 px-4 py-2 bg-gray-200 rounded hover:bg-gray-300"
      >
        ← Retour
      </button>

      <div className="bg-white shadow-lg rounded-lg p-6 border">
        <h1 className="text-3xl font-bold mb-6">
          Détails de l’expérimentation {id}
        </h1>

        {metricsData.length > 0 && (
          <div className="metrics-layout">
            <div className="historique-info bg-white shadow-lg rounded-lg p-6 border">
              <h2 className="text-2xl font-semibold mb-4">Informations</h2>
              <div className="grid grid-cols-1 gap-2 text-base">
                <p><strong>Algorithme :</strong> {algorithm}</p>
                <p><strong>Testeur :</strong> {expérimentation}</p>
                <p><strong> Date :</strong> {data}</p>
                <p><strong>Taille de fenêtre :</strong> {windowSize}</p>
                <p><strong>Date de début :</strong> {startDate}</p>
                <p><strong>Date de fin :</strong> {endDate}</p>
              </div>
            </div>

          <div className="table-section bg-white shadow-lg rounded-lg p-6 border">
              <div className="evolution-chart">
                <h3 className="text-lg font-medium mb-2">Évolution temporelle</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <AreaChart data={timeSeriesData}>
                    <XAxis dataKey="timestamp" tickFormatter={formatTimestamp} />
                    <YAxis />
                    <Tooltip labelFormatter={formatTimestamp} />
                    <Area type="monotone" dataKey="PrecisionAtK" stroke="#3b82f6" fill="#3b82f6" fillOpacity={0.2} />
                    <Area type="monotone" dataKey="RecallAtK" stroke="#10b981" fill="#10b981" fillOpacity={0.2} />
                    <Area type="monotone" dataKey="F1AtK" stroke="#f59e0b" fill="#f59e0b" fillOpacity={0.2} />
                    <Area type="monotone" dataKey="NDCGAtK" stroke="#8b5cf6" fill="#8b5cf6" fillOpacity={0.2} />
                    <Area type="monotone" dataKey="CTRAtK" stroke="#ef4444" fill="#ef4444" fillOpacity={0.2} />
                  </AreaChart>
                </ResponsiveContainer>
              </div>
            </div>
      </div>

        )}
       
        {comparaisonData.length > 0 && (
          <div className="mt-6 bg-white shadow-lg rounded-lg p-6 border">
            <div className="comparison-layout">
              <div className="table-section bg-white shadow-lg rounded-lg p-6 border mt-6">
                <h3 className="text-lg font-medium mb-4">Comparaison des algorithmes (Tableau)</h3>
                <table className="min-w-full table-auto border-collapse border border-gray-300">
                  <thead>
                    <tr className="bg-gray-100">
                      <th className="px-4 py-2 border border-gray-300 text-left">Métrique</th>
                      {Object.keys(comparaisonData[0])
                        .filter((key) => key !== 'metric')
                        .map((alg) => (
                          <th key={alg} className="px-4 py-2 border border-gray-300 text-left">{alg}</th>
                        ))}
                    </tr>
                  </thead>
                  <tbody>
                    {comparaisonData.map((row) => (
                      <tr key={row.metric} className="border-t hover:bg-gray-50">
                        <td className="px-4 py-2 border border-gray-300 font-semibold">{row.metric}</td>
                        {Object.keys(row)
                          .filter((key) => key !== 'metric')
                          .map((alg) => (
                            <td key={alg} className="px-4 py-2 border border-gray-300">{row[alg]?.toFixed(4)}</td>
                          ))}
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              <div className="algorithms-chart mt-4">
                <h3 className="text-lg font-medium mb-2">Comparaison des algorithmes</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart data={comparaisonData}>
                    <CartesianGrid stroke="#ccc" />
                    <XAxis dataKey="metric" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    {Object.keys(comparaisonData[0])
                      .filter((key) => key !== 'metric')
                      .map((alg, index) => (
                        <Bar
                          key={alg}
                          dataKey={alg}
                          fill={["#3b82f6", "#10b981", "#f59e0b", "#ef4444", "#8b5cf6"][index % 5]}
                        />
                      ))}
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Historique;
