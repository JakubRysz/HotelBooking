# HOTEL ROOM BOOKING SYSTEM

This application is REST  API backend application  designed  for  booking  a  hotel  room. The  application  has  modeled  entities  such  as locations, hotels, rooms, users  and  bookings,  along  with  the  relevant  relationships between  them.

In the application authenticated user depends of his role are able to:
- save / edit / delete localization of hotel
- save / edit / delete hotels with connection to specific localization
- save / edit / delete rooms with connection to specific hotel
- save / edit / delete users with will be able to booking room
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


## 1. User roles

 User might have one of two roles: 
 - ROLE_USE
 - ROLE_ADMIN

### 1.1 User with ROLE_USER 

ROLE_USER gives user access to operations:


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

- register new user with default role ROLE_USER - `/v1/users/registration` with `POST` method
- get currently authenticated user with bookings - `/v1/users/own/bookings` with `GET` method

Bookings:

- add bookings for currently authenticated user - `/v1/bookings/own` with `POST` method
- get bookings owned by currently authenticated user - `/v1//bookings/own` with `GET` method
- edit specific booking owned by currently authenticated user - `/v1/bookings/own` with `PUT` method
- delete specific booking owned by currently authenticated user - `/v1/bookings/own/{id}` with `DELETE` method

### 1.2 User with ROLE_ADMIN 

ROLE_ADMIN gives user access to the same operations as user with role ROLE_USER and also:


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
- edit specific user - `/v1/users`with `PUT` method
- delete specific user - `/v1/users/{id}`with `DELETE` method

Bookings:

- add booking with - `/v1/bookings` with `POST` method
- get all bookings - `/v1/bookings` with `GET` method with params `page` and `sort`
- get specific booking - `/v1/bookings/{id}` with `GET` method
- edit specific booking - `/v1/bookings`with `PUT` method
- delete specific booking - `/v1/bookings/{id}`with `DELETE` method


## 2. Examples of using endpoints
### 2.1 Manage with localizations
2.1.1 Add localization - name of city and name of country must have minimum two characters,otherwise error 400 "Bad request" will be return. Make sure that localization is unique, otherwise error 409 "Conflict" will be return.
```bash
Method: POST   URL: http://localhost:8080/v1/localizations 

Sample body:
{
    "city": "Cracow",
    "country": "Poland"
}
```
2.1.2 Get localizations without hotels - type page number and sort method (ASC / DSC), one page has five localizations
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
2.1.3 Get localizations with assigned hotels
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
2.1.4 Get one localization with hotels by Id - type specific Id in URL
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
2.1.5 Edit localization
```bash
Method: PUT   URL: http://localhost:8080/v1/localizations
Sample body:
    {
        "id": 12,
        "city": "Cracow edit",
        "country": "Poland"
    }
```

2.1.6 Delete localization - type specyfic id in URL
```bash
Method: DELETE   URL: http://localhost:8080/v1/localizations/{id}
```

### 2.2 Manage with hotels
2.2.1 Add hotel - name of hotel and hotel chain must have minimum two characters and number of stars must be between 1 and 5, otherwise error 400 "Bad request" will be return. Make sure that localization with specific Id exist, otherwise error 404 "Not found" will be return. Make sure that hotel is unique, otherwise error 409 "Conflict" will be return.
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
2.2.2 Get hotels without rooms
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
2.2.3 Get hotels with assigned rooms
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
2.2.4 Get one hotel with rooms by Id
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
2.2.5 Edit hotel
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

2.2.6 Delete hotel - type specyfic id in URL
```bash
Method: DELETE   URL: http://localhost:8080/v1/hotels/{id}
```
### 2.3 Manage with rooms

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
### 2.4 Manage with users

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

### 2.5 Manage with bookings

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


