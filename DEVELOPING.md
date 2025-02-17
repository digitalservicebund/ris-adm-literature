# Development / Tech Notes

Here's what to know from a tech perspective.

## Run Frontend with Docker

```bash
docker build --tag ris-adm-vwv-frontend-local:dev .
docker run -p 5173:5173 ris-adm-vwv-frontend-local:dev
```

Visit [http://localhost:5173/](http://localhost:5173/)

## Run Frontend bare metal

See [frontend/DEVELOPING.md](./frontend/DEVELOPING.md)

## Run backend

A docker container is started automatically with a local build.

```shell
cd backend
./gradlew bootRun
```
