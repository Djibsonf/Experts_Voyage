import api, { clearSession, saveSession } from './client'

export async function login(email, motDePasse) {
  const { data } = await api.post('/api/auth/admin/login', { email, motDePasse })
  saveSession(data)
  return data
}

export async function logout() {
  const refreshToken = localStorage.getItem('ev_refresh_token')
  try {
    if (refreshToken) await api.post('/api/auth/logout', { refreshToken })
  } finally {
    clearSession()
  }
}

export const isAuthenticated = () => Boolean(
  localStorage.getItem('ev_access_token') || localStorage.getItem('ev_refresh_token'),
)
