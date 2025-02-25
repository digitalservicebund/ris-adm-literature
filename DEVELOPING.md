# Development / Tech Notes

The application consists of a Vue/TypeScript frontend and a Java/SpringBoot backend.

Details on each can be found in 

* [./frontend/DEVELOPING.md](./frontend/DEVELOPING.md) and
* [./backend/DEVELOPING.md](./backend/DEVELOPING.md)

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