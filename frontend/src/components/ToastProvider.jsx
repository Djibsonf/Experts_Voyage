import { createContext, useCallback, useContext, useState } from 'react'
import '../styles/toast.css'

const ToastContext = createContext(null)

export function ToastProvider({ children }) {
  const [toast, setToast] = useState(null)
  const dismiss = useCallback(() => setToast(null), [])
  const showToast = useCallback((message, type = 'success') => {
    const id = Date.now() + Math.random()
    setToast({ message, type, id })
    window.setTimeout(() => setToast((current) => current?.id === id ? null : current), 4500)
  }, [])

  return <ToastContext.Provider value={showToast}>
    {children}
    {toast && <div className={`ev-toast ev-toast-${toast.type}`} role={toast.type === 'danger' ? 'alert' : 'status'}>
      <i className={`bi ${toast.type === 'danger' ? 'bi-exclamation-circle-fill' : 'bi-check-circle-fill'}`} aria-hidden="true" />
      <span>{toast.message}</span>
      <button type="button" onClick={dismiss} aria-label="Fermer la notification"><i className="bi bi-x-lg" /></button>
    </div>}
  </ToastContext.Provider>
}

export function useToast() {
  const showToast = useContext(ToastContext)
  if (!showToast) throw new Error('useToast doit être utilisé dans ToastProvider.')
  return showToast
}
