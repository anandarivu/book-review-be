variable "aws_region" {
  description = "AWS region to deploy resources"
  type        = string
  default     = "ap-south-1"
}

variable "ec2_key_name" {
  description = "Name for the EC2 SSH key pair"
  type        = string
  default     = "book-review-key"
}

variable "ssm_parameter_name" {
  description = "Name of the SSM parameter for Perplexity API key"
  type        = string
  default     = "/book-review/perplexity-api-key"
}
