provider "alicloud" {
  access_key = "LTAI5t6DFvJNSEhht4ZV72pi"
  secret_key = "tEczF2eIsH8qSHH021Mi3vVggohx1o"
  region     = "cn-hangzhou"
}
module "tf-instances" {
  source              = "alibaba/ecs-instance/alicloud"
  region              = "cn-hangzhou"
  number_of_instances = 3
  instance_type       = "ecs.n2.small"
  image_ids           = ["ubuntu_18_04_64_20G_alibase_20190624.vhd"]
  vswitch_id          = "vsw-bp1kkipm49v2w3bclcgjq"
  group_ids           = ["sg-bp1bwb63m7wb7wstfl4i"]

  instance_charge_type = "PostPaid"
  system_disk_category = "cloud_ssd"
  data_disks = [
    {
      category = "cloud_ssd"
      name     = "my_module_disk"
      size     = "40"
    }
  ]
#   instance_name               = "my_module_instances_"
  host_name                   = "ECS-test"
  password                    = "ECS@test123"
  associate_public_ip_address = true
  internet_max_bandwidth_out  = 10
  internet_charge_type        = "PayByTraffic"
  #   private_ips                 = ["172.16.0.10", "172.16.0.11", "172.16.0.12"]

}
