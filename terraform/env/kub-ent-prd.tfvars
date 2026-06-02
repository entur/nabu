gcp_resources_project = "ent-nabu-prd"
service_account = "application@ent-nabu-prd.iam.gserviceaccount.com"
db_availability="REGIONAL"
labels = {
  manager     = "terraform"
  team        = "ror"
  slack       = "talk-ror"
  app         = "nabu"
  environment = "prd"
}
crud_event_publishers = [
  "serviceAccount:application@ent-irkalla-prd.iam.gserviceaccount.com"
]

