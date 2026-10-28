#!/usr/bin/env bash

set -e

cd "$(dirname "$0")"

env_file="../env/.env"

docker network inspect architecture_solution >/dev/null 2>&1 \
    || docker network create architecture_solution

docker compose -p architecture-solution-infra --env-file "$env_file" -f ../compose/compose-infra.yaml up -d --wait
APP_ENV_FILE="$env_file" docker compose -p architecture-solution-app --env-file "$env_file" -f ../compose/compose-app.yaml up -d --pull always

echo "App Is Up!"
