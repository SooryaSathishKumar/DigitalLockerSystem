import React, { useEffect, useState } from 'react';
import { getDocuments } from '../api';

function DocumentList() {
  const [docs, setDocs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;

    async function loadDocuments() {
      try {
        const res = await getDocuments();
        if (isMounted) {
          setDocs(res.data);
        }
      } catch (err) {
        console.error('Failed to load documents:', err);
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    }

    loadDocuments();

    return () => {
      isMounted = false;
    };
  }, []);

  return (
    <div>
      <div className="ledger-header">
        <h2>Document List</h2>
        <span className="ledger-count">
          {loading ? 'Loading…' : `${docs.length} document${docs.length === 1 ? '' : 's'}`}
        </span>
      </div>

      {!loading && docs.length === 0 ? (
        <div className="ledger-empty">
          Nothing uploaded yet. Documents you upload will be listed here.
        </div>
      ) : (
        <ul className="ledger">
          {docs.map((doc, i) => (
            <li key={doc.id ?? i} className="ledger-row">{doc.name}</li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default DocumentList;