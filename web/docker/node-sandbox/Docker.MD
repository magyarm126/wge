# Node sandbox

### Spin up

```
docker compose up -d
```

### Execute commands

```
docker exec -it node-sandbox bash
npm install
npx ng serve --host=0.0.0.0 --disable-host-check
```

### Stop/Remove

```
docker compose stop
docker compose down
```