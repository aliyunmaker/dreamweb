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
