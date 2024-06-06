# devops-cicd-github-actions

Shared library of GitHub Actions Workflows and files to standardize CI/CD operations for RCSB repositories.

# Using this library

Within the GitHub repository for your application, you will need to create and include the `.yaml` files under the `.github/workflows` directory in order to enable the automated CI/CD pipelines to handle linting, testing, builds, and deployments.

To package your application as a Helm chart, add the `templates/helm` directory under the root of your project as `k8s/helm`. Modify the values in the `Chart.yaml` and `values.yaml` files to fit the project needs.

To deploy your application using Skaffold, add the `templates/skaffold.yaml` file to the root of your project as `skaffold.yaml`. Modify the `releases.name` values to match the application.

To learn more about GitHub Actions workflows, see [this link](https://docs.github.com/en/actions/using-workflows/about-workflows).

For more in-depth documentation on the workflow `.yaml` files, see [this link](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions#about-yaml-syntax-for-workflows).

# Java Projects

## Jib plugin

Java applications Docker images are built using the Google Jib Maven plugin (https://github.com/GoogleContainerTools/jib). At a minimum, to leverage this plugin and have the CI/CD pipeline automate optimized builds of the Docker container image, ensure that the following plugin directive is included in the application `pom.xml` file:

```xml
<project>
  ...
  <build>
    <plugins>
      ...
      <plugin>
        <groupId>com.google.cloud.tools</groupId>
        <artifactId>jib-maven-plugin</artifactId>
        <version>3.4.1</version>
        <configuration>
            <to>
                <image>harbor.devops.k8s.rcsb.org/${jib.target.namespace}/[docker application image name]</image>
            </to>
        </configuration>
      </plugin>
      ...
    </plugins>
  </build>
  ...
</project>
```

## .github/workflows/workflow-java.yaml

Add this file into your repository as `.github/workflows/workflow-java.yaml` in order to have automated builds run for all branches:

```yaml
name: Run CI/CD Workflow

on:
  push:
    paths-ignore:
      - "k8s/**"
  workflow_dispatch:

jobs:
  run-workflow:
    name: "Run automated workflow"
    uses: rcsb/devops-cicd-github-actions/.github/workflows/workflow-java.yaml@master
    with:
      mainline_branch: # The mainline branch for the repo. Deployments to the staging and production environments are done only on push to this branch. Defaults to the repo's default branch.
      docker_namespace: # The Docker image namespace built by jib. Defaults to 'rcsb'.
      do_staging_build: # Build and push a staging tagged container image for this application on commits to the mainline_branch. Defaults to false.
      do_production_build: # Build and push a production tagged container image for this application on commits to the mainline_branch. Defaults to false.
```

Note that because some of the current RCSB Java applications are tightly coupled, production image release must be separately scheduled and deployed in tandem with other Java applications. For these applications, we should schedule a build of the Java application before the weekly release, and have the weekly update workflow handle restarting the deployments and utilize the new images.

Add this file into your repository as `.github/workflows/scheduled-production-release.yaml` in order to have scheduled builds of the mainline branch:

```yaml
name: Weekly production release

on:
  schedule:
    # Time is based on UTC timezone
    #  ┌───────────── minute (0 - 59)
    #  │ ┌───────────── hour (0 - 23)
    #  │ │ ┌───────────── day of the month (1 - 31)
    #  │ │ │ ┌───────────── month (1 - 12 or JAN-DEC)
    #  │ │ │ │ ┌───────────── day of the week (0 - 6 or SUN-SAT)
    #  │ │ │ │ │
    #  * * * * *
    # * is a special character in YAML so you have to quote this string
    - cron:  '0 18 * * TUE'
    - cron:  '0 20 * * TUE'
    - cron:  '0 23 * * TUE'
  workflow_dispatch:

jobs:
  run-workflow:
    name: "Run automated workflow for production release"
    uses: rcsb/devops-cicd-github-actions/.github/workflows/workflow-java.yaml@master
    with:
      mainline_branch: # The mainline branch for the repo. Deployments to the staging and production environments are done only on push to this branch. Defaults to the repo's default branch.
      docker_namespace: # The Docker image namespace built by jib. Defaults to 'rcsb'.
      do_staging_build: false
      do_production_build: true
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
    paths-ignore:
      - "k8s/**"
  workflow_dispatch:

jobs:
  run-workflow:
    name: "Run automated workflow"
    uses: rcsb/devops-cicd-github-actions/.github/workflows/workflow-node.yaml@master
    with:
      mainline_branch: # The mainline branch for the repo. Deployments to the staging and production environments are done only on push to this branch. Defaults to the repo's default branch.
      repo_url: # The URL of the remote Docker image repository. Defaults to harbor.devops.k8s.rcsb.org.
      repo_project: # REQUIRED. The name of the project or organization in the remote Docker image repository.
      docker_image_name: # REQUIRED. The name of the Docker image to create.
      do_staging_build: # Build, tag, and push a docker image with the staging tag on commits to the mainline branch. Defaults to true.
      restart_staging_deployment: # Restart the staging K8s deployments for this application on commits to the mainline branch. Defaults to false.
      staging_k8s_deployment_name: # The name of the deployment in the K8s staging namespace to restart. Needs to be defined if restart_staging_deployment is set to true.
      do_production_build: # Build, tag, and push a docker image with the production tag on commits to the mainline branch. Defaults to true.
      restart_production_deployment: # Restart the production K8s deployment for this application on commits to the mainline branch. Defaults to false.
      production_k8s_deployment_name: # The names of the deployment in the K8s production namespace to restart. Needs to be defined if restart_production_deployment is set to true.
      node_version: #The nodejs version of the runner to use. Defaults to 16.
```

# Docker Projects

## .github/workflows/workflow-docker.yaml

Add this file into your repository as `.github/workflows/workflow-docker.yaml` in order to have automated builds run for all branches:

```yaml
name: Run CI/CD Workflow

on:
  push:
    paths-ignore:
      - "k8s/**"
  workflow_dispatch:

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
      mainline_branch: # The mainline branch for the repo. Deployments to the staging and production environments are done only on push to this branch. Defaults to master.    
      do_staging_build: # Build, tag, and push a docker image with the staging tag.
      do_production_build: # Build, tag, and push a docker image with the production tag.
```
