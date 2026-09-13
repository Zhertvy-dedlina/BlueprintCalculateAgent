const TOKEN_KEY = 'auth_token';

export class AuthError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'AuthError';
  }
}

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function saveToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token);
}

export function clearToken(): void {
  localStorage.removeItem(TOKEN_KEY);
}

function unwrapToken(raw: string): string {
  return raw.replace(/^"|"$/g, '').trim();
}

export async function login(baseUrl: string, username: string, password: string): Promise<string> {
  const response = await fetch(`${baseUrl}/api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  });
  const text = await response.text().catch(() => '');
  if (!response.ok) {
    throw new Error(unwrapToken(text) || `Ошибка входа: ${response.status}`);
  }
  return unwrapToken(text);
}

export async function register(
  baseUrl: string,
  username: string,
  email: string,
  password: string
): Promise<string> {
  const response = await fetch(`${baseUrl}/api/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, email, password }),
  });
  const text = await response.text().catch(() => '');
  if (!response.ok) {
    throw new Error(unwrapToken(text) || `Ошибка регистрации: ${response.status}`);
  }
  return unwrapToken(text);
}
