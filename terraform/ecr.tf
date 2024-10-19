locals {
  ecr_repository_name = "ecr_repository_1"
}

resource "aws_ecr_repository" "ecr_1" {
  name = local.ecr_repository_name
}