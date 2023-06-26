resource "alicloud_instance" "instance" {
  security_groups      = ["your_security_group_id"]
  instance_type        = "ecs.t6-c2m1.large"
  system_disk_category = "cloud_efficiency"
  image_id             = "ubuntu_18_04_64_20G_alibase_20190624.vhd"
  instance_name        = "test_foo"
  vswitch_id           = "your_vswitch_id"
  internet_max_bandwidth_out = 10

}
