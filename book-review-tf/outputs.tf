output "ec2_public_ip" {
  description = "Public IP of the EC2 instance"
  value       = aws_instance.book_review_ec2.public_ip
}

output "s3_bucket_name" {
  description = "Name of the S3 bucket for frontend"
  value       = aws_s3_bucket.frontend_bucket.id
}

output "ssm_parameter_name" {
  description = "Name of the SSM parameter for Perplexity API key"
  value       = var.ssm_parameter_name
}
