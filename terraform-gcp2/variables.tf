#Enviroment variables
variable "gcp_resources_project" {
  description = "The GCP project hosting the project resources"
}

variable "kube_namespace" {
  description = "The Kubernetes namespace"
  default = "nabu"
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

variable "ror-nabu-auth0-secret" {
  description = "nabu Auth0 secret"
}

variable "db_region" {
  description = "GCP  region"
  default = "europe-west1"
}

variable "db_zone" {
  description = "GCP zone"
  default = "europe-west1-b"
}

variable "db_tier" {
  description = "Database instance tier"
  default = "db-custom-1-3840"
}

variable "db_availability" {
  description = "Database availablity"
  default = "ZONAL"
}


