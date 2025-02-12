FROM node:23.7.0 AS builder

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

FROM cgr.dev/chainguard/nginx:latest@sha256:cebd3e4630ee2355ee1683d884fe7190c8ba7a1aee85e3c4d1b7a33aa8380ccf
EXPOSE 8081
COPY --from=builder /frontend/dist /var/lib/nginx/html
COPY nginx.conf /etc/nginx/conf.d/ris-adm-vwv.conf