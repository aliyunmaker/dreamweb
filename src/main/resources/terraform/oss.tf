provider "alicloud" {
  access_key = "LTAI5t6DFvJNSEhht4ZV72pi"
  secret_key = "tEczF2eIsH8qSHH021Mi3vVggohx1o"
  region = "cn-hangzhou"
}

resource "alicloud_oss_bucket" "bucket-acl" {
  bucket = "bucket-tf-test"
  acl    = "private"
}