variable "aws_region" {
  description = "AWS region to deploy resources"
  type        = string
  default     = "us-east-1"
}

variable "app_ami" {
  description = "AMI ID for the application server"
  type        = string
}

variable "app_instance_type" {
  description = "EC2 instance type for the application server"
  type        = string
  default     = "t3.micro"
}
