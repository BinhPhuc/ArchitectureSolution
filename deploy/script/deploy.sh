#!/usr/bin/env bash

set -e

# TODO: for CI/CD, store all secrets from deploy/env/.env.example in GitHub Secrets
# and have the workflow generate
# deploy/env/.env at deploy time, then set is_dev=false below.

cd "$(dirname "$0")"

is_dev=true
env_file="../env/.env"

if [[ "$is_dev" == "true" ]]; then
    env_file="../env/.env.dev"
fi

docker network inspect architecture_solution >/dev/null 2>&1 \
    || docker network create architecture_solution

docker compose -p architecture-solution-infra --env-file "$env_file" -f ../compose/compose-infra.yaml up -d --wait
APP_ENV_FILE="$env_file" docker compose -p architecture-solution-app --env-file "$env_file" -f ../compose/compose-app.yaml up -d --pull always

echo "App Is Up!"
