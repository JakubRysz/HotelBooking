locals {
  ec2_ami           = "ami-0e04bcbe83a83792e"
  ec2_instance_type = "t2.micro"
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

resource "aws_key_pair" "key_pair_ec2" {
  key_name   = "key_pair_ec2"
  public_key = file("../key_pair_ec2.pub")
}

resource "aws_instance" "web-server-instance" {
  ami                    = local.ec2_ami # Replace with your desired AMI
  instance_type          = local.ec2_instance_type
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