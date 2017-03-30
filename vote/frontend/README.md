# Developpement

## Frontweb
```
npm start
```

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Backend

mvn clean compile exec:java

# Build

Run `npm run build` to build the project. The build artifacts will be stored in the `../webroot/` directory.
docker build -t vertx-voting-app/vote-front:dev .

# RUN
docker run --name angular-client -p 4200:4200 vertx-voting-app/vote-front:dev
