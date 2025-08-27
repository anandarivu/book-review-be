| Requirement ID | Description | User Story | Expected Behavior/Outcome |
|---------------|-------------|------------|--------------------------|
| FR001 | User Signup | As a user, I should be able to signup using user id, email and password | The website should provide a way for users to signup using user id, email and password |
| FR002 | User Login | As a user, I should be able to login using my credentials | The website should allow users to login with user id and password |
| FR003 | Password Requirements | As a user, I should be required to set a strong password | Password must be at least 8 characters, include uppercase, lowercase, number, and special character |
| FR004 | User Roles | As an admin, I should be able to manage books; as a user, I should be able to review books | Admin can CRUD books; users can CRUD their own reviews |
| FR005 | Book CRUD | As an admin, I should be able to add, edit, and delete books | Admin can create, update, and delete books with required details |
| FR006 | Book Details | As an admin, I should be able to provide book details | BookID, title, author, description, cover image URL, genres, published year |
| FR007 | Review CRUD | As a user, I should be able to add, edit, and delete my reviews | Users can create, update, and delete their own reviews |
| FR008 | Review Details | As a user, I should be able to provide review details | Title, review text, rating (1-5), date |
| FR009 | Review Visibility | As a user, I should only see reviews when logged in | Reviews are visible only to logged-in users |
| FR010 | Book Ratings | As a user, I should see average ratings for each book | Each book displays its average rating |
| FR011 | Recommendations | As a user, I should see top rated books | Platform displays top rated books |
| FR012 | Search/Filter | As a user, I should be able to search/filter books by title or author | Books listing page supports search and filter by title or author |
| FR013 | Pagination | As a user, I should see paginated lists of books and reviews | Book and review lists are paginated |
| FR014 | Profile | As a user, I should be able to view my profile, reviews, and favorite books | Profile page shows user's reviews and favorite books; user can mark/unmark favorites |
| FR015 | Book Cover Images | As an admin, I should be able to provide book cover images via URL | Only external URLs are supported for book cover images |
| FR016 | Tech Stack | As a developer, I should use ReactJS + Material UI for frontend and Java + Spring Boot for backend | Platform is built using ReactJS + Material UI (frontend) and Java + Spring Boot (backend) with in-memory DB |
| FR017 | Open Source | As a developer, the platform should be open source | Source code is publicly available |
