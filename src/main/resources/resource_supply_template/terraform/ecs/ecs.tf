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

variable "number_of_instances" {
  type = number
  default = 2
}

variable "instance_type" {
  type = string
  default = "ecs.n2.small"
}

variable "image_id" {
  type = string
  default = "ubuntu_18_04_64_20G_alibase_20190624.vhd"
}

variable "vswitch_id" {
  type = string
  default = "vsw-bp1tipegxihb0brq0qy61"
}

variable "security_groups" {
  type = list(string)
  default = ["sg-bp103rdxtizwmfam0tfa"]
}

variable "instance_charge_type" {
  type = string
  default = "PostPaid"
}

variable "system_disk_category" {
  type = string
  default = "cloud_ssd"
}

variable "data_disks_category" {
  type = string
  default = "cloud_ssd"
}

variable "data_disks_size" {
  type = number
  default = 40
}

variable "data_disks_name" {
  type = string
  default = "my_module_disk"
}

variable "instance_name" {
  type = string
  default = "my_module_instances_"
}

variable "host_name" {
  type = string
  default = "ECS-test"
}

variable "password" {
  type = string
  default = "ECS@test123"
}

variable "associate_public_ip_address" {
  type = bool
  default = true
}

variable "internet_max_bandwidth_out" {
  type = number
  default = 10
}

variable "internet_charge_type" {
  type = string
  default = "PayByTraffic"
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

resource "alicloud_instance" "instance" {
  count = var.number_of_instances
  instance_type       = var.instance_type
  image_id           = var.image_id
  vswitch_id          = var.vswitch_id
  security_groups     = var.security_groups 

  instance_charge_type = var.instance_charge_type
  system_disk_category = var.system_disk_category
  data_disks {
    category = var.data_disks_category
    name     = var.data_disks_name
    size     = var.data_disks_size
  }
  instance_name               = var.instance_name
  host_name                   = var.host_name
  password                    = var.password
  internet_max_bandwidth_out  = var.internet_max_bandwidth_out
  internet_charge_type        = var.internet_charge_type
  tags = var.tags
}

output "ecs_instance_id" {
  description = "ECS instance ID"
  value = alicloud_instance.instance.*.id
}

