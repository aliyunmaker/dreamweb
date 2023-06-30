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

variable "project_name" {
  type = string
}

variable "project_description" {
  type = string
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

resource "alicloud_log_project" "create_new_project" {
  name = var.project_name
  description = var.project_description
  tags   = var.tags
}

output "this_sls_project_id" {
  description = "The name of the project:"
  value = alicloud_log_project.create_new_project.id
}