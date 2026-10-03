// ─── File utility helpers ────────────────────────────────────────────────────

export const MAX_FILE_SIZE = 50 * 1024 * 1024; // 50 MB in bytes

export const ALLOWED_MIME_TYPES = [
  // Documents
  'application/pdf',
  'application/msword',
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
  'application/vnd.ms-excel',
  'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
  'application/vnd.ms-powerpoint',
  'application/vnd.openxmlformats-officedocument.presentationml.presentation',
  'text/plain',
  'text/csv',
  // Images
  'image/jpeg',
  'image/png',
  'image/gif',
  'image/webp',
  'image/svg+xml',
  // Archives
  'application/zip',
  'application/x-rar-compressed',
  'application/x-7z-compressed',
];

export const ALLOWED_EXTENSIONS = [
  '.pdf', '.doc', '.docx', '.xls', '.xlsx', '.ppt', '.pptx',
  '.txt', '.csv', '.jpg', '.jpeg', '.png', '.gif', '.webp',
  '.svg', '.zip', '.rar', '.7z',
];

/**
 * Returns true if the File object passes MIME type or extension checks.
 */
export function isAllowedType(file) {
  if (ALLOWED_MIME_TYPES.includes(file.type)) return true;
  const ext = '.' + file.name.split('.').pop().toLowerCase();
  return ALLOWED_EXTENSIONS.includes(ext);
}

/**
 * Format bytes to human-readable string.
 * @param {number} bytes
 * @param {number} [decimals=1]
 */
export function formatBytes(bytes, decimals = 1) {
  if (!bytes || bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(decimals)) + ' ' + sizes[i];
}

/**
 * Format a date string or Date object to a readable local string.
 */
export function formatDate(date) {
  if (!date) return '—';
  return new Date(date).toLocaleDateString(undefined, {
    year: 'numeric', month: 'short', day: 'numeric',
  });
}

/**
 * Format a date string or Date object to date + time.
 */
export function formatDateTime(date) {
  if (!date) return '—';
  return new Date(date).toLocaleString(undefined, {
    year: 'numeric', month: 'short', day: 'numeric',
    hour: '2-digit', minute: '2-digit',
  });
}
