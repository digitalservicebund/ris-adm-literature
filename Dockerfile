FROM node:25.3.0

# make the 'app' folder the current working directory
WORKDIR /frontend

# copy both 'package.json' and 'package-lock.json' (if available)
COPY /frontend/package*.json ./

# install project dependencies
RUN pnpm install --ignore-scripts

# copy project files and folders to the current working directory (i.e. 'app' folder)
COPY /frontend/. .

# build app for production with minification
RUN pnpm run build

EXPOSE 5173
CMD [ "pnpm", "run", "dev", "--", "--host" ]
