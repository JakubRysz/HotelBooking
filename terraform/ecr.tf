locals {
  ecr_repository_name = "ecr_repository_1"
}

resource "aws_ecr_repository" "ecr_1" {
  name = local.ecr_repository_name
}

resource "aws_ecr_lifecycle_policy" "ecr_lifecycle_policy" {
  repository = aws_ecr_repository.ecr_1.name

  policy = jsonencode({
    rules = [
      {
        rulePriority = 1
        description  = "Limit to 3 most recent images"
        selection = {
          tagStatus    = "any"
          countType    = "imageCountMoreThan"
          countNumber  = 3
        }
        action = {
          type = "expire"
        }
      }
    ]
  })
}