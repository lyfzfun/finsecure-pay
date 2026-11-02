$ErrorActionPreference = "Stop"

$env:DOCKERHUB_PASSWORD |
    docker login --username $env:DOCKERHUB_USER --password-stdin

if ($LASTEXITCODE -ne 0) {
    exit 1
}

$imageTag = "$($env:DOCKER_IMAGE):$($env:BUILD_NUMBER)"

docker push $imageTag

if ($LASTEXITCODE -ne 0) {
    exit 1
}

docker logout
exit 0