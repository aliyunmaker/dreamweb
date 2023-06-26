provider "alicloud" {
  access_key = your_access_key
  secret_key = your_secret_key
  region     = "cn-hangzhou"
}

resource "alicloud_log_project" "test" {
  name        = "log-tf-test"
  description = "create by terraform"
}