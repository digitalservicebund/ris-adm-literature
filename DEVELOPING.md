# Development / Tech Notes

The application consists of a Vue/TypeScript frontend and a Java/SpringBoot backend.

## Git Hooks Setup 👈 (do not skip this!)

This repository uses Git hooks for

- preventing accidentally pushing secrets or other sensitive information

In order to make use of these, do install the following tools:

- [`lefthook`](https://github.com/evilmartians/lefthook) (Git hooks)
- [`trivy`](https://github.com/aquasecurity/trivy) (Secret and Vulnerability scanning)

then install the hooks via

```bash
lefthook install
```

The git hook installed always executes lefthook which reads the configuration in `lefthook.yml`. Therefore, even the file is changed, re-running the `install` command is _not_ needed.

## Run Frontend with Docker

Details on each can be found in

- [./frontend/DEVELOPING.md](./frontend/DEVELOPING.md) and
- [./backend/DEVELOPING.md](./backend/DEVELOPING.md)

## Quick Start

Start the backend (this requires a docker demon)

```bash
cd ./backend
./gradlew bootRun
```

Start the frontend in dev mode

```bash
cd ./frontend
npm run dev
```

The last step will tell you where to point your browser in order to access the app.
