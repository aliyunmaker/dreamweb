variable "access_key" {
  type = string
}

variable "secret_key" {
  type = string
}

variable "region" {
  type = string
  default = "cn-hangzhou"
}

variable "bucket_name" {
  type = string
}

variable "acl" {
  type    = string
  default = "private"
}

variable "tag_application_key" {
  type = string
  default = "application"
}

variable "tag_application_value" {
  type = string
  default = "application1"
}

variable "tag_environment_type_key" {
  type = string
  default = "environmentType"
}
variable "tag_environment_type_value" {
  type = string
  default = "product"
}

variable "tags" {
    type = map(string)
    default = {
        application = "application1"
        environmentType = "product"
    }
}

provider "alicloud" {
  access_key = var.access_key
  secret_key = var.secret_key
  region = var.region
}

resource "alicloud_oss_bucket" "create_new_bucket" {
  bucket = var.bucket_name
  acl    = var.acl
  tags   = var.tags
}

output "this_oss_bucket_id" {
  description = "The name of the bucket:"
  value = alicloud_oss_bucket.create_new_bucket.id
}

