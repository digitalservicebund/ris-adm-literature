FROM node:25.3.0

# make the 'app' folder the current working directory
WORKDIR /frontend

# Install bun
RUN npm install -g bun

# copy both 'package.json' and 'bun.lock'
COPY frontend/package.json frontend/bun.lock ./

# install project dependencies
RUN bun install --frozen-lockfile --ignore-scripts

# copy project files and folders to the current working directory (i.e. 'app' folder)
COPY /frontend/. .

# build app for production with minification
RUN bun run build

EXPOSE 5173
CMD [ "pnpm", "run", "dev", "--", "--host" ]
