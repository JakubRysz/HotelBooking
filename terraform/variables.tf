variable "region" {
  description = "Default region for provider"
  type        = string
  default     = "eu-central-1"
}

# EC2 Variables

variable "ec2_ami" {
  description = "Amazon machine image to use for ec2 instance"
  type        = string
  default     = "ami-0e04bcbe83a83792e" # Ubuntu 20.04 LTS // us-east-1
}

variable "ec2_instance_type" {
  description = "ec2 instance type"
  type        = string
  default     = "t2.micro"
}

# S3 Variables

#variable "bucket_prefix" {
#  description = "prefix of s3 bucket for app data"
#  type        = string
#}


variable "db_user" {
  description = "Username for DB"
  type        = string
}

variable "db_password" {
  description = "Password for DB"
  type        = string
  sensitive   = true
}

variable "public_subnet_cidr" {
  description = "cidr for public subnet"
  type        = string
  default     = "10.0.1.0/24"
}

variable "private_subnet_cidr_blocks" {
  description = "cidr blocks for private subnets"
  type        = list(string)
  default = [
    "10.0.101.0/24",
    "10.0.102.0/24",
  ]
}

variable "my_public_ip_address" {
  description = "used in test scope - temporary variable to set inbound traffic available only to my ip address"
  type        = string
  default     = "193.192.177.152"
}

variable "private_subnets_count" {
  description = "number of private subnets, RDS requires two subnets"
  type        = number
  default     = 2
}

variable "aws_account_id" {
  description = "AWS account id"
  type        = string
  default     = "586794440391"
}

variable "ecr_repository_name" {
  description = "AWS ecr repository name"
  type        = string
  default     = "ecr_repository_1"
}

variable "settings" {
  description = "Config settings"
  type        = map(any)
  default = {
    "database" = {
      allocated_storage   = 10
      engine              = "postgres"
      engine_version      = "16.3"
      identifier          = "database-instance-1"
      db_name             = "database_1"
      instance_class      = "db.t3.micro"
      skip_final_snapshot = true
      storage_encrypted   = false
      publicly_accessible = true
      apply_immediately   = true
    }
  }
}


