# Frontend

## Development server
Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Build

Run `npm run build` to build the project. The build artifacts will be stored in the `../webroot/` directory.

## DOCKER

### BUILD
docker build -t vertx-voting-app/vote-front:dev .

### RUN
docker run --name angular-client -p 4200:4200 vertx-voting-app/vote-front:dev
