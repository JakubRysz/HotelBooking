locals {
  ecr_repository_name = "ecr_repository_1"
}

resource "aws_ecr_repository" "ecr_1" {
  name = local.ecr_repository_name
}

resource "aws_ecr_lifecycle_policy" "ecr_lifecycle_policy_1" {
  repository = aws_ecr_repository.ecr_1.name

  policy = <<EOF
{
  "rules": [
    {
      "rulePriority": 1,
      "description": "Keep the latest image if there's only one image",
      "selection": {
        "tagStatus": "any",
        "countType": "imageCountLessThan",
        "countNumber": 2,
        "countUnit": "images"
      },
      "action": {
        "type": "expire"
      }
    },
    {
      "rulePriority": 2,
      "description": "Delete images older than one week",
      "selection": {
        "tagStatus": "any",
        "countType": "sinceImagePushed",
        "countNumber": 7,
        "countUnit": "days"
      },
      "action": {
        "type": "expire"
      }
    }
  ]
}
EOF
}