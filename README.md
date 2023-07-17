# devops-cicd-github-actions

Shared library of GitHub Actions Workflows to standardize CI/CD operations for RCSB repositories.

# Using this library

Within the GitHub repository for your application, you will need to create and include the `.yaml` files under the `.github/workflows` directory in order to enable the automated CI/CD pipelines to handle linting, testing, builds, and deployments.

To learn more about GitHub Actions workflows, see [this link](https://docs.github.com/en/actions/using-workflows/about-workflows).

For more in-depth documentation on the workflow `.yaml` files, see [this link](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions#about-yaml-syntax-for-workflows).

# Java Projects

## .github/workflows/workflow-java.yaml

Add this file into your repository as `.github/workflows/workflow-java.yaml` in order to have automated builds run for all branches:

```yaml
name: Run CI/CD Workflow

on:
  push:

jobs:
  run-workflow:
    name: "Run automated workflow"
    uses: rcsb/devops-cicd-github-actions/.github/workflows/workflow-java.yaml@master
```

# Node Projects

## .github/workflows/nightly-test.yaml

Add this file into your repository as `.github/workflows/nightly-test.yaml` in order to have automated builds run periodic tests:

```yaml
name: Nightly Cypress Test

on:
  schedule:
    #  ┌───────────── minute (0 - 59)
    #  │ ┌───────────── hour (0 - 23)
    #  │ │ ┌───────────── day of the month (1 - 31)
    #  │ │ │ ┌───────────── month (1 - 12 or JAN-DEC)
    #  │ │ │ │ ┌───────────── day of the week (0 - 6 or SUN-SAT)
    #  │ │ │ │ │
    #  * * * * *
    # * is a special character in YAML so you have to quote this string
    - cron:  '0 2 * * *'
  workflow_dispatch:

jobs:
  cypress:
    uses: rcsb/devops-cicd-github-actions/.github/workflows/run_npm.yaml@master
    with:
      script: runnightly
```

## .github/workflows/workflow-node.yaml

Add this file into your repository as `.github/workflows/workflow-node.yaml` in order to have automated builds run for all branches:

```yaml
name: Run CI/CD Workflow

on:
  push:

jobs:
  run-workflow:
    name: "Run automated workflow"
    uses: rcsb/devops-cicd-github-actions/.github/workflows/workflow-node.yaml@master
```

# Docker Projects

## .github/workflows/workflow-docker.yaml

Add this file into your repository as `.github/workflows/workflow-docker.yaml` in order to have automated builds run for all branches:

```yaml
name: Run CI/CD Workflow

on:
  push:

jobs:
  run-workflow:
    name: "Run automated workflow"
    uses: rcsb/devops-cicd-github-actions/.github/workflows/workflow-docker.yaml@master
    with:
      dockerfile_location: # The location of the Dockerfile relative to the root of the repository. Defaults to "Dockerfile".
      repo_url: # The URL of the remote Docker image repository. Defaults to "harbor.devops.k8s.rcsb.org".
      repo_project: # REQUIRED. The name of the project or organization in the remote Docker image repository.
      docker_image_name: # REQUIRED. The name of the Docker image to create.
      docker_build_context: # The path location of the docker build context, relative to the project root. Defaults to the project root.
```
