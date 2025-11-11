# 🧩 Nexus Chunk

[![License: CC BY-NC 4.0](https://img.shields.io/badge/License-CC%20BY--NC%204.0-lightgrey.svg)](https://creativecommons.org/licenses/by-nc/4.0/)
[![GitHub stars](https://img.shields.io/github/stars/Invouker/NexusChunk?style=social)](https://github.com/Invouker/NexusChunk/stargazers)

> A fast, scalable, and modular web application designed to integrate a Minecraft (PaperMC/Spigot) server with a web portal. Nexus Chunk functions as a centralized hub for managing server statistics, player ranks, and administrative controls via an accessible web API.

---

### ✨ Key Features
- Public API & Server Plugin: Core API is seamlessly integrated with a dedicated server plugin (PaperMC/Spigot) to bridge web functionality and the game server.
- Player Synchronization: Robust synchronization layer for managing and updating player data, specifically synchronizing ranks between the web and the game server.
- Detailed Server Statistics: Comprehensive tracking and display of in-game statistics, including blocks mined, total server currency (economy), and leaderboards for top players.
- Remote Administration: Secure access for administrators to remotely manage the server, including console command execution and server restarts.

### Modular Architecture: Designed for simple modification and extensibility.
- Public API: Fully accessible API for easy code and data manipulation.
- Modular Architecture: Designed for simple modification and extensibility.
- User Authorization: Support for modern authentication methods (e.g., OAuth2/Google).
- Reliable Data Layer: Utilizing Mariadb and JPA for data persistence.


# 🛠️ Project Setup (Development)

The project is built on Spring Boot and uses the Gradle Wrapper (`gradlew`).

## 1. Cloning and Building

### 1. Clone the repository and navigate into it
git clone [https://github.com/Invouker/NexusChunk.git](https://github.com/Invouker/NexusChunk.git)

### 2. Compile the project and create the executable JAR file using Gradle Wrapper
```./gradlew clean build ```

Note: For Windows, use the command ```.\gradlew.bat```.

### 3. Environment Configuration
The project requires the following environment variables. Set these in a .env file or directly in your environment.


```
#### JDBC URL. NOTE the exact syntax: 
SPRING_DB_HOST= # jdbc:mariadb://HOST:PORT/DB_NAME
SPRING_DB_USERNAME= # Username for database
SPRING_DB_PASSWORD= # Password for database

### C. EMAIL SERVICES (Gmail SMTP)
SPRING_MAIL_USERNAME= # Your Gmail address (xxx@gmail.com)
SPRING_MAIL_APP_PASSWORD= # Generated App Password (NOT your regular password)

### D. EXTERNAL SERVICES

SPRING_GOOGLE_OAUTH_SECRET=  # Google OAuth 2.0 Client Secret
SPRING_RECAPTCHA_SECRET_KEY= # Google reCAPTCHA Secret Key

# GITHUB API Token (Optional) (It's about limitaion of request for admin panel - commits list)
SPRING_GITHUB_TOKEN=
```

### 4. Running the Application
After a successful build, the executable JAR is located in build/libs/.

### 5. Run the application
```java -jar build/libs/NexusChunk-*.jar ```

## ⚖️ License
This project is licensed under the terms of the Creative Commons Attribution-NonCommercial 4.0 International Public License (CC BY-NC 4.0).

Key License Points:
CC BY (Attribution): You must give appropriate credit to the original author (Invouker/Nexus Chunk).
NC (Non-Commercial): The code may not be resold or used for a primary commercial (profit) purpose. You are free to modify and use it for non-commercial projects, research, or internal development.
For the full license text, see the LICENSE file.

### ✉️ Contact
Name: [Invouk] Project Link: https://github.com/Invouker/NexusChunk
