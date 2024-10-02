output "instance_1_ip_addr" {
  value = aws_instance.web-server-instance.public_ip
}

output "db_instance_addr" {
  value = aws_db_instance.postgres-db-instance.address
}