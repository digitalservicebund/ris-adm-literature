FROM node:23.8.0 AS builder

# make the 'app' folder the current working directory
WORKDIR /frontend

# copy both 'package.json' and 'package-lock.json' (if available)
COPY /frontend/package*.json ./

# install project dependencies
RUN npm ci --omit=dev

# copy project files and folders to the current working directory (i.e. 'app' folder)
COPY /frontend/. .

# build app for production with minification
RUN npm run build

FROM cgr.dev/chainguard/nginx:latest@sha256:cf0d196bacf68158248da9156e0bc884abf31c9d66f9f81ea046a1a0d4253d65
EXPOSE 8081
COPY --from=builder /frontend/dist /var/lib/nginx/html
COPY nginx.conf /etc/nginx/conf.d/ris-adm-vwv.conf