locals {
  dynamodb_terraform-state-locking_table_name = "terraform-state-locking"
  s3_terraform_state_bucket_name = "tf-state-bucket-hotel-booking"
}

provider "aws" {
  region = "eu-central-1"
}

resource "aws_s3_bucket" "s3_terraform_state_bucket" {
  bucket        = local.s3_terraform_state_bucket_name
  force_destroy = true
}

resource "aws_s3_bucket_versioning" "terraform_bucket_versioning" {
  bucket = aws_s3_bucket.s3_terraform_state_bucket.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_public_access_block" "terraform_state" {
  bucket                  = aws_s3_bucket.s3_terraform_state_bucket.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "terraform_state_crypto_conf" {
  bucket = aws_s3_bucket.s3_terraform_state_bucket.bucket
  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_dynamodb_table" "dynamodb_terraform-state-locking" {
  name         = local.dynamodb_terraform-state-locking_table_name
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "LockID"
  attribute {
    name = "LockID"
    type = "S"
  }
}