resource "aws_key_pair" "deployer" {
  key_name   = var.ec2_key_name
  public_key = file("~/.ssh/id_rsa_bookreview.pub")
}

resource "aws_security_group" "ec2_sg" {
  name        = "book-review-ec2-sg"
  description = "Allow HTTP access"

  # Open port 8080 for Java application
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "book_review_ec2" {
  ami           = data.aws_ami.ubuntu.id
  instance_type = "t3.micro"
  key_name      = aws_key_pair.deployer.key_name
  vpc_security_group_ids = [aws_security_group.ec2_sg.id]

  tags = {
    Name = "book-review-backend"
  }

  iam_instance_profile = aws_iam_instance_profile.ec2_profile.name
}

data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"] # Canonical
  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-*"]
  }
}

resource "aws_s3_bucket" "frontend_bucket" {
  bucket = "book-review-frontend-anand-${random_id.s3_suffix.hex}"
}

resource "aws_s3_bucket_website_configuration" "frontend_bucket_website" {
  bucket = aws_s3_bucket.frontend_bucket.id
  index_document {
    suffix = "index.html"
  }
  error_document {
    key = "index.html"
  }
}

resource "aws_s3_bucket_policy" "frontend_bucket_policy" {
  bucket = aws_s3_bucket.frontend_bucket.id
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = "*",
        Action = ["s3:GetObject"],
        Resource = ["${aws_s3_bucket.frontend_bucket.arn}/*"]
      }
    ]
  })
}

resource "random_id" "s3_suffix" {
  byte_length = 4
}

resource "aws_ssm_parameter" "perplexity_api_key" {
  name      = var.ssm_parameter_name
  type      = "SecureString"
  value     = "REPLACE_ME"
  overwrite = true
}

resource "aws_iam_role" "ec2_ssm_role" {
  name = "book-review-ec2-ssm-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy" "ec2_ssm_policy" {
  name = "book-review-ec2-ssm-policy"
  role = aws_iam_role.ec2_ssm_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ssm:GetParameter",
          "ssm:GetParameters",
          "ssm:GetParameterHistory"
        ]
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_instance_profile" "ec2_profile" {
  name = "book-review-ec2-profile"
  role = aws_iam_role.ec2_ssm_role.name
}
