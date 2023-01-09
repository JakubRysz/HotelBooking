# HOTEL ROOM BOOKING APPLICATION

This application is REST  API backend application  designed  for  booking  a  hotel  room. The  application  has  modeled  entities  such  as locations, hotels, rooms, users  and  bookings,  along  with  the  relevant  relationships between  them.

In the application authenticated user depends of his role are able to:
- save / edit / delete localization of hotel
- save / edit / delete hotels with connection to specific localization
- save / edit / delete rooms with connection to specific hotel
- save / edit / delete users which will be able to booking room
- save / edit / delete room bookings made by specific user for given period of time

E-mail with information is send to specific user after creating, editing or deleting user and after creating, editing or deleting booking owned by user. 

## Used technologies

- Spring Boot
- Hibernate
- Gradle
- Spring Security with Json Web Tokens
- H2 database
- Swagger 2
- JUnit 5
- Mockito


## 1. User authentication and authorization

 In application there are two roles of user: 
 - ROLE_USE
 - ROLE_ADMIN

### 1.1 User roles description

#### 1.1.1 Without authentication

Without authentication we are able to:

- initialize database - `/v1/initializeDb` with `POST` method - described in chapter 2
- register new user with default role ROLE_USER - `/v1/users/registration` with `POST` method
- log in a user that exists in the database - `/login`  with `POST` method

#### 1.1.2 Authenticated user with ROLE_USER is authorized to:

Localizations:

- get all localizations without hotels - `/v1/localizations` with `GET` method with params `page` and `sort`
- get all localizations with hotels - `/v1/localizations/Hotels` with `GET` method with params `page` and `sort`
- get single localization with hotels - `/v1/localizations/hotels/{id}` with GET method

Hotels:

- get all hotels without rooms - `/v1/hotels` with `GET` method with params `page` and `sort`
- get all hotels with room - `/v1/hotels/rooms` with `GET` method with params `page` and `sort`
- get single hotel with rooms - `/v1/hotels/rooms/{id}` with GET method 

Rooms:

- get all rooms without bookings - `/v1/rooms` with `GET` method with params `page` and `sort`
- get all rooms with bookings without information about users - `/v1/rooms/Bookings/withoutUsers` with `GET` method with params `page` and `sort`
- get single room with bookings without information about users - `/v1/rooms/bookings/withoutUsers/{id}` with GET method

Users:

- get currently authenticated user with bookings - `/v1/users/own/bookings` with `GET` method

Bookings:

- add bookings for currently authenticated user - `/v1/bookings/own` with `POST` method
- get bookings owned by currently authenticated user - `/v1/bookings/own` with `GET` method
- edit specific booking owned by currently authenticated user - `/v1/bookings/own` with `PUT` method
- delete specific booking owned by currently authenticated user - `/v1/bookings/own/{id}` with `DELETE` method

#### 1.1.3 Authenticated user with ROLE_ADMIN is authorized the do the same operations as user with role ROLE_USER and also:

Localizations:

- add localization - `/v1/localizations` with `POST` method 
- edit specific localization - `/v1/localizations`with `PUT` method
- delete specific localization - `/v1/localizations/{id}`with `DELETE` method

Hotels:

- add hotel - `/v1/hotels` with `POST` method
- edit specific hotel - `/v1/hotels`with `PUT` method
- delete specific hotel - `/v1/hotels/{id}`with `DELETE` method

Rooms:

- add room - `/v1/rooms` with `POST` method
- get all rooms with bookings with information about users `/v1/rooms/bookings` with `GET` method with params `page` and `sort`
- get specific room with bookings with information about users `/v1/rooms/bookings/{id}` with `GET` method
- edit specific room - `/v1/rooms`with `PUT` method
- delete specific room - `/v1/rooms/{id}`with `DELETE` method

Users:

- add user with specific role - `/v1/users` with `POST` method
- get all users without bookings `/v1/users` with `GET` method with params `page` and `sort`
- get all users with bookings `/v1/users/bookings` with `GET` method with params `page` and `sort`
- get specific user with bookings `/v1/users/bookings/{id}` with `GET` method
- edit specific user - `/v1/users` with `PUT` method
- delete specific user - `/v1/users/{id}` with `DELETE` method

Bookings:

- add booking - `/v1/bookings` with `POST` method
- get all bookings - `/v1/bookings` with `GET` method with params `page` and `sort`
- get specific booking - `/v1/bookings/{id}` with `GET` method
- edit specific booking - `/v1/bookings` with `PUT` method
- delete specific booking - `/v1/bookings/{id}` with `DELETE` method

Database:
- clear database - `v1/clearDb` with method `DELETE`

### 1.2 User authentication 

#### 1.2.1 Request to log user by JSON

Application provides option to log user by JSON using endpoint `/login` with `POST` method. Example of body to log user:
````bash
{
    "username":"username",
    "password":"password"
}
````
#### 1.2.2 Default users after database initialization

There are two default users after database initialization described in chapter 2. Information of default users are presented below.

````bash
    
    User with ROLE_USER:
    {
        "firstName": "user_firstname",
        "lastName": "user_lastname",
        "dateOfBirth": "2000-01-01",
        "username": "user_role_user",
        "password": "user123",
        "role": "ROLE_USER",
        "email": "application.test1010@gmail.com"
    }
    
    User with role ROLE_ADMIN:
    {
        "firstName": "admin_firstname",
        "lastName": "admin_lastname",
        "dateOfBirth": "2000-01-01",
        "username": "user_role_admin",
        "password": "admin123",
        "role": "ROLE_ADMIN",
        "email": "application.test1010@gmail.com"
    }
````

#### 1.2.3 Using JSON Web Tokens

After successful user authentication JSON Web Token is send in `Authorization` header of response. 
That token must be copied and send in header `Authorization` of any other request which require user authentication. 

Token is valid one hour after sending. The validity period of the token can be changed in `application.properties` by change the `jwt.expirationTime` property.

En examples of using JSON Wen Tokens (JWT) in application are described in chapter 4 and 5.

## 2. Manage with database

H2 memory database is used in application, which gives opportunity to run applications in different environment without changing database configuration.

### 2.1 Access to database via console

H2 database after configuration provides option to access database via web browser using URL `http://localhost:8080/h2-console`. We might tog to console 
using logging credentials like below:

<img src="src/main/resources/README pictures/H2_console_logging.PNG" width=449>

### 2.2 Initialize and clear database

#### 2.2.1 Initialize database

For test mode application provides endpoint `/v1/initializeDb`to initialize database. Initialization include making two default users 
as described in chapter 1. To use endpoint `/v1/initializeDb` user authentication is not required.

Initial value of tables from database are presented below.

Localizations:

![img.png](src/main/resources/README%20pictures/localizations_table.png)

Hotels:

![img.png](src/main/resources/README%20pictures/hotels_table.png)

Rooms:

![img.png](src/main/resources/README%20pictures/rooms_table.png)

Users:

![img.png](src/main/resources/README%20pictures/users_table.png)

Bookings:

![img.png](src/main/resources/README%20pictures/bookings_table.png)

#### 2.2.2 Clear database

For test mode application provides endpoint `v1/clearDb`to clear database. Only user wit ROLE_ADMIN is authorized to clear database.

## 3. Examples of using endpoints
### 3.1 Manage with localizations
#### 3.1.1 Add localization - name of city and name of country must have minimum two characters,otherwise error 400 "Bad request" will be return. Make sure that localization is unique, otherwise error 409 "Conflict" will be return.
```bash
Method: POST   URL: http://localhost:8080/v1/localizations 

Sample body:
{
    "city": "Cracow",
    "country": "Poland"
}
```
#### 3.1.2 Get localizations without hotels - type page number and sort method (ASC / DSC), one page has five localizations
```bash
Method: GET   URL: http://localhost:8080/v1/localizations?page=0&sort=ASC
Sample result:
[
    {
        "id": 12,
        "city": "Cracow",
        "country": "Poland"
    },
    {
        "id": 21,
        "city": "Berlin",
        "country": "Germany"
    }
]
```
#### 3.1.3 Get localizations with assigned hotels
```bash
Method: GET   URL: http://localhost:8080/v1/localizations/hotels?page=0&sort=ASC
Sample result:
[
    {
        "id": 12,
        "city": "Cracow",
        "country": "Poland",
        "hotel": [
            {
                "id": 13,
                "name": "Hotel1",
                "numberOfStars": 2,
                "hotelChain": "Chain1",
                "localizationId": 12
            },
            {
                "id": 14,
                "name": "Hotel2",
                "numberOfStars": 3,
                "hotelChain": "Chain2",
                "localizationId": 12
            }
        ]
    },
    {
        "id": 21,
        "city": "Berlin",
        "country": "Germany",
        "hotel": []
    }
]
```
#### 3.1.4 Get one localization with hotels by Id - type specific Id in URL
```bash
Method: GET   URL: http://localhost:8080/v1/localizations/hotels/{id}
Sample result:
{
    "id": 12,
    "city": "Cracow",
    "country": "Poland",
    "hotel": [
        {
            "id": 13,
            "name": "Hotel1",
            "numberOfStars": 2,
            "hotelChain": "Chain1",
            "localizationId": 12
        },
        {
            "id": 14,
            "name": "Hotel2",
            "numberOfStars": 3,
            "hotelChain": "Chain2",
            "localizationId": 12
        }
    ]
}
```
#### 3.1.5 Edit localization
```bash
Method: PUT   URL: http://localhost:8080/v1/localizations
Sample body:
    {
        "id": 12,
        "city": "Cracow edit",
        "country": "Poland"
    }
```

#### 3.1.6 Delete localization - type specyfic id in URL
```bash
Method: DELETE   URL: http://localhost:8080/v1/localizations/{id}
```

### 3.2 Manage with hotels
#### 3.2.1 Add hotel - name of hotel and hotel chain must have minimum two characters and number of stars must be between 1 and 5, otherwise error 400 "Bad request" will be return. Make sure that localization with specific Id exist, otherwise error 404 "Not found" will be return. Make sure that hotel is unique, otherwise error 409 "Conflict" will be return.
```bash
Method: POST   URL: http://localhost:8080/v1/hotels
Sample body:
{
    "name": "Hotel1",
    "numberOfStars": 2,
    "hotelChain": "Chain1",
    "localizationId": 12
}
```
#### 3.2.2 Get hotels without rooms
```bash
Method: GET   URL: http://localhost:8080/v1/hotels?page=0&sort=ASC
Sample result:
[
    {
        "id": 13,
        "name": "Hotel1",
        "numberOfStars": 2,
        "hotelChain": "Chain1",
        "localizationId": 12
    },
    {
        "id": 14,
        "name": "Hotel2",
        "numberOfStars": 3,
        "hotelChain": "Chain2",
        "localizationId": 12
    }
]
```
#### 3.2.3 Get hotels with assigned rooms
```bash
Method: GET   URL: http://localhost:8080/v1/hotels/rooms?page=0&sort=ASC
Sample result:
    [
    {
        "id": 13,
        "name": "Hotel1",
        "numberOfStars": 2,
        "hotelChain": "Chain1",
        "localizationId": 12,
        "rooms": [
            {
                "id": 15,
                "roomNumber": 2,
                "numberOfPersons": 3,
                "standard": 2,
                "hotelId": 13
            },
            {
                "id": 16,
                "roomNumber": 3,
                "numberOfPersons": 4,
                "standard": 4,
                "hotelId": 13
            }
        ]
    },
    {
        "id": 14,
        "name": "Hotel2",
        "numberOfStars": 3,
        "hotelChain": "Chain2",
        "localizationId": 12,
        "rooms": []
    }
]
```
#### 3.2.4 Get one hotel with rooms by Id
```bash
Method: GET   URL: http://localhost:8080/v1/hotels/rooms/{id}
Sample result:
{
    "id": 13,
    "name": "Hotel1",
    "numberOfStars": 2,
    "hotelChain": "Chain1",
    "localizationId": 12,
    "rooms": [
        {
            "id": 15,
            "roomNumber": 2,
            "numberOfPersons": 3,
            "standard": 2,
            "hotelId": 13
        },
        {
            "id": 16,
            "roomNumber": 3,
            "numberOfPersons": 4,
            "standard": 4,
            "hotelId": 13
        }
    ]
}
```
#### 3.2.5 Edit hotel
```bash
Method: PUT   URL: http://localhost:8080/v1/hotels
Sample body:
    {
        "id": 13,
        "name": "hotel1 edit",
        "numberOfStars": 2,
        "hotelChain": "Chain1",
        "localizationId": 12
    }
```

#### 3.2.6 Delete hotel - type specyfic id in URL
```bash
Method: DELETE   URL: http://localhost:8080/v1/hotels/{id}
```
### 3.3 Manage with rooms

Operations analogous to those for locations and hotels for example add room below.

Make sure that hotel with specific Id exist, otherwise error 404 "Not found" will be return. Room number must be greater than zero, standard must be between 1 and 5, otherwise error 400 "Bad request" will be return.
```bash
Method: POST   URL: http://localhost:8080/v1/rooms
Sample body:
{
        "roomNumber": 3,
        "numberOfPersons": 4,
        "standard": 4,
        "hotelId": 13
}

```
### 3.4 Manage with users

Operations analogous to those described before for example add user below.

User first name and last name must have minimum 2 characters, user age must be between 18 and 100 years, user role must be one of existing roles otherwise error 400 "Bad request" will be return. Make sure that username and user e-mail are unique, otherwise error 409 "Conflict" will be return.
```bash
Method: POST   URL: http://localhost:8080/v1/users
Sample body:
        {
        "firstName": "FirstName123",
        "lastName": "LastName123",
        "dateOfBirth": "1979-01-10",
        "username": "username1234",
        "password": "password123",
        "role": "ROLE_USER",
        "email": "application.test101@gmail.com"
    }
```

### 3.5 Manage with bookings

Operations analogous to those before for example add booking below.

Make sure that user and room with specific Id exist, otherwise error 404 "Not found" will be return.Booking start date must be before booking end date and equal or after current date, otherwise error 400 "Bad request" will be return. Room must be free in given period of time, otherwise error 409 "Conflict" will be return.
```bash
Method: POST   URL: http://localhost:8080/v1/bookings
Sample body:
{
        "userId": 17,
        "roomId": 15,
        "start_date": "2023-02-11",
        "end_date": "2023-02-15"
}
```

## 4. Example using Postman tool

We may test API in our application using Postman tool. At first we need to initialize database according to description
in chapter 2. When we initialize database, there are two default users according to subsection 1.2.2.

### 4.1 Log in user using Postman

#### 4.1.1 Get JWT from application

On the picture below there is an example of log in user with ROLE_ADMIN using endpoint `/login` with `POST` method in Postman tool.
Application response contains JSON Web Token in Header `Authorization`. We should copy value of that header.

<img src="src/main/resources/README pictures/server_response_JWT.PNG"/>

#### 4.1.2 Copy JWT from application response to specific request

When we already have JWT from server, we need to send it with any request which require user authentication.

On the picture below there is an example of adding `Authorization` header in request send from Postman to our application.

<img src="src/main/resources/README pictures/request_with_JWT.PNG" />

#### 4.1.3 Automatically copy JWT into the request

Using approach described in subsection 4.1.1 and 4.1.2 there is a need of copy token manually
to every request after receiving new JWT from application. We can automate the copying of the token
by doing the following steps:

a) create global variable `token` in Postman:

<img src="src/main/resources/README pictures/create_global_variable_Postman.PNG" />

b) type script in `Tests` of request for log user, to copy value from application response `Authorization` header to previously created global variable `token`

<img src="src/main/resources/README pictures/copy_JWT_to_global_variable.PNG" />

c) insert global variable `token` in header `Authorization` of any request which require authentication

<img src="src/main/resources/README pictures/request_with_JWT_variable_token.PNG"/>

## 5. Using Swagger 2 tool

Swagger 2 is an open source project used to describe and document REST APIs. In short, it is a tool 
created to dynamically and automatically create documentation for our REST API.


### 5.1 Starting using Swagger 2

Swagger 2 is handled through a web browser using URL `http://localhost:8080/swagger-ui.html`

Bellow there is an example of view with booking controller endpoints from Swagger 2:

<img src="src/main/resources/README pictures/swagger2_view.PNG"/>

### 5.2 Initialize database

To initialize database, go to the manager controller execute the `/v1/initializeDb` endpoint like on the picture bellow

<img src="src/main/resources/README pictures/swagger2_initialize_database.PNG" />

### 5.3 Log in user using Swagger 2

To log in user go to the login controller and execute the `/login` endpoint with username and password in request body like bellow

<img src="src/main/resources/README pictures/swagger2_log_in_user.PNG" />

Copy token returned in application response `Authorization` header


Past copied token in Swagger authorization window which shows after click the `Authorize` button marked on the picture bellow.
After positive authorization Swagger keeps token in memory and token will be added to any request

<img src="src/main/resources/README pictures/swagger2_authorize.png"/>