# Book Review Platform – Task Breakdown

## 1. Project Setup
- Initialize backend (Spring Boot) and frontend (ReactJS + Material UI) repositories
- Set up in-memory database (H2/MapDB) for backend
- Configure Docker for both backend and frontend
- Prepare Terraform scripts for AWS deployment
- Set up Swagger for API documentation

## 2. Authentication & User Management
- Implement JWT authentication (30 min expiry)
- User signup (attach “user” role)
- User login/logout endpoints
- Admin creation API (protected by secret)
- Role management entity/table
- Basic user profile endpoint

## 3. Book Management
- Book CRUD endpoints (admin only)
- Book entity: BookID, title, author, description, cover image URL, genres, published year
- Validation: non-empty, min/max length for fields
- Book cover image URL validation (non-empty)
- Search/filter books by title/author (partial, case-insensitive)
- Pagination and sorting for book list

## 4. Review Management
- Review CRUD endpoints (user only, own reviews)
- Review entity: title, review text, rating (1-5), date
- Validation: non-empty, min/max length, XSS sanitization
- Average rating calculation (exclude deleted reviews)
- Pagination and sorting for review list

## 5. Ratings & Recommendations
- Calculate and display average rating per book
- Endpoint for top rated books (by average rating)

## 6. Profile & Favorites
- Endpoint to view user profile (reviews, favorites)
- Add/remove favorite books (per user, no limit)
- Endpoint to fetch all favorite books for a user

## 7. Non-Functional & DevOps
- Ensure stateless backend design
- Containerize backend and frontend with Docker
- Write Terraform scripts for AWS deployment (separate services)
- Make Swagger UI publicly accessible
- Document API endpoints and usage

## 8. Testing & Quality
- Unit and integration tests for backend services
- Frontend component and integration tests
- Security testing (JWT, XSS, role-based access)
- Performance testing (pagination, sorting, search)

## 9. Documentation
- Update design and architecture docs as needed
- Write user and developer guides
