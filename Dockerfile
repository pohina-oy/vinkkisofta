FROM gradle:5.0.0-jdk8-alpine

USER root

WORKDIR /app
COPY . /app/

RUN apk update
RUN apk add nodejs
RUN apk add npm

RUN ls -la
RUN npm install

CMD ["gradle", "run"]