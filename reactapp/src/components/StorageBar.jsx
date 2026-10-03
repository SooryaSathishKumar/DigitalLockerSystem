import React from 'react';
import { formatBytes } from '../utils/fileUtils';

/**
 * StorageBar — displays used / total storage with a progress bar.
 * Props: usedBytes {number}, totalBytes {number}
 */
export default function StorageBar({ usedBytes = 0, totalBytes = 524288000 }) {
  const pct = totalBytes > 0 ? Math.min(100, (usedBytes / totalBytes) * 100) : 0;
  const color = pct >= 90 ? '#ef4444' : pct >= 70 ? '#f59e0b' : '#2563eb';

  return (
    <div className="storage-bar-wrap">
      <div className="storage-bar-labels">
        <span className="storage-bar-used">{formatBytes(usedBytes)} used</span>
        <span className="storage-bar-total">{formatBytes(totalBytes)} total</span>
      </div>
      <div className="storage-bar-track">
        <div
          className="storage-bar-fill"
          style={{ width: `${pct}%`, background: color }}
          role="progressbar"
          aria-valuenow={Math.round(pct)}
          aria-valuemin={0}
          aria-valuemax={100}
        />
      </div>
      <p className="storage-bar-pct">{Math.round(pct)}% used</p>
    </div>
  );
}
