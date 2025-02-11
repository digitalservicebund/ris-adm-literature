FROM ubuntu:24.04

# install node
RUN apt-get update
RUN apt-get -y upgrade
RUN apt-get -y install curl

# Download and install nvm:
RUN curl -fsSL https://deb.nodesource.com/setup_23.x -o nodesource_setup.sh

RUN bash nodesource_setup.sh

RUN apt-get install -y nodejs

# Verify the Node.js version:
# Should print "v23.7.0".
RUN node -v
RUN npm -v

# make the 'app' folder the current working directory
WORKDIR /frontend

# copy both 'package.json' and 'package-lock.json' (if available)
COPY /frontend/package*.json ./

# install project dependencies
RUN npm install

# copy project files and folders to the current working directory (i.e. 'app' folder)
COPY /frontend/. .

# build app for production with minification
RUN npm run build

EXPOSE 5173
CMD [ "npm", "run", "dev", "--", "--host" ]