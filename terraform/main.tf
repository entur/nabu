# Contains main description of bulk of terraform?
terraform {
  required_version = ">= 0.13.2"
}

provider "google" {
  version = "~> 3.74.0"
}
provider "kubernetes" {
  load_config_file = var.load_config_file
  version = "~> 1.13.4"
}

# create service account
resource "google_service_account" "nabu_service_account" {
  account_id = "${var.labels.team}-${var.labels.app}-sa"
  display_name = "${var.labels.team}-${var.labels.app} service account"
  project = var.gcp_resources_project
}

# add service account as member to the cloudsql client
resource "google_project_iam_member" "cloudsql_iam_member" {
  project = var.gcp_resources_project
  role = var.service_account_cloudsql_role
  member = "serviceAccount:${google_service_account.nabu_service_account.email}"
}

# create key for service account
resource "google_service_account_key" "nabu_service_account_key" {
  service_account_id = google_service_account.nabu_service_account.name
}

# Add SA key to to k8s
resource "kubernetes_secret" "nabu_service_account_credentials" {
  metadata {
    name = "${var.labels.team}-${var.labels.app}-sa-key"
    namespace = var.kube_namespace
  }
  data = {
    "credentials.json" = base64decode(google_service_account_key.nabu_service_account_key.private_key)
  }
}

resource "kubernetes_secret" "ror-nabu-secret" {
  metadata {
    name = "${var.labels.team}-${var.labels.app}-secret"
    namespace = var.kube_namespace
  }

  data = {
    "nabu-db-username" = var.ror-nabu-db-username
    "nabu-db-password" = var.ror-nabu-db-password
    "nabu-smtp-username" = var.ror-nabu-smtp-username
    "nabu-smtp-password" = var.ror-nabu-smtp-password
    "nabu-auth0-secret" = var.ror-nabu-auth0-secret
  }
}

# PubSub resources configuration

resource "google_pubsub_topic" "JobEventQueue" {
  name = "JobEventQueue"
  project = var.gcp_resources_project
  labels = var.labels
}

resource "google_pubsub_subscription" "JobEventQueue" {
  name = "JobEventQueue"
  topic = google_pubsub_topic.JobEventQueue.name
  project = var.gcp_resources_project
  labels = var.labels
  retry_policy {
    minimum_backoff = "10s"
  }
}

resource "google_pubsub_subscription_iam_member" "pubsub_subscription_iam_member_subscriber_job_event_queue" {
  project = var.gcp_resources_project
  subscription = google_pubsub_subscription.JobEventQueue.name
  role = "roles/pubsub.subscriber"
  member = "serviceAccount:${google_service_account.nabu_service_account.email}"
}

resource "google_pubsub_topic" "CrudEventQueue" {
  name = "CrudEventQueue"
  project = var.gcp_resources_project
  labels = var.labels
}

resource "google_pubsub_subscription" "CrudEventQueue" {
  name = "CrudEventQueue"
  topic = google_pubsub_topic.CrudEventQueue.name
  project = var.gcp_resources_project
  labels = var.labels
  retry_policy {
    minimum_backoff = "10s"
  }
}

resource "google_pubsub_subscription_iam_member" "pubsub_subscription_iam_member_subscriber_crud_event_queue" {
  project = var.gcp_resources_project
  subscription = google_pubsub_subscription.CrudEventQueue.name
  role = "roles/pubsub.subscriber"
  member = "serviceAccount:${google_service_account.nabu_service_account.email}"
}

resource "google_sql_database_instance" "db_instance" {
  name = "nabu-db-pg13"
  database_version = "POSTGRES_13"
  project = var.gcp_resources_project
  region = var.db_region

  settings {
    location_preference {
      zone = var.db_zone_letter
    }
    tier = var.db_tier
    user_labels = var.labels
    availability_type = var.db_availability
    backup_configuration {
      enabled = true
      // 01:00 UTC
      start_time = "01:00"
    }
    maintenance_window {
      // Sunday
      day = 7
      // 02:00 UTC
      hour = 2
    }
    ip_configuration {
      require_ssl = true
    }
  }
}

resource "google_sql_database" "db" {
  name = "nabu"
  project = var.gcp_resources_project
  instance = google_sql_database_instance.db_instance.name
}

resource "google_sql_user" "db-user" {
  name = var.ror-nabu-db-username
  project = var.gcp_resources_project
  instance = google_sql_database_instance.db_instance.name
  password = var.ror-nabu-db-password
}

