# Library Management System

This project aims to provide a seamless experience for managing a library, from user authentication to CRUD operations on loans, books and users. Done using MVC architecture.

## Technologies and Tools

<p align="center">
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/117201156-9a724800-adec-11eb-9a9d-3cd0f67da4bc.png" alt="Java" title="Java"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/117207242-07d5a700-adf4-11eb-975e-be04e62b984b.png" alt="Maven" title="Maven"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/183891303-41f257f8-6b3d-487c-aa56-c497b880d0fb.png" alt="Spring Boot" title="Spring Boot"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/192107858-fe19f043-c502-4009-8c47-476fc89718ad.png" alt="REST" title="REST"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/117533873-484d4480-afef-11eb-9fad-67c8605e3592.png" alt="JUnit" title="JUnit"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/183892181-ad32b69e-3603-418c-b8e7-99e976c2a784.png" alt="mockito" title="mockito"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/182884177-d48a8579-2cd0-447a-b9a6-ffc7cb02560e.png" alt="MongoDB" title="MongoDB"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/117207330-263ba280-adf4-11eb-9b97-0ac5b40bc3be.png" alt="Docker" title="Docker"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/186711335-a3729606-5a78-4496-9a36-06efcc74f800.png" alt="Swagger" title="Swagger"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/192108372-f71d70ac-7ae6-4c0d-8395-51d8870c2ef0.png" alt="Git" title="Git"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/192108890-200809d1-439c-4e23-90d3-b090cf9a4eea.png" alt="IntelliJ" title="IntelliJ"/></code>
</p>
<br />

- **As well as**: JWT, Jackson, Lombok, MapStruct, Spring Security, Testcontainers

## Unique Functionalities

### Authentication Controller

- **[public] ```POST /api/auth/registration```**: Allows new users to register securely.
- **[public] ```POST /api/auth/login```**: Enables existing users to authenticate securely using JWT.

### HealthCheck Controller

- **[public] `GET /api/health`**: Check the health of the app

### Book Controller

- **[customer] `GET /api/books`**: Retrieve a paginated list of all available books.
- **[customer] `GET /api/books/{id}`**: Retrieve a book by its ID.
- **[manager] `POST /api/books`**: Create a new book entry.
- **[manager] `DELETE /api/books/{id}`**: Soft delete a book by its ID.
- **[manager] `PUT /api/books/{id}`**: Update the details of a book by its ID.

### Loan Controller

- **[customer] `GET /api/loans`**: Retrieve a paginated list of all loans.
- **[customer] `GET /api/loans/{id}`**: Retrieve the details of a specific loan by its ID.
- **[customer] `POST /api/loans`**: Create a new loan for a book.
- **[customer] `PUT /api/loans/{id}/return`**: Mark a loan as returned.
- **[manager] `DELETE /api/loans/{id}`**: Soft delete a loan by its ID.
- **[manager] `PUT /api/loans/{id}`**: Update loan details by its ID.

### User Controller

- **[manager] `PUT /api/users/{id}/role`**: Update the role of a specific user by their ID.
- **[customer, manager] `GET /api/users/me`**: Retrieve the profile information of the currently authenticated user.
- **[customer, manager] `PATCH /api/users/me`**: Update the profile information of the currently authenticated user.
- **[manager] `DELETE /api/users/{id}`**: Soft delete a user by their ID.


## Test Coverage
<p align="center">
<img src="https://i.imgur.com/JkzGmMx.png" alt="Coverage"/>
</p>

## Getting Started

1. Make sure to install [IDE](https://www.jetbrains.com/idea/), [Maven](https://maven.apache.org/download.cgi), [Docker](https://www.docker.com/products/docker-desktop/), [JDK 17+](https://www.oracle.com/pl/java/technologies/downloads/)
2. Clone the repository.
3. Configure the .env file with your database credentials and ports and add it to root project directory. Working example:
```
MONGODB_LOCAL_PORT=27017
MONGODB_DOCKER_PORT=27017
MONGODB_ROOT_USERNAME=admin
MONGODB_ROOT_PASSWORD=admin
MONGODB_DATABASE=library_app

SPRING_LOCAL_PORT=8080
SPRING_DOCKER_PORT=8080
DEBUG_PORT=5005

JWT_SECRET_STRING=superLong12345AndStrong12345SecretString
```
4. Ensure Docker Desktop is running.
5. Build and run the application using Docker: `docker-compose up --build`
6. Access the API documentation at Swagger UI: `http://localhost:8080/api/swagger-ui/index.html#/` using credentials below.

You can now access the endpoints using `Swagger` or `Postman`. To access the functionality, you must first register, and you will be granted `Customer` role. To access manager endpoints, feel free to use already pre-defined credentials:
```
{
    "email": "manager@library.com",
    "password": "safePassword"
}
```
After logging in, you receive a `Bearer Token` which you must then provide as authorization to access the endpoints. The database is initialized with 10 sample books and a manager account.
