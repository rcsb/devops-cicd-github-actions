# devops-cicd-github-actions

Shared library of GitHub Actions Workflows to standardize CI/CD operations for RCSB repositories.

# Using this library

Within the GitHub repository for your application, you will need to create and include the `.yaml` files under the `.github/workflows` directory in order to enable the automated CI/CD pipelines to handle linting, testing, builds, and deployments.

To learn more about GitHub Actions workflows, see [this link](https://docs.github.com/en/actions/using-workflows/about-workflows).

For more in-depth documentation on the workflow `.yaml` files, see [this link](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions#about-yaml-syntax-for-workflows).

# Java Projects

## .github/workflows/push-to-branch.yaml

Add this file into your repository as `.github/workflows/push-to-branch.yaml` in order to have automated builds of all branches which are not `master`:

```yaml
name: Push2Branch

on:
  push:
    branches-ignore:
      - "master"

jobs:
  push-to-branch:
    uses: rcsb/devops-cicd-github-actions/.github/workflows/push-to-branch.yaml@master
    with:
      distribution: # The file name of the built artifact which will be uploaded to buildlocker
      type: # The type of file for the built artifact. Valid options are [ war | jar ]
      do_skaffold_deploy: # Deploy the application to Kubernetes via skaffold. Valid options are [ true | false ]
```

## .github/workflows/push-to-master.yaml

Add this file into your repository as `.github/workflows/push-to-master.yaml` in order to have automated builds on the `master` branch (and only that branch):

```yaml
name: Push2Master

on:
  push:
    branches:
      - "master"

jobs:
  push-to-master:
    uses: rcsb/devops-cicd-github-actions/.github/workflows/push-to-master.yaml@master
    with:
      distribution: # The file name of the built artifact which will be uploaded to buildlocker
      type: # The type of file for the built artifact. Valid options are [ "war" | "jar" ]
```

# Node Projects

## .github/workflows/nightly-test.yaml

Add this file into your repository as `.github/workflows/nightly-test.yaml` in order to have automated builds run periodically to perform tests:

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
    uses: rcsb/devops-cicd-github-actions/.github/workflows/cypress.yaml@master
    with:
      branch: "master" # The name of the branch the test will be ran against
      type: "nightly" # The type of test to run. Values are [ "nightly" | "push" ]
```

## .github/workflows/node-deploy.yaml

Add this file into your repository as `.github/workflows/node-deploy.yaml` in order to have automated builds run for all branches:

```yaml
name: Node Build and Deploy

on:
  push:
  workflow_dispatch:

jobs:
  cypress-test:
    uses: rcsb/devops-cicd-github-actions/.github/workflows/cypress.yaml@master
    with:
      branch: ${{ github.ref_name }} # Set this value to always use the commit branch. Leave as is.
      type: 'push' # The type of test to run. Values are [ "nightly" | "push" ]. Should just leave this as "push" in most cases.

  node-build-deploy:
    needs: cypress-test
    uses: rcsb/devops-cicd-github-actions/.github/workflows/node-deploy.yaml@master
    with:
      distribution: # The file name of the built artifact which will be uploaded to buildlocker
      branch: ${{ github.ref_name }} # Set this value to always use the commit branch. Leave as is.
```