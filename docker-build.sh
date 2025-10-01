#!/bin/bash
NAME=sk023
IMAGE_NAME="myhw-api-server"
VERSION="1.0.0"

CPU_PLATFORM=amd64

# Docker 이미지 빌드
docker build \
  --tag ${NAME}-${IMAGE_NAME}:${VERSION} \
  --file Dockerfile \
  --platform linux/${CPU_PLATFORM} \
  ${IS_CACHE} .
