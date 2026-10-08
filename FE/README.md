# Winter Olympics Frontend

React, TypeScript, and Vite single-page application for the Winter Olympics backend.

## Run locally

```sh
cp .env.example .env.local
npm ci
npm run dev
```

The local API base URL is configured with `VITE_API_BASE_URL` and defaults to `http://localhost:8081`. Vite runs at `http://localhost:5173` by default. Vite environment variables are exposed to the browser; never place secrets in them.

## Commands

- `npm run dev` — local development server
- `npm run build` — TypeScript check and production Vite build
- `npm run preview` — preview the generated build locally
- `npm run lint` — Oxlint
- `npm run type-check` — TypeScript check only

For frontend routes and implemented workflows, see the [root README](../README.md). For the API contract and deployment environment, see the [project documentation](../docs/README.md).
