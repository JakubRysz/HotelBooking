output "hotel_booking_instance_ip_addr" {
  value = aws_eip.eip_hotel_booking_instance.public_ip
}

output "db_instance_addr" {
  value = aws_db_instance.postgres-db-instance.address
}