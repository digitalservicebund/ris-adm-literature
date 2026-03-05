FROM node:25.8.0-alpine AS builder

# make the 'app' folder the current working directory
WORKDIR /frontend

# Install pnpm
RUN npm install -g pnpm

# copy both 'package.json' and 'pnpm-lock.yaml'
COPY frontend/package.json frontend/pnpm-lock.yaml ./

# install project dependencies, ignore scripts (because of NPM attacks)
RUN pnpm install --frozen-lockfile --ignore-scripts

# copy project files and folders to the current working directory (i.e. 'app' folder)
COPY /frontend/. .

# build production app
RUN --mount=type=secret,id=SENTRY_AUTH_TOKEN\
    SENTRY_AUTH_TOKEN=$(cat /run/secrets/SENTRY_AUTH_TOKEN) \
    pnpm run build

FROM cgr.dev/chainguard/nginx:latest@sha256:c4dded23d757224fde61de7d7b5e91114f665a9ac966d354736f96de34bbf659
EXPOSE 8081
COPY --from=builder /frontend/dist /var/lib/nginx/html
COPY nginx.conf /etc/nginx/conf.d/ris-adm-vwv.conf
