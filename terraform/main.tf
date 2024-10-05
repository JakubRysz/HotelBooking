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

provider "aws" {
  region = var.region
}

data "aws_availability_zones" "available_zones" {
  state = "available"
}

resource "aws_s3_bucket" "terraform_state" {
  bucket        = "tf-state-bucket-hotel-booking"
  force_destroy = true
}

resource "aws_s3_bucket_versioning" "terraform_bucket_versioning" {
  bucket = aws_s3_bucket.terraform_state.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "terraform_state_crypto_conf" {
  bucket = aws_s3_bucket.terraform_state.bucket
  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_dynamodb_table" "terraform_locks" {
  name         = "terraform-state-locking"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "LockID"
  attribute {
    name = "LockID"
    type = "S"
  }
}

resource "aws_vpc" "vpc_1" {
  cidr_block = "10.0.0.0/16"
  tags = {
    Name = "vpc_1"
  }
}

resource "aws_internet_gateway" "internet_gw" {
  vpc_id = aws_vpc.vpc_1.id
  tags = {
    Name = "internet_gateway_1"
  }
}

resource "aws_subnet" "subnet_public" {
  vpc_id     = aws_vpc.vpc_1.id
  cidr_block = var.public_subnet_cidr

  tags = {
    Name = "subnet_public"
  }
}

resource "aws_subnet" "subnet_private" {
  // RDS requires 2 subnets for a database
  count             = var.private_subnets_count
  vpc_id            = aws_vpc.vpc_1.id
  cidr_block        = var.private_subnet_cidr_blocks[count.index]
  availability_zone = data.aws_availability_zones.available_zones.names[count.index]

  tags = {
    Name = "subnet_private_${count.index}"
  }
}

resource "aws_route_table" "route_table_public" {
  vpc_id = aws_vpc.vpc_1.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.internet_gw.id
  }

  tags = {
    Name = "route_table_public"
  }
}

resource "aws_route_table_association" "route_table_association_public" {
  subnet_id      = aws_subnet.subnet_public.id
  route_table_id = aws_route_table.route_table_public.id
}

resource "aws_route_table" "route_table_private" {
  vpc_id = aws_vpc.vpc_1.id

  //no route as this is private rout table

  tags = {
    Name = "route_table_private"
  }
}

resource "aws_route_table_association" "route_table_association_private" {
  count          = var.private_subnets_count
  subnet_id      = aws_subnet.subnet_private[count.index].id
  route_table_id = aws_route_table.route_table_private.id
}

resource "aws_security_group" "security_group_web" {
  name        = "security_group_web"
  description = "Allow Web inbound traffic"
  vpc_id      = aws_vpc.vpc_1.id

  ingress {
    description = "HTTPS"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["${var.my_public_ip_address}/32"]
  }
  ingress {
    description = "HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["${var.my_public_ip_address}/32"]
  }
  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow all outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "security_group_web"
  }
}

resource "aws_security_group" "security_group_rds" {
  name        = "security_group_rds"
  description = "No inbound or outbound traffic for outside traffic, as only EC2 instance should be able to connect to db"
  vpc_id      = aws_vpc.vpc_1.id

  ingress {
    description     = "Allow PostgreSQL traffic only for security_group_web "
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.security_group_web.id]
  }

  tags = {
    Name = "security_group_rds"
  }
}

resource "aws_db_subnet_group" "db_subnet_group" {
  name        = "db_subnet_group"
  description = "subnet group for RDS database"
  // RDS requires two or more subnets
  subnet_ids = [for subnet in aws_subnet.subnet_private : subnet.id]
}

resource "aws_ecr_repository" "ecr_1" {
  name = var.ecr_repository_name
}

resource "aws_iam_role" "ec2_role" {
  name = "ec2_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_role_policy" "ec2_policy" {
  name = "ec2_policy"
  role = aws_iam_role.ec2_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "ecr:GetAuthorizationToken",
          "ecr:BatchCheckLayerAvailability",
          "ecr:GetDownloadUrlForLayer",
          "ecr:DescribeRepositories",
          "ecr:ListImages",
          "ecr:DescribeImages",
          "ecr:BatchGetImage"
        ],
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_instance_profile" "ec2_instance_profile" {
  name = "ec2_instance_profile"
  role = aws_iam_role.ec2_role.name
}

resource "aws_db_instance" "postgres-db-instance" {
  allocated_storage      = var.settings.database.allocated_storage
  engine                 = var.settings.database.engine
  engine_version         = var.settings.database.engine_version
  identifier             = var.settings.database.identifier
  db_name                = var.settings.database.db_name
  instance_class         = var.settings.database.instance_class
  username               = var.db_user
  password               = var.db_password
  skip_final_snapshot    = var.settings.database.skip_final_snapshot
  db_subnet_group_name   = aws_db_subnet_group.db_subnet_group.id
  vpc_security_group_ids = [aws_security_group.security_group_rds.id]
}

resource "aws_key_pair" "key_pair_ec2" {
  key_name   = "key_pair_ec2"
  public_key = file("key_pair_ec2.pub")
}

resource "aws_instance" "web-server-instance" {
  ami                    = var.ec2_ami # Replace with your desired AMI
  instance_type          = var.ec2_instance_type
  subnet_id              = aws_subnet.subnet_public.id
  vpc_security_group_ids = [aws_security_group.security_group_web.id]
  key_name               = aws_key_pair.key_pair_ec2.key_name
  iam_instance_profile   = aws_iam_instance_profile.ec2_instance_profile.id
  user_data              = <<-EOF
              #!/bin/bash
              sudo apt update
              sudo apt install -y docker.io

              sudo systemctl start docker
              sudo systemctl enable docker

              sudo apt-get install unzip
              curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
              unzip awscliv2.zip
              sudo ./aws/install

              aws ecr get-login-password --region eu-central-1 | docker login --username AWS --password-stdin 586794440391.dkr.ecr.eu-central-1.amazonaws.com

              sudo docker pull 586794440391.dkr.ecr.eu-central-1.amazonaws.com/ecr_repository_1:latest
              sudo docker run -d -p 80:8080 586794440391.dkr.ecr.eu-central-1.amazonaws.com/ecr_repository_1:latest
              EOF

  tags = {
    Name = "my_instance_1"
  }
}

resource "aws_eip" "one" {
  domain   = "vpc"
  instance = aws_instance.web-server-instance.id
}