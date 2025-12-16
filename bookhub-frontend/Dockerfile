FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist/bookhub-frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Nginx escucha en el puerto 80 dentro del contenedor
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]


