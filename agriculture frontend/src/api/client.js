// Change this if your Spring Boot app runs on a different port.
export const API_BASE_URL = 'http://localhost:8080';

function getToken() {
  return localStorage.getItem('token');
}

async function request(path, options = {}) {
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  const token = getToken();
  if (token) headers['Authorization'] = 'Bearer ' + token;

  let response;
  try {
    response = await fetch(API_BASE_URL + path, { ...options, headers });
  } catch {
    throw new Error(`Could not reach the server. Is Spring Boot running on ${API_BASE_URL}?`);
  }

  if (response.status === 204) return null;

  const text = await response.text();
  const body = text ? safeParseJson(text) : null;

  if (!response.ok) {
    if (response.status === 401) {
      localStorage.clear();
      window.location.href = '/';
    }
    let message = (body && body.message) || `Request failed (${response.status})`;
    if (body && body.fieldErrors) {
      message = Object.values(body.fieldErrors).join(' · ');
    }
    throw new Error(message);
  }

  return body;
}

function safeParseJson(text) {
  try {
    return JSON.parse(text);
  } catch {
    return null;
  }
}

export const api = {
  get: (path) => request(path, { method: 'GET' }),
  post: (path, data) => request(path, { method: 'POST', body: JSON.stringify(data) }),
  put: (path, data) => request(path, { method: 'PUT', body: JSON.stringify(data) }),
  patch: (path, data) => request(path, { method: 'PATCH', body: JSON.stringify(data) }),
  delete: (path) => request(path, { method: 'DELETE' }),
};
