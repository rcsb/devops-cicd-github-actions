# Helm Chart Template for A/B Path Services

This Helm chart template is used for any RCSB services which will require multiple deployments as part of the data archive A/B pathing strategy. These services will publish one path to the public for a given week while the other path is updated with the newest dataset in preparation of the next week's 0 UTC Wednesday release.

The path to be used for public distribution will be managed by the custom [RCSB path operator](https://github.com/rcsb/devops-k8s-path-operator) resource. This Helm chart will only setup the underlying resources to support k8s resources for both paths.