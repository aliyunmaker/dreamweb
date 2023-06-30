provider "alicloud" {
  access_key = "your_access_key"
  secret_key = "your_secret_key"
  region     = "cn-hangzhou"
}
module "tf-instances" {
  source              = "alibaba/ecs-instance/alicloud"
  region              = "cn-hangzhou"
  number_of_instances = 3
  instance_type       = "ecs.n2.small"
  image_ids           = ["ubuntu_18_04_64_20G_alibase_20190624.vhd"]
  vswitch_id          = "your_vswitch_id"
  group_ids           = ["your_security_group_id"]

  instance_charge_type = "PostPaid"
  system_disk_category = "cloud_ssd"
  data_disks = [
    {
      category = "cloud_ssd"
      name     = "my_module_disk"
      size     = "40"
    }
  ]
  host_name                   = "ECS-test"
  password                    = "ECS@test123"
  associate_public_ip_address = true
  internet_max_bandwidth_out  = 10
  internet_charge_type        = "PayByTraffic"

}
