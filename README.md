# BE

## Local MySQL troubleshooting

If the first `docker compose up` for MySQL is interrupted, the named volume can be left half-initialized.
In that state Spring Boot may fail with:

```text
Host '172.19.0.1' is not allowed to connect to this MySQL server
```

Reset the local MySQL volume and start the DB again:

```bash
docker compose down -v
docker compose up -d db
```
