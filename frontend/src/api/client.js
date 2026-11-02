import axios from 'axios'

export const apiBase = (import.meta.env.VITE_API_URL ?? (import.meta.env.DEV ? 'http://localhost:8080' : '')).replace(/\/$/, '')

const api = axios.create({ baseURL: apiBase || undefined, timeout: 10000 })
let refreshPromise = null

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('ev_access_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use((response) => response, async (error) => {
  const request = error.config
  const isAuthRequest = request?.url?.includes('/api/auth/')
  if (error.response?.status !== 401 || !request || request._retried || isAuthRequest) throw error

  const refreshToken = localStorage.getItem('ev_refresh_token')
  if (!refreshToken) {
    clearSession()
    throw error
  }

  request._retried = true
  refreshPromise ??= axios.post(`${apiBase}/api/auth/refresh`, { refreshToken }, { timeout: 10000 })
    .then(({ data }) => {
      saveSession(data)
      return data.accessToken
    })
    .catch((refreshError) => {
      clearSession()
      throw refreshError
    })
    .finally(() => { refreshPromise = null })

  try {
    const accessToken = await refreshPromise
    request.headers.Authorization = `Bearer ${accessToken}`
    return api(request)
  } catch (refreshError) {
    if (window.location.pathname.startsWith('/admin')) window.location.assign('/admin')
    throw refreshError
  }
})

export function saveSession(tokens) {
  localStorage.setItem('ev_access_token', tokens.accessToken)
  localStorage.setItem('ev_refresh_token', tokens.refreshToken)
}

export function clearSession() {
  localStorage.removeItem('ev_access_token')
  localStorage.removeItem('ev_refresh_token')
}

export default api
