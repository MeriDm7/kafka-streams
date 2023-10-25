provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "train" {
  name     = "bigdata-train"
  location = "West Europe"
}

resource "azurerm_storage_account" "train" {
  name                     = "bigdatakfk"
  resource_group_name      = azurerm_resource_group.train.name
  location                 = azurerm_resource_group.train.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
  is_hns_enabled           = true
}



resource "azurerm_storage_container" "train" {
  name                  = "kafka-streams"
  storage_account_name  = azurerm_storage_account.train.name
  container_access_type = "private"
}

resource "null_resource" "upload_files" {
  triggers = {
    always_run = "${timestamp()}"
  }

  provisioner "local-exec" {
    command = "az storage blob upload-batch --destination kafka-streams --type block --account-name ${azurerm_storage_account.train.name} --account-key ${azurerm_storage_account.train.primary_access_key} --source <DATA_PATH>"
  }
}