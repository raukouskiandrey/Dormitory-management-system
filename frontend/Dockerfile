# Этап сборки (Node.js)
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Этап раздачи (Nginx)
FROM nginx:alpine
# Копируем собранные файлы (проверь, папка dist или build в зависимости от твоего фреймворка)
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]