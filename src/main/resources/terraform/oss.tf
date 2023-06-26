provider "alicloud" {
  access_key = your_access_key
  secret_key = your_secret_key
  region     = "cn-hangzhou"
}

resource "alicloud_oss_bucket" "bucket-acl" {
  bucket = "bucket-tf-test"
  acl    = "private"
}