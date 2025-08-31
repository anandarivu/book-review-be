# Terraform AWS Provisioning for Book Review Platform

This repo provisions AWS resources for a simple AI learning project using Terraform. It is designed for free tier eligibility and minimal cost.

## Resources Provisioned
- **EC2 (t2.micro)**: Hosts backend (Spring Boot API)
- **S3 Bucket**: Hosts frontend static files
- **IAM Role/Policy**: Allows EC2 to access SSM Parameter Store
- **SSM Parameter Store**: Stores Perplexity API key
- **Security Group**: Allows HTTP (port 80) access to EC2

## Not Included
- No HTTPS (access backend via EC2 public IP)
- No RDS, CloudFront, or Route53

## Prerequisites
- [Terraform](https://www.terraform.io/downloads.html) installed
- AWS account with free tier eligibility
- AWS CLI configured (`aws configure`)

## Setup Instructions
1. Clone this repo and navigate to its directory
2. Initialize Terraform:
   ```bash
   terraform init
   ```
3. Review and edit variables in `variables.tf` as needed
4. Apply the Terraform plan:
   ```bash
   terraform apply
   ```
   - Review the plan and type `yes` to confirm
5. After provisioning:
   - Backend: Deploy your Spring Boot app to the EC2 instance (use SSH with the generated key)
   - Frontend: Upload static files to the S3 bucket (see outputs)
   - API Key: Store your Perplexity API key in SSM Parameter Store

## Outputs
- EC2 public IP
- S3 bucket name
- SSM parameter name

## Cleanup
To destroy all resources:
```bash
terraform destroy
```

## Notes
- All resources are free tier eligible
- Security group only allows HTTP (port 80) inbound
- No HTTPS or custom domain
