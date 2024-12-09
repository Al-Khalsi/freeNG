FROM node:20.14.0 AS build

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

FROM node:20.14.0 AS production

WORKDIR /app

COPY --from=build /app ./

RUN npm install --only=production

ENV NODE_ENV=production

EXPOSE 3000

CMD ["npm", "start"]