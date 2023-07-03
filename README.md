# Spots-services

[![codecov](https://codecov.io/gh/0xivanov/spots-services/branch/master/graph/badge.svg?token=8ZBQDUOOW0)](https://codecov.io/gh/0xivanov/spots-services)



## Architecture Overview

The proposed architecture consists of a client-server model, where the mobile application serves as the client and communicates with a backend server to handle various functionalities. The backend server will be developed using Java, Spring, and Redis for caching. It will also require a persistence database for storing data. The frontend of the application will be built using React Native and will utilize Google Maps API or other map services for navigation.

### Backend Components

1. **Java**: Use Java as the programming language for developing the backend server.
2. **Spring**: Utilize the Spring framework to build the backend server, including features like dependency injection, MVC pattern, and RESTful API development.
3. **Redis**: Integrate Redis as a caching layer to improve performance and reduce database load.
4. **Persistence Database**: Implement a persistence database, such as MySQL or PostgreSQL, to store and retrieve user data, spot information, and other relevant data. The choice of the database depends on your specific requirements and preferences.
5. **Gradle**: Use Gradle as the build tool for managing dependencies and building the backend server.
6. **JUnit**: Employ JUnit for writing unit tests to ensure the quality and reliability of the backend code.

### Frontend Components

1. **React Native**: Develop the frontend of the mobile application using React Native, a popular framework for building cross-platform mobile apps.
2. **Map Service Integration**: Integrate a map service, such as Google Maps API, NextGIS, or Mapbox, to provide navigation functionalities and display spot locations.
3. **User Authentication**: Implement user authentication features, including login with Google or email/password options. Utilize appropriate libraries or APIs for secure authentication.
4. **Spot Generation**: Enable users to generate new spots through the mobile application. Implement necessary forms or input mechanisms for spot creation.
5. **Ad Integration**: Incorporate ad networks or lightning network functionality to generate revenue for admins. Integrate appropriate SDKs or APIs for displaying ads or handling lightning network transactions.
6. **Navigation Integration**: Enable users to navigate to a specific destination using Google Maps or other navigation services like Waze. Integrate the chosen navigation service's API to provide seamless navigation.
7. **Spot Conquering**: Implement features that allow users to mark spots as "conquered" once they visit or complete the specified activity at those locations.
8. **Social Media Sharing**: Enable users to share spot-related information or achievements on social media platforms. Integrate relevant APIs or SDKs for social media sharing functionality.
9. **Spot Details**: Provide users with additional information about each spot, including descriptions, photos, ratings, and reviews. Fetch and display this information from the backend server.
10. **Spot Proposals**: Allow users to propose new spots by submitting relevant information through the mobile application. Implement necessary forms or input mechanisms for spot proposals.

### Admin Functionality

1. **Email Address Verification**: Implement functionality for admin users to verify user email addresses, ensuring the validity of registered users.
2. **Payment Verification**: Enable admins to verify payments made by users, ensuring successful transactions and managing the revenue generated through ads or lightning network.
3. **Admin Portal**: Develop an administrative portal (web or separate mobile app) for admins to manage user accounts, spots, proposals, payments, and other relevant aspects of the application.

### Class diagram

```
+----------------------------------+
|             User                 |
+----------------------------------+
| - id: String                     |
| - email: String                  |
| - password: String               |
| - googleToken: String            |
+----------------------------------+
| + loginWithGoogle(): void        |
| + loginWithEmail(email, pass): void|
| + generateNewSpot(): void        |
| + navigateWithMaps(): void       |
| + conquerSpot(): void            |
| + shareToSocialMedia(): void     |
| + getSpotDetails(): SpotDetails  |
| + proposeNewSpot(): void         |
+----------------------------------+

+----------------------------------+
|           AdminUser              |
+----------------------------------+
| - id: String                     |
| - email: String                  |
| - password: String               |
| - emailVerified: boolean         |
+----------------------------------+
| + verifyEmail(): void            |
| + generateRevenue(): void        |
| + verifyPayment(): void          |
+----------------------------------+

+----------------------------------+
|             Spot                 |
+----------------------------------+
| - id: String                     |
| - name: String                   |
| - location: Location             |
| - description: String            |
| - rating: float                  |
| - reviews: List<Review>          |
| - conqueredBy: List<User>        |
+----------------------------------+
| + getName(): String              |
| + getLocation(): Location        |
| + getDescription(): String       |
| + getRating(): float             |
| + getReviews(): List<Review>     |
| + getConqueredBy(): List<User>   |
+----------------------------------+

+----------------------------------+
|           Location               |
+----------------------------------+
| - latitude: float                |
| - longitude: float               |
+----------------------------------+
| + getLatitude(): float           |
| + getLongitude(): float          |
+----------------------------------+

+----------------------------------+
|            Review                |
+----------------------------------+
| - id: String                     |
| - user: User                     |
| - rating: float                  |
| - comment: String                |
+----------------------------------+
| + getUser(): User                |
| + getRating(): float             |
| + getComment(): String           |
+----------------------------------+

+----------------------------------+
|         SpotDetails              |
+----------------------------------+
| - spot: Spot                     |
| - additionalInfo: String         |
+----------------------------------+
| + getSpot(): Spot                |
| + getAdditionalInfo(): String    |
+----------------------------------+
```

Explanation:
- The `User` class represents a user of the mobile application and has attributes like `id`, `email`, `password`, and `googleToken`. It provides methods for various user actions, including login, spot generation, navigation, conquering spots, sharing to social media, etc.
- The `AdminUser` class represents an administrative user with additional attributes such as `emailVerified`. Admins have methods for verifying user emails, generating revenue, and verifying payments.
- The `Spot` class represents a specific spot in the application, identified by an `id`. It has attributes like `name`, `location`, `description`, `rating`, `reviews`, and `conqueredBy`. It provides methods to retrieve spot details like name, location, description, rating, reviews, and users who have conquered the spot.
- The `Location` class represents the latitude and longitude coordinates of a spot. It has attributes `latitude` and `longitude` along with corresponding getter methods.
- The `Review` class represents a user review for a spot, identified by an `id`. It has attributes like `user`, `rating`, and `comment`. It provides

 getter methods for user, rating, and comment.
- The `SpotDetails` class represents additional information about a spot, including the spot object itself (`spot`) and `additionalInfo`. It provides getter methods to retrieve the spot object and additional information.

Note: This is just an exemplary class diagram to demonstrate the relationships between the main classes involved in the application. The actual implementation and structure may vary based on the specific requirements and design decisions made during development.  


### Endpoints

1. User Endpoints:
   - `POST /api/login/google` - Login with Google
   - `POST /api/login/email` - Login with email and password
   - `POST /api/spots` - Create a new spot
   - `GET /api/spots/{spotId}` - Get spot details
   - `POST /api/spots/{spotId}/conquer` - Conquer a spot
   - `GET /api/spots/{spotId}/reviews` - Get reviews for a spot
   - `POST /api/spots/{spotId}/reviews` - Add a review for a spot
   - `POST /api/spots/{spotId}/proposals` - Propose a new spot

2. Admin Endpoints:
   - `POST /api/admin/verify-email` - Verify user email
   - `POST /api/admin/revenue` - Generate revenue (ads or lightning network)
   - `POST /api/admin/verify-payment` - Verify user payment
