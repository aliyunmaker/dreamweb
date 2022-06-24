delete from provisioned_product where status = "UnderChange";
delete from myAsk where versionid is null;
delete from provisioned_product where startname = "admin";
delete from provisioned_product where id = 6;