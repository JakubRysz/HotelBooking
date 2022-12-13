# HOTEL ROOM BOOKING SYSTEM

REST  API  application  designed  for  booking  a  hotel  room. The  application  has  modeled  entities  such  as locations, hotels, rooms, users  and  bookings,  along  with  the  relevant  relationships between  them.

In the application we are able to:
- save localization of hotel
- save hotel with connection to specific localization
- add rooms with connection to specific hotel
- add users wtih will be able to booking room
- add booking for room by specific user for given period of time


## Used technologies

- Spring Boot
- Hibernate
- H2 database
- Gradle
- Swagger 2

## Example of use
### 1. Manage with localizations
1.1 Add localization - name of city and name of country must have miminum two characters, , otherwise error 400 "Bad request" will be return, make sure that localization is unique, otherwise error 409 "Conflict" will be return
```bash
Method: POST   URL: http://localhost:8080/v1/localizations
Sample body:
{
    "city": "Cracow",
    "country": "Poland"
}
```
1.2 Get localizations without hotels - type page number and sort method (ASC / DSC), one page has five localizations
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
1.3 Get localizations with assigned hotels
```bash
Method: GET   URL: http://localhost:8080/v1/localizations/Hotels?page=0&sort=ASC
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
1.4 Get one localization with hotels by Id - type specyfic Id in URL
```bash
Method: GET   URL: http://localhost:8080/v1/localizations/Hotels/{id}
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
1.5 Edit localization
```bash
Method: PUT   URL: http://localhost:8080/v1/localizations
Sample body:
    {
        "id": 12,
        "city": "Cracow edit",
        "country": "Poland"
    }
```

1.6 Delete localization - type specyfic id in URL
```bash
Method: DELETE   URL: http://localhost:8080/v1/localizations/{id}
```

### 2. Manage with hotels
2.1 Add hotel - name of hotel and hotel chain must have miminum two characters and number of stars must be betwen 1 and 5, otherwise error 400 "Bad request" will be return, make sure that localization with specyfic Id exist, otherwise error 404 "Not found" will be return, make sure that hotel is unique, otherwise error 409 "Conflict" will be return
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
2.2 Get hotels without rooms
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
2.3 Get hotels with assigned rooms
```bash
Method: GET   URL: http://localhost:8080/v1/hotels/Rooms?page=0&sort=ASC
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
2.4 Get one hotel with rooms by Id
```bash
Method: GET   URL: http://localhost:8080/v1/hotels/Rooms/{id}
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
2.5 Edit hotel
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

2.6 Delete hotel - type specyfic id in URL
```bash
Method: DELETE   URL: http://localhost:8080/v1/hotels/{id}
```
### 3. Manage with rooms

operations analogous to those for locations and hotels

3.1 Add room
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
3.2 Get rooms without bookings
```bash
Method: GET   URL: http://localhost:8080/v1/rooms?page=0&sort=ASC
```
3.3 Get rooms with assigned bookings
```bash
Method: GET   URL: http://localhost:8080/v1/rooms/Bookings?page=0&sort=ASC
```
3.4 Get one room with bookings by Id
```bash
Method: GET   URL: http://localhost:8080/v1/rooms/Bookings/{id}
```
3.5 Edit room
```bash
Method: PUT   URL: http://localhost:8080/v1/rooms
```
3.6 Delete room - type specyfic id in URL
```bash
Method: DELETE   URL: http://localhost:8080/v1/rooms/{id}
```
### 4. Manage with users

operations analogous to those before

4.1 Add user
```bash
Method: POST   URL: http://localhost:8080/v1/users
Sample body:
    {
        "firstName": "Cris",
        "lastName": "Brown",
        "dateOfBirth": "1984-02-15"
    }
```
4.2 Get users without bookings
```bash
Method: GET   URL: http://localhost:8080/v1/users?page=0&sort=ASC
```
4.3 Get users with assigned bookings
```bash
Method: GET   URL: http://localhost:8080/v1/users/Bookings?page=0&sort=ASC
```
4.4 Get one user with bookings by Id
```bash
Method: GET   URL: http://localhost:8080/v1/users/Bookings/{id}
```
4.5 Edit user
```bash
Method: PUT   URL: http://localhost:8080/v1/users
```
4.6 Delete user - type specyfic id in URL
```bash
Method: DELETE   URL: http://localhost:8080/v1/users/{id}
```
### 5. Manage with bookings

operations analogous to those before

5.1 Add booking - make sure that user and room with specific Id exist, otherwise error 404 "Not found" will be return, start date must be before end date and equal or after current date, otherwise error 400 "Bad request" will be return, room must be free in given period of time, otherwise error 409 "Conflict" will be return
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
5.2 Get bookings
```bash
Method: GET   URL: http://localhost:8080/v1/bookings?page=0&sort=ASC
```
5.3 Get one booking by ID
```bash
Method: GET   URL: http://localhost:8080/v1/bookings/{id}
```
5.4 Edit booking
```bash
Method: PUT   URL: http://localhost:8080/v1/bookings
```
5.5 Delete booking - type specyfic id in URL
```bash
Method: DELETE   URL: http://localhost:8080/v1/bookings/{id}
```
