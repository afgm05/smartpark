# SmartPark

SmartPark is a RESTful parking management system built with **Java 17** and **Spring Boot**.  
It enables you to manage parking lots, register vehicles, and track vehicle check-ins and check-outs.  

For ease of development and testing, the application uses an **in-memory H2 database**, requiring no additional database setup.


---

## Table of Contents
1. [Requirements](#requirements)
2. [Build](#build)
3. [Run](#run)
4. [API Endpoints](#api-endpoints)
5. [Testing](#testing)
6. [Additional Notes](#additional-notes)

---

## Requirements

- Java 17+
- Maven 3.8+
- Postman or curl to test APIs

---

## Build

1. Clone the repository
    ```bash
    git clone https://github.com/afgm05/smartpark.git
    ```

2. Navigate to the project directory
    ```bash
    cd smartpark
    ```

3. Build the project using Maven
    ```bash
    mvn clean package
    ```

4. This will compile the project and create a runnable JAR file in the target/ directory, for example:
    ```bash
    target/smartpark-0.0.1-SNAPSHOT.jar
    ```

---

## Run

After building the project, you can run the application as follows:

1. Navigate to the target directory (if not already there)
    ```bash
    cd target
    ```

2. Run the JAR file using Java
    ```bash
    java -jar smartpark-0.0.1-SNAPSHOT.jar
    ```

    By default, the application will start on port 8080. 

    **Note:** If port 8080 is already in use, you can run the JAR on a different port:
    ```bash
    java -jar smartpark-0.0.1-SNAPSHOT.jar --server.port=9090
    ```
   
---

## API Endpoints

The SmartPark application exposes the following RESTful endpoints:

### Parking Lot Management

1. **Register a parking lot**  
**POST** `/api/parking`
  
    Request body example:

    ```json
    {
        "lotId": "LOT1",
        "location": "Downtown Parking Lot",
        "capacity": 50,
        "occupiedSpaces": 0
    }
    ```
    Response example (HTTP 201 Created):
    
    ```json
    {
        "lotId": "LOT1",
        "location": "Downtown Parking Lot",
        "capacity": 50,
        "occupiedSpaces": 0
    }
    ```
     

2. **Get parking lot status**  
**GET** /api/parking/{lotId}/status

   Response example (HTTP 200 OK):
    ```json
    {
        "lotId": "LOT1",
        "location": "Downtown Parking Lot",
        "capacity": 50,
        "occupiedSpaces": 4,
        "availableSpaces": 46
    }
    ```

3. **List vehicles in a lot**  
**GET** /api/parking/{lotId}/vehicles  

    Response example (HTTP 200 OK):  
    ```json
    [ 
      {
        "licensePlate": "ABC-123",
        "type": "CAR",
        "ownerName": "John Doe",
        "checkInTime": "2025-11-08 09:28:44 PM Sat"
      },
      {
        "licensePlate": "XYZ-456",
        "type": "TRUCK",
        "ownerName": "Robert Brown",
        "checkInTime": "2025-11-08 09:29:31 PM Sat"
      }
    ]
    ```

### Vehicle Management

1. **Register a vehicle**  
**POST** `/api/vehicles`

    Request body example:
    ```json
    {
        "licensePlate": "ABC-123",
        "type": "CAR",
        "ownerName": "John Doe"
    }
   ```
   Response example (HTTP 201 Created):
    ```json
   {
        "licensePlate": "ABC-123",
        "type": "CAR",
        "ownerName": "John Doe"
    }
    ````
### Parking Records

1. **Check-in a vehicle**  
**POST** /api/parkingrecords/checkin  

    Request body example:
    ```json
    {
        "licensePlate": "ABC-123",
        "lotId": "LOT1"
    }
    ```
   Response example (HTTP 201 Created):
    ```json
    {
        "licensePlate": "ABC-123",
        "lotId": "LOT1",
        "checkInTime": "2025-11-08 09:22:46 PM Sat",
        "checkOutTime": null
    }
    ```

2. **Check-out a vehicle**  
**POST** /api/parkingrecords/checkout  

    Request body example:
    ```json
    {
        "licensePlate": "ABC-123",
        "lotId": "LOT1"
    }
    ```  
   Response example (HTTP 200 OK):
    ```json
    {
        "licensePlate": "ABC-123",
        "lotId": "LOT1",
        "checkInTime": "2025-11-08 09:22:46 PM Sat",
        "checkOutTime": "2025-11-08 09:25:31 PM Sat"
    }
    ```
   
---

## Testing

The project includes unit tests for controllers and services.  
You can run the tests using Maven from the project root directory:

```bash
cd smartpark        # navigate to the project root if not already there
mvn test
```

---

## Additional Notes

> **Postman Collection Location**
>
> ```
> postman/SmartPark.postman_collection.json
> ```

----