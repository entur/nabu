#Enviroment variables
variable "gcp_resources_project" {
  description = "The GCP project hosting the project resources"
}

variable "kube_namespace" {
  description = "The Kubernetes namespace"
  default     = "nabu"
}

variable "labels" {
  description = "Labels used in all resources"
  type        = map(string)
  default = {
    manager = "terraform"
    team    = "ror"
    slack   = "talk-ror"
    app     = "nabu"
  }
}

variable "service_account_pubsub_role" {
  description = "Role of the Service Account - more about roles https://cloud.google.com/pubsub/docs/access-control"
  default     = "roles/pubsub.subscriber"
}

variable "service_account" {
  description = "Application gcp service account"
}

variable "db_region" {
  description = "GCP  region"
  default     = "europe-west1"
}

variable "db_zone" {
  description = "GCP zone"
  default     = "europe-west1-b"
}

variable "db_tier" {
  description = "Database instance tier"
  default     = "db-custom-1-3840"
}

variable "db_availability" {
  description = "Database availablity"
  default     = "ZONAL"
}


