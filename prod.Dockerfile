FROM node:23.9.0 AS builder

# check parameter
RUN echo ${SENTRY_AUTH_TOKEN} | tail -c 5
RUN echo $${SENTRY_AUTH_TOKEN} | tail -c 5
RUN echo $SENTRY_AUTH_TOKEN | tail -c 5

# make the 'app' folder the current working directory
WORKDIR /frontend

# copy both 'package.json' and 'package-lock.json' (if available)
COPY /frontend/package*.json ./

# install project dependencies
RUN npm ci --omit=dev

# copy project files and folders to the current working directory (i.e. 'app' folder)
COPY /frontend/. .

# build app for production with minification
RUN SENTRY_AUTH_TOKEN=$${SENTRY_AUTH_TOKEN} npm run build

FROM cgr.dev/chainguard/nginx:latest@sha256:391d7234a6648dabd2fafa3cfa2326a026e6e85e029a7963199990d4bc437819
EXPOSE 8081
COPY --from=builder /frontend/dist /var/lib/nginx/html
COPY nginx.conf /etc/nginx/conf.d/ris-adm-vwv.conf