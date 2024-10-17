provider "aws" {
  region = var.region
}

terraform {
  backend "s3" {
    bucket         = "tf-state-bucket-hotel-booking"
    key            = "terraform/terraform.tfstate"
    region         = "eu-central-1"
    dynamodb_table = "terraform-state-locking"
    encrypt        = true
  }
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.67.0"
    }
  }
}