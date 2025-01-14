FROM node:23.1.0 AS builder

# make the 'app' folder the current working directory
WORKDIR /frontend

# copy both 'package.json' and 'package-lock.json' (if available)
COPY /frontend/package*.json ./

# install project dependencies
RUN npm install

# copy project files and folders to the current working directory (i.e. 'app ' folder)
COPY /frontend/. .

# build app for production with minification
RUN npm run build

FROM cgr.dev/chainguard/nginx@sha256:9cbce3d5ee2bf696232931119919c2db19e7272cddb0fae0dc0602e78281b688
EXPOSE 8081
COPY --from=builder /frontend/dist /var/lib/nginx/html
COPY nginx.conf /etc/nginx/conf.d/ris-adm-vwv.conf

# TODO: remove this line
