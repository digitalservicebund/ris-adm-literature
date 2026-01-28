FROM node:25-alpine AS builder

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

FROM cgr.dev/chainguard/nginx:latest@sha256:8d3c10dc37e42a11b306af3960342d1b11f7b8568eaadd50088d93c510ead97c
EXPOSE 8081
COPY --from=builder /frontend/dist /var/lib/nginx/html
COPY nginx.conf /etc/nginx/conf.d/ris-adm-vwv.conf
