# Secure Docs — Angular Frontend

Angular 19 SPA designed for **Arabic and English** users,
featuring dynamic theming and centralized HTTP handling.

---

## Implementation Details

### 1. RTL/LTR Support

Language selection dynamically switches layout direction (`dir="rtl"/"ltr"`)
and updates Angular locales for correct date and number formatting.
Designed to match the backend's bilingual API responses end-to-end.

### 2. HTTP Interceptors

Cross-cutting concerns are handled at the interceptor level:

- **Auth Interceptor:** Attaches the Keycloak JWT token to every outgoing API request.
- **Error Interceptor:** Catches HTTP errors globally and displays them via a centralized **Toast Message Service**, Developers don't need to write try/catch blocks in every component.

### 3. Dynamic Theming

Supports runtime switching between **Dark** and **Light** modes via PrimeNG,
without page reloads.

### 4. Container Design

The Docker image uses a **multi-stage build**:

1. **Node stage** — compiles Angular source to static artifacts.
2. **Nginx stage** — serves artifacts with SPA routing (`try_files`) and Gzip compression.

---

## Tech Stack

Angular 19 · TypeScript · PrimeNG · Nginx · Docker

---

## Deployment

Managed via [secure-docs-deploy](https://github.com/Salamndir/secure-docs-deploy)