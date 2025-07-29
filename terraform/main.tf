# Contains main description of bulk of terraform?
terraform {
  required_version = ">= 0.13.2"
}

provider "google" {
  version = "~> 4.84.0"
}
provider "kubernetes" {
  version = ">= 2.13.1"
}

resource "google_pubsub_topic" "CrudEventQueue" {
  name    = "CrudEventQueue"
  project = var.gcp_resources_project
  labels  = var.labels
}

resource "google_pubsub_topic" "JobEventQueue" {
  name    = "JobEventQueue"
  project = var.gcp_resources_project
  labels  = var.labels
}

# add service account as member to pubsub service in the resources project

resource "google_pubsub_subscription_iam_member" "CrudEventQueueSubscriber" {
  project      = var.gcp_resources_project
  subscription = google_pubsub_subscription.CrudEventQueue.name
  role         = var.service_account_pubsub_role
  member       = "serviceAccount:${var.service_account}"
}

resource "google_pubsub_subscription_iam_member" "JobEventQueueSubscriber" {
  project      = var.gcp_resources_project
  subscription = google_pubsub_subscription.JobEventQueue.name
  role         = var.service_account_pubsub_role
  member       = "serviceAccount:${var.service_account}"
}



resource "google_pubsub_subscription" "CrudEventQueue" {
  name    = "CrudEventQueue"
  topic   = google_pubsub_topic.CrudEventQueue.name
  project = var.gcp_resources_project
  labels  = var.labels
  retry_policy {
    minimum_backoff = "10s"
  }
}

resource "google_pubsub_subscription" "JobEventQueue" {
  name    = "JobEventQueue"
  topic   = google_pubsub_topic.JobEventQueue.name
  project = var.gcp_resources_project
  labels  = var.labels
  retry_policy {
    minimum_backoff = "10s"
  }
}

# Database resources configuration

resource "google_sql_database_instance" "db_instance" {
  name             = "nabu-db-pg13"
  database_version = "POSTGRES_13"
  project          = var.gcp_resources_project
  region           = var.db_region

  settings {
    location_preference {
      zone = var.db_zone
    }
    tier              = var.db_tier
    user_labels       = var.labels
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
    database_flags {
      name  = "work_mem"
      value = "30000"
    }
    database_flags {
      name  = "log_min_duration_statement"
      value = "200"
    }
    insights_config {
      query_insights_enabled = true
      query_string_length    = 4500
    }
  }
}

resource "google_sql_database" "db" {
  name     = "nabu"
  project  = var.gcp_resources_project
  instance = google_sql_database_instance.db_instance.name
}

data "google_secret_manager_secret_version" "db_username" {
  secret  = "SPRING_DATASOURCE_USERNAME"
  project = var.gcp_resources_project
}

data "google_secret_manager_secret_version" "db_password" {
  secret  = "SPRING_DATASOURCE_PASSWORD"
  project = var.gcp_resources_project
}

resource "google_sql_user" "db-user" {
  project  = var.gcp_resources_project
  instance = google_sql_database_instance.db_instance.name
  name     = data.google_secret_manager_secret_version.db_username.secret_data
  password = data.google_secret_manager_secret_version.db_password.secret_data
}

