resource "aws_db_subnet_group" "db_subnet_group" {
  name        = "db_subnet_group"
  description = "subnet group for RDS database"
  // RDS requires two or more subnets
  subnet_ids = [for subnet in aws_subnet.subnet_private : subnet.id]
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