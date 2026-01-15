FROM node:25.3.0

# make the 'app' folder the current working directory
WORKDIR /frontend

# Install pnpm
RUN npm install -g pnpm

# copy both 'package.json' and 'pnpm-lock.yaml'
COPY frontend/package.json frontend/pnpm-lock.yaml ./

# install project dependencies
RUN pnpm install --frozen-lockfile --ignore-scripts

# copy project files and folders to the current working directory (i.e. 'app' folder)
COPY /frontend/. .

# build app for production with minification
RUN pnpm run build

EXPOSE 5173
CMD [ "pnpm", "run", "dev", "--", "--host" ]
