import React, { useEffect, useRef, useState } from 'react';
import $ from 'jquery';
import './HistoriqueList.css'; 
import 'datatables.net-dt/js/dataTables.dataTables'; 
import 'datatables.net-dt/css/dataTables.dataTables.css';

const HistoriqueList = () => {
  const [historiques, setHistoriques] = useState([]);
  const [loading, setLoading] = useState(true);
  const tableRef = useRef();

  useEffect(() => {
    fetch('http://localhost:8049/interaction/send-Historique')
      .then(res => res.json())
      .then(data => {
        setHistoriques(data);
        setLoading(false);
      });
  }, []);

  useEffect(() => {
    if (!loading && historiques.length > 0) {
      // destroy table if already exists to avoid reinitialization error
      if ($.fn.DataTable.isDataTable(tableRef.current)) {
        $(tableRef.current).DataTable().destroy();
      }
      $(tableRef.current).DataTable();
    }
  }, [loading, historiques]);

  return (
    <div className="historique-container">
      <h1 className="historique-title">📄 Tableau des Expérimentations</h1>
      <div className="historique-table-container">
        <table ref={tableRef} className="display historique-table" style={{width: '100%'}}>
          <thead>
            <tr>
              <th>#</th>
              <th>Algorithme</th>
              <th>Expérimentation</th>
              <th>Fenêtre</th>
              <th>data</th>
              <th>Début</th>
              <th>Fin</th>
              <th>Top K</th>
              <th>Voir Résultats</th>
            </tr>
          </thead>
          <tbody>
            {historiques.map((item, index) => (
              <tr key={item._id || index}>
                <td>{index + 1}</td>
                <td>{item.algorithm}</td>
                <td>{item.expérimentation}</td>
                <td>{item.windowSize}</td>
                <td>{item.data}</td>
                <td>{item.startDate}</td>
                <td>{item.endDate}</td>
                <td>{item.top_k}</td>
                <td>
                  <button
                    className="historique-button"
                    onClick={() => window.location.href = `/historique/${item.historiqueId}`}
                  >
                    Voir résultats
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default HistoriqueList;
