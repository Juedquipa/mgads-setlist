#!/bin/sh
set -e

until nc -z "$POSTGRES_HOST" "$POSTGRES_PORT"; do
    echo "Waiting for Postgres at ${POSTGRES_HOST}:${POSTGRES_PORT}..."
    sleep 1
done

python manage.py migrate --noinput
python manage.py collectstatic --noinput

exec daphne -b 0.0.0.0 -p 8000 setlist.asgi:application