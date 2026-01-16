FROM node:25.3.0 AS builder

# make the 'app' folder the current working directory
WORKDIR /frontend

# Install bun
RUN npm install -g bun

# copy both 'package.json' and 'bun.lock'
COPY frontend/package.json frontend/bun.lock ./

# install project dependencies, ignore scripts (because of NPM attacks)
RUN bun install --frozen-lockfile --ignore-scripts

# copy project files and folders to the current working directory (i.e. 'app' folder)
COPY /frontend/. .

# build production app
RUN --mount=type=secret,id=SENTRY_AUTH_TOKEN\
    SENTRY_AUTH_TOKEN=$(cat /run/secrets/SENTRY_AUTH_TOKEN) \
    bun run build

FROM cgr.dev/chainguard/nginx:latest@sha256:c8d973ef4cad186b7285271f97d9699ed235228078f801f13fb2ad2b9cd86afa
EXPOSE 8081
COPY --from=builder /frontend/dist /var/lib/nginx/html
COPY nginx.conf /etc/nginx/conf.d/ris-adm-vwv.conf
