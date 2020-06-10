#Enviroment variables
variable "gcp_project" {
  description = "The GCP project hosting the workloads"
}

variable "gcp_cloudsql_project" {
  description = "The GCP project hosting the CloudSQL resources"
}

variable "gcp_pubsub_project" {
  description = "The GCP project hosting the PubSub resources"
}

variable "location" {
  description = "GCP bucket location"
}
variable "kube_namespace" {
  description = "The Kubernetes namespace"
}

variable "labels" {
  description = "Labels used in all resources"
  type = map(string)
  default = {
    manager = "terraform"
    team = "ror"
    slack = "talk-ror"
    app = "nabu"
  }
}

variable "force_destroy" {
  description = "(Optional, Default: false) When deleting a bucket, this boolean option will delete all contained objects. If you try to delete a bucket that contains objects, Terraform will fail that run"
  default = false
}

variable "prevent_destroy" {
  description = "Prevent destruction of bucket"
  type = bool
  default = false
}

variable "load_config_file" {
  description = "Do not load kube config file"
  default = false
}

variable "service_account_cloudsql_role" {
  description = "Role of the Service Account - more about roles https://cloud.google.com/pubsub/docs/access-control"
  default = "roles/cloudsql.client"
}

variable "service_account_pubsub_role" {
  description = "Role of the Service Account - more about roles https://cloud.google.com/pubsub/docs/access-control"
  default = "roles/pubsub.editor"
}

variable "ror-nabu-db-username" {
  description = "nabu database username"
}

variable "ror-nabu-db-password" {
  description = "nabu database password"
}

variable "ror-nabu-smtp-username" {
  description = "nabu smtp username"
}

variable "ror-nabu-smtp-password" {
  description = "nabu smtp password"
}

variable "ror-nabu-keycloak-secret" {
  description = "nabu keycloak secret"
}
