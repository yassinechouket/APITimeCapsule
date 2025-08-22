import { Injectable } from "@angular/core"

@Injectable({
  providedIn: "root",
})
export class Token {
  private readonly TOKEN_KEY = "auth_token"

  get token(): string | null {
    if (typeof window !== "undefined") {
      return localStorage.getItem(this.TOKEN_KEY)
    }
    return null
  }

  set token(value: string | null) {
    if (typeof window !== "undefined") {
      if (value) {
        localStorage.setItem(this.TOKEN_KEY, value)
      } else {
        localStorage.removeItem(this.TOKEN_KEY)
      }
    }
  }

  setToken(token: string): void {
    this.token = token
  }

  getToken(): string | null {
    return this.token
  }

  removeToken(): void {
    this.token = null
  }

  isTokenValid(): boolean {
    const token = this.token
    if (!token) return false

    try {
      const payload = JSON.parse(atob(token.split(".")[1]))
      return payload.exp * 1000 > Date.now()
    } catch {
      return false
    }
  }

  getTokenPayload(): any | null {
    const token = this.token
    if (!token) return null

    try {
      return JSON.parse(atob(token.split(".")[1]))
    } catch {
      return null
    }
  }
}
