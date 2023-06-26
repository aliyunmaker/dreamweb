# provider "alicloud" {
#   access_key = "LTAI5t6DFvJNSEhht4ZV72pi"
#   secret_key = "tEczF2eIsH8qSHH021Mi3vVggohx1o"
#   region     = "cn-hangzhou"
# }

# # Create a new ECS instance for a VPC
# resource "alicloud_security_group" "group" {
#   name        = "tf_test_foo"
#   description = "foo"
#   vpc_id      = "${alicloud_vpc.vpc.id}"
# }
# resource "alicloud_vpc" "vpc" {
#   name       = "tf_test_foo"
#   cidr_block = "172.16.0.0/12"
# }

# resource "alicloud_vswitch" "vswitch" {
#   vpc_id            = "${alicloud_vpc.vpc.id}"
#   cidr_block        = "172.16.0.0/21"
#   availability_zone = "cn-hangzhou-i"
# } 

resource "alicloud_instance" "instance" {
#   availability_zone = "cn-hangzhou-i"
  #   security_groups   = "${alicloud_security_group.group.*.id}"
  security_groups      = ["sg-bp1bwb63m7wb7wstfl4i"]
  instance_type        = "ecs.t6-c2m1.large"
  system_disk_category = "cloud_efficiency"
  image_id             = "ubuntu_18_04_64_20G_alibase_20190624.vhd"
  instance_name        = "test_foo"
  #   vswitch_id                 = "${alicloud_vswitch.vswitch.id}"
  vswitch_id           = "vsw-bp1kkipm49v2w3bclcgjq"
  internet_max_bandwidth_out = 10

}
