# Development / Tech Notes

Here's what to know from a tech perspective.

**⚠️ Security note: Do run the `lefthook` setup below, including the setup of `talisman`.<br>
⚠️ This ensures that secrets are caught before reaching the remote repository**

## Git Hooks Setup

This repository uses Git hooks for

- preventing accidentally pushing secrets or other sensitive information

In order to make use of these, do install the following tools:

- [`lefthook`](https://github.com/evilmartians/lefthook) (Git hooks)
- [`talisman`](https://thoughtworks.github.io/talisman/docs) (scans for secrets)

then install the hooks via

```bash
lefthook install
```

## Run Frontend with Docker

```bash
docker build --tag ris-adm-vwv-frontend-local:dev .
docker run -p 5173:5173 ris-adm-vwv-frontend-local:dev
```

Visit [http://localhost:5173/](http://localhost:5173/)

## Run Frontend bare metal

See [./frontend/DEVELOPING.md](./frontend/DEVELOPING.md)
