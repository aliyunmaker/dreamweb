provider "alicloud" {
  access_key = "LTAI5t6DFvJNSEhht4ZV72pi"
  secret_key = "tEczF2eIsH8qSHH021Mi3vVggohx1o"
  region = "cn-hangzhou"
}

resource "alicloud_log_project" "test" {
  name        = "log-tf-test"
  description = "create by terraform"
}