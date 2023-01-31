# Contains main description of bulk of terraform?
terraform {
  required_version = ">= 0.13.2"
}

provider "google" {
  version = ">= 4.26"
}
provider "kubernetes" {
  version = ">= 2.13.1"
}

resource "google_pubsub_topic" "CrudEventQueue" {
  name = "CrudEventQueue"
  project = var.gcp_resources_project
  labels = var.labels
}

# add service account as member to pubsub service in the resources project
resource "google_pubsub_topic_iam_member" "pubsub_topic_iam_member" {
  project = var.gcp_resources_project
  topic = google_pubsub_topic.CrudEventQueue.name
  role = var.service_account_pubsub_role
  member = "serviceAccount:${var.service_account}"
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

resource "kubernetes_secret" "nabu-psql-credentials" {
  metadata {
    name = "nabu-psql-credentials"
    namespace = var.kube_namespace
  }

  data = {
    "SPRING_DATASOURCE_USERNAME" = var.ror-nabu-db-username
    "SPRING_DATASOURCE_PASSWORD" = var.ror-nabu-db-password
    "SPRING_MAIL_USERNAME" = var.ror-nabu-smtp-username
    "SPRING_MAIL_PASSWORD" = var.ror-nabu-smtp-password
    "SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_NABU_CLIENT_SECRET" = var.ror-nabu-auth0-secret
  }
}


# Database resources configuration

resource "google_sql_database_instance" "db_instance" {
  name = "nabu-db-pg13"
  database_version = "POSTGRES_13"
  project = var.gcp_resources_project
  region = var.db_region

  settings {
    location_preference {
      zone = var.db_zone
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

