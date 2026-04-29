const defaultApiBaseUrl = 'http://localhost:8080'

export const apiBaseUrl =
  import.meta.env.VITE_API_BASE_URL?.replace(/\/$/, '') ?? defaultApiBaseUrl

export type AuthRole = 'PLATFORM_ADMIN' | 'VENDOR_ADMIN' | 'VENDOR_STAFF'

export interface AuthUser {
  id: number
  email: string
  displayName: string
  role: AuthRole
  vendorId: number | null
}

interface ApiErrorBody {
  message?: string
}

export class ApiError extends Error {
  status: number

  constructor(status: number, message: string) {
    super(message)
    this.status = status
  }
}

export async function getHealth() {
  const response = await fetch(`${apiBaseUrl}/api/health`)

  if (!response.ok) {
    throw new Error(`Health check failed with status ${response.status}`)
  }

  return response.json() as Promise<{ status: string; service: string }>
}

export async function getCurrentUser() {
  const response = await fetch(`${apiBaseUrl}/api/auth/me`, {
    credentials: 'include',
  })

  if (response.status === 401) {
    return null
  }

  await assertOk(response)
  return response.json() as Promise<AuthUser>
}

export async function login(email: string, password: string) {
  return authPost<AuthUser>('/api/auth/login', { email, password })
}

export async function logout() {
  await authPost<void>('/api/auth/logout')
}

async function authPost<T>(path: string, body?: unknown) {
  const csrf = await getCsrf()
  const response = await fetch(`${apiBaseUrl}${path}`, {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      [csrf.headerName]: csrf.token,
    },
    body: body === undefined ? undefined : JSON.stringify(body),
  })

  await assertOk(response)

  if (response.status === 204 || response.headers.get('content-length') === '0') {
    return undefined as T
  }

  return response.json() as Promise<T>
}

async function getCsrf() {
  const response = await fetch(`${apiBaseUrl}/api/auth/csrf`, {
    credentials: 'include',
  })
  await assertOk(response)
  return response.json() as Promise<{ headerName: string; token: string }>
}

async function assertOk(response: Response) {
  if (response.ok) {
    return
  }

  let message = `Request failed with status ${response.status}`

  try {
    const body = (await response.json()) as ApiErrorBody
    message = body.message ?? message
  } catch {
    // Keep the generic message when the backend does not return JSON.
  }

  throw new ApiError(response.status, message)
}
