---
puppeteer:
    pdf:
        format: A4
        displayHeaderFooter: true
        landscape: false
        scale: 0.8
        margin:
            top: 1.2cm
            right: 1cm
            bottom: 1cm
            left: 1cm
    image:
        quality: 100
        fullPage: false
---

Open DID TA Server Installation And Operation Guide
==

- Date: 2025-05-30
- Version: v2.0.0

Table of Contents
==

- [1. Introduction](#1-introduction)
  - [1.1. Overview](#11-overview)
  - [1.2. What is the TA Server?](#12-what-is-the-ta-server)
  - [1.3. System Requirements](#13-system-requirements)
- [2. Preparation](#2-preparation)
  - [2.1. Git Installation](#21-git-installation)
  - [2.2. PostgreSQL Installation](#22-postgresql-installation)
  - [2.3. Installing Node.js](#23-installing-nodejs)
- [3. Cloning the Source Code from GitHub](#3-cloning-the-source-code-from-github)
  - [3.1. Cloning the Source Code](#31-cloning-the-source-code)
  - [3.2. Directory Structure](#32-directory-structure)
- [4. How to Run the Server](#4-how-to-run-the-server)
  - [4.1. Running with IntelliJ IDEA (Gradle Supported)](#41-running-with-intellij-idea-gradle-supported)
    - [4.1.1. Running the Backend (Spring Boot) with IntelliJ IDEA](#411-running-the-backend-spring-boot-with-intellij-idea)
    - [4.1.2. Running the Frontend (React) with VS Code](#412-running-the-frontend-react-with-vs-code)
  - [4.2. Running via Console Commands](#42-running-via-console-commands)
    - [4.2.1. Gradle Build Commands](#421-gradle-build-commands)
    - [4.2.2. Running the Server](#422-running-the-server)
    - [4.2.3. Installing the Database](#423-installing-the-database)
    - [4.2.4. Server Configuration](#424-server-configuration)
  - [4.3. Running with Docker](#43-running-with-docker)
- [5. Configuration Guide](#5-configuration-guide)
  - [5.1. application.yml](#51-applicationyml)
    - [5.1.1. Basic Spring Settings](#511-basic-spring-settings)
    - [5.1.2. Jackson Settings](#512-jackson-settings)
    - [5.1.3. Servlet Settings](#513-servlet-settings)
    - [5.1.4. Actuator Configuration](#514-actuator-configuration)
    - [5.1.5. Server Configuration](#515-server-configuration)
  - [5.2. application-auth.yml](#52-application-authyml)
    - [5.2.1. Token Usage Configuration](#521-token-usage-configuration)
  - [5.3. database.yml](#53-databaseyml)
    - [5.3.1. Spring Liquibase Settings](#531-spring-liquibase-settings)
    - [5.3.2. Datasource Configuration](#532-datasource-configuration)
    - [5.3.3. JPA Configuration](#533-jpa-configuration)
  - [5.4. application-logging.yml](#54-application-loggingyml)
    - [5.4.1. Logging Configuration](#541-logging-configuration)
  - [5.5. application-spring-docs.yml](#55-application-spring-docsyml)
  - [5.6. application-wallet.yml](#56-application-walletyml)
    - [5.6.1. Wallet Access Configuration](#561-wallet-access-configuration)
  - [5.7. application-blockchain.yml](#57-application-blockchainyml)
  - [5.8. blockchain.properties](#58-blockchainproperties)
    - [5.8.1. Blockchain Integration Configuration](#581-blockchain-integration-configuration)
- [6. Profile Configuration and Usage](#6-profile-configuration-and-usage)
  - [6.1. Profile Overview (`sample`, `dev`)](#61-profile-overview-sample-dev)
    - [6.1.1. `sample` Profile](#611-sample-profile)
    - [6.1.2. `dev` Profile](#612-dev-profile)
  - [6.2. How to Set a Profile](#62-how-to-set-a-profile)
    - [6.2.1. When Using an IDE](#621-when-using-an-ide)
    - [6.2.2. When Using Console Commands](#622-when-using-console-commands)
    - [6.2.3. When Using Docker](#623-when-using-docker)
- [7. Running After Building with Docker](#7-running-after-building-with-docker)
  - [7.1. Docker Image Build Method (Based on `Dockerfile`)](#71-docker-image-build-method-based-on-dockerfile)
    - [7.1.1. Build Docker image](#711-build-docker-image)
  - [7.2. Running with Docker Compose](#72-running-with-docker-compose)
    - [7.2.1. Preparing Directories and Configuration Files](#721-preparing-directories-and-configuration-files)
    - [7.2.2. Create `docker-compose.yml` file](#722-create-docker-composeyml-file)
    - [7.2.3. Run Container](#723-run-container)
- [8. Installing PostgreSQL with Docker](#8-installing-postgresql-with-docker)
  - [8.1. Installing PostgreSQL with Docker Compose](#81-installing-postgresql-with-docker-compose)
  - [8.2. Running the PostgreSQL Container](#82-running-the-postgresql-container)
    

# 1. Introduction

## 1.1. Overview

This document provides a guide for installing, configuring, and running the Open DID TA Server.  
The TA Server consists of a Spring Boot-based backend and a React-based Admin Console frontend, which can be integrated and deployed using Gradle.  
The guide explains each step including installation, environment configuration, Docker execution, and profile setup, enabling users to efficiently set up and run the server.

- For the complete installation of OpenDID, refer to the [Open DID Installation Guide].
- For Admin Console configuration, refer to the [Open DID Admin Console Guide].

<br/>

## 1.2. What is the TA Server?

The TA (Trust Agent) Server plays a central role in establishing trust between the server and users within the Open DID ecosystem.  
It verifies the data requested by various components, signs the validated data, and registers it on the blockchain.  
Through this process, trusted data is stored on the blockchain, and the TA Server becomes a core component in building this chain of trust.

<br/>

## 1.3. System Requirements
- **Java 21** or higher
- **Gradle 7.0** or higher
- **Docker** and **Docker Compose** (when using Docker)
- At least **2GB RAM** and **10GB of disk space**

<br/>

# 2. Preparation

This chapter provides the necessary preparatory steps before installing the components of the Open DID project.

## 2.1. Git Installation

`Git` is a distributed version control system that tracks changes in the source code and supports collaboration among multiple developers. Git is essential for managing the source code and version control of the Open DID project.

After a successful installation, you can check the version of Git with the following command:
```bash
git --version
```

> **Reference Links**
> - [Git Installation Guide](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository)

<br/>

## 2.2. PostgreSQL Installation
To run the TA server, a database installation is required, and Open DID uses PostgreSQL.

> **Reference Links**
- [PostgreSQL Installation Guide](https://www.postgresql.org/download/)
- [8. Installing PostgreSQL with Docker](#8-installing-postgresql-with-docker)

<br/>

## 2.3. Installing Node.js

To run the React-based TA Admin Console, you need to install both `Node.js` and `npm`.

npm (Node Package Manager) is used to install and manage dependencies required for frontend development.

After installation is complete, you can verify that it was installed correctly with the following commands:

```bash
node --version
npm --version
```

---
> **Reference Links**  
> - [Official Node.js Download Page](https://nodejs.org/)  
> - It is recommended to install the LTS (Long Term Support) version.

> ✅ Installation Check Tip  
> If running `node -v` and `npm -v` outputs version information, the installation was successful.
---

<br/>


# 3. Cloning the Source Code from GitHub

## 3.1. Cloning the Source Code

The `git clone` command allows you to copy the source code from a remote repository hosted on GitHub to your local computer. By using this command, you can work on the entire source code and related files locally. After cloning, you can proceed with the necessary tasks within the repository and push any changes back to the remote repository.

Open the terminal and run the following commands to copy the TA server repository to your local computer:
```bash
# Clone the repository from the Git repository
git clone https://github.com/OmniOneID/did-ta-server.git

# Navigate to the cloned repository
cd did-tas-server
```

> **Reference Links**
> - [Git Clone Guide](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository)

<br/>

## 3.2. Directory Structure

The main directory structure of the cloned project is as follows:

```
did-ta-server
├── CHANGELOG.md
├── CLA.md
├── CODE_OF_CONDUCT.md
├── CONTRIBUTING.md
├── dependencies-license.md
├── MAINTAINERS.md
├── README.md
├── RELEASE-PROCESS.md
├── SECURITY.md
├── docs
│   └── admin
│       ├── OpenDID_TAAdmin_Operation_Guide.md
│       └── OpenDID_TAAdmin_Operation_Guide_ko.md
│   └── api
│       ├── TAS_API.md
│       └── TAS_API_ko.md
│   └── errorCode
│       ├── TAS_ErrorCode.md
│       └── TAS_ErrorCode_ko.md
│   └── installation
│       ├── OpenDID_TASServer_InstallationAndOperation_Guide.md
│       └── OpenDID_TASServer_InstallationAndOperation_Guide_ko.md
│   └── db
│       ├── OpenDID_TableDefinition_TAS.md
│       └── OpenDID_TableDefinition_TAS_ko.md
└── source
    └── did-ta-server
        ├── gradle
        ├── libs
            └── did-sdk-common-2.0.0.jar
            └── did-blockchain-sdk-server-2.0.0.jar
            └── did-core-sdk-server-2.0.0.jar
            └── did-crypto-sdk-server-2.0.0.jar
            └── did-datamodel-server-2.0.0.jar
            └── did-wallet-sdk-server-2.0.0.jar
            └── did-zkp-sdk-server-2.0.0.jar
        ├── sample
        └── src
        └── build.gradle
        └── README.md
    └── did-ta-admin        
        ├── frontend
```

| Name                    | Description                                              |
| ----------------------- | -------------------------------------------------------- |
| CHANGELOG.md            | Version history of the project                          |
| CODE_OF_CONDUCT.md      | Code of conduct for contributors                        |
| CONTRIBUTING.md         | Contribution guidelines and procedures                  |
| dependencies-license.md | License information of project dependencies             |
| MAINTAINERS.md          | Guidelines for project maintainers                      |
| RELEASE-PROCESS.md      | Process for releasing new versions                      |
| SECURITY.md             | Security policy and vulnerability reporting instructions|
| docs                    | Documentation folder                                    |
| ┖ api                   | API guide documentation                                 |
| ┖ errorCode             | Error code and troubleshooting guide                    |
| ┖ installation          | Installation and configuration guide                    |
| ┖ db                    | Database ERD and table specifications                   |
| source                  | Source code                                              |
| ┖ did-tas-server        | TA server source code and build files                   |
| ┖ gradle                | Gradle build settings and scripts                       |
| ┖ libs                  | External libraries and dependencies                     |
| ┖ sample                | Sample files                                             |
| ┖ src                   | Main source code directory                              |
| ┖ build.gradle          | Gradle build configuration file                         |
| ┖ README.md             | Overview and instructions for the source code           |
| ┖ did-ta-admin          | TA Admin source code                                    |
| ┖ frontend              | TA Admin frontend source code                           |

<br/>

# 4. How to Run the Server

This chapter explains three ways to run the server.

The project source code is located under the `source` directory. You need to load and configure the source from this directory depending on the chosen method.

1. **Using an IDE**: You can open the project in an Integrated Development Environment (IDE), set up the run configuration, and launch the server directly. This method is useful for testing code changes during development.

2. **Using Console Commands after Build**: After building the project, you can run the generated JAR file using the console command (`java -jar`). This method is mainly used for deployment or running the server in a production environment.

3. **Building and Running with Docker**: You can build a Docker image of the server and run it in a Docker container. This ensures consistency across environments and makes deployment and scaling easier.

## 4.1. Running with IntelliJ IDEA (Gradle Supported)

The Open DID project is composed of a backend (based on Spring Boot) and a frontend (based on React), which can be developed and run using IntelliJ IDEA and VS Code respectively.

### 4.1.1. Running the Backend (Spring Boot) with IntelliJ IDEA

#### 4.1.1.1. Install and Set Up IntelliJ IDEA

1. Install IntelliJ IDEA. (Refer to the link below for installation instructions)

> **Reference**
> - [Download IntelliJ IDEA](https://www.jetbrains.com/idea/download/)

#### 4.1.1.2. Open the Project

- Launch IntelliJ and select `File -> New -> Project from Existing Sources`.  
- When the file selection window opens, navigate to the `source/did-tas-server` folder cloned in [3.1. Clone the Repository](#31-clone-the-repository).
- IntelliJ will automatically recognize the `build.gradle` file.
- Gradle will download the required dependencies automatically. Wait until the process is completed.

#### 4.1.1.3. Gradle Build

- In IntelliJ’s `Gradle` tab, run `Tasks -> build -> build`.
- Once the build completes successfully, the project is ready to be executed.

#### 4.1.1.4. Run the Server

- In the Gradle tab, go to `Tasks -> application -> bootRun` and execute.
- Gradle will automatically build and run the server.
- Check the console for the message: `Started [ApplicationName] in [time] seconds` to confirm the server started successfully.
- Once the server is running, go to [http://localhost:8090/swagger-ui/index.html](http://localhost:8090/swagger-ui/index.html) in your browser to verify the Swagger UI is working.

> **Note**
> - The TA server is initially configured with the `sample` profile.
> - The `sample` profile skips required settings (e.g., database). See [6. Profile Configuration and Usage](#6-profile-configuration-and-usage) for more details.

#### 4.1.1.5. Install the Database

The TA server stores operational data in a database, so PostgreSQL must be installed to run the server properly.  
Although there are multiple ways to install PostgreSQL, using Docker is the simplest and easiest.  
Refer to [2.2. Install PostgreSQL](#22-install-postgresql) for installation instructions.

#### 4.1.1.6. Configure the Server

- You must adjust server configurations according to your deployment environment for stable operation.  
  For example, database connection settings, port numbers, email configuration, etc.
- The configuration files are located at `src/main/resources/config`.
- For detailed configuration instructions, see [5. Configuration Guide](#5-configuration-guide).

<br/>

### 4.1.2. Running the Frontend (React) with VS Code

The TA Admin Console is based on React and can be run independently in VS Code.  
This is useful for frontend development and UI testing.

#### 4.1.2.1. Install VS Code

- [Download VS Code](https://code.visualstudio.com/)

#### 4.1.2.2. Open the Project

- Open the `source/did-issuer-admin` directory in VS Code.

#### 4.1.2.3. Install Dependencies

```bash
npm install
```

#### 4.1.2.4. Start the Development Server

```bash
npm run dev
```

- Default Access URL: [http://localhost:8090](http://localhost:8090)

> 📌 **Note:**  
> The backend (Spring Boot server) must be running separately.  
> The frontend API server URL can be configured in the `vite.config.ts` file or an environment/config file.

<br/>

## 4.2. Running via Console Commands

This section explains how to run the Open DID server using console commands.  
You'll use Gradle to build the project and run the server using the generated JAR file.

- When building with Gradle, the frontend (Admin Console) is also automatically built and included as static resources.

### 4.2.1. Gradle Build Commands

Use `gradlew` to build the source:

```shell
# Navigate to the source folder of the cloned repository
cd source/did-tas-server

# Grant execute permissions to the Gradle Wrapper
chmod 755 ./gradlew

# Clean and build the project (removes previous build files and rebuilds)
./gradlew clean build
```

> Notes:  
> - `gradlew` is short for Gradle Wrapper. It is a script used to run Gradle for the project. Even if Gradle is not installed locally, it will automatically download and use the version specified in the project.  
>   This ensures all developers build the project in the same environment.  
> - If you don't need to build the frontend (e.g., testing backend only or already bundled frontend), you can skip it by adding the following option:  
>   - `./gradlew clean build -DskipFrontendBuild=true`

After the build is complete, navigate to the folder containing the generated JAR file:

```shell
cd build/libs
ls
```

You should see the file: `did-tas-server-2.0.0.jar`

<br/>

### 4.2.2. Running the Server

Run the server using the built JAR file:

```bash
java -jar did-tas-server-2.0.0.jar
```

Once the server is running, open your browser and visit [http://localhost:8090/swagger-ui/index.html](http://localhost:8090/swagger-ui/index.html) to check that the Swagger UI is working properly.

> **Note**
> - The TA server is initially configured with the `sample` profile.
> - With the `sample` profile, required configurations (e.g., database) are ignored.  
>   Refer to [6. Profile Configuration and Usage](#6-profile-configuration-and-usage) for more details.

<br/>

### 4.2.3. Installing the Database

The TA server stores operational data in a database, so it must be installed before running the server.  
Open DID uses a PostgreSQL database. There are several installation methods, but using Docker is the simplest and easiest.  
Refer to [2.2. Install PostgreSQL](#22-install-postgresql) for instructions.

<br/>

### 4.2.4. Server Configuration

- You must modify the configuration to suit your deployment environment to ensure stable server operation.  
  For example, database connection settings, port numbers, email integration, etc.
- The configuration files are located in the `src/main/resources/config` directory.
- For detailed configuration instructions, refer to [5. Configuration Guide](#5-configuration-guide).

<br/>

## 4.3. Running with Docker

For Docker image build, configuration, and execution steps, refer to [7. Build and Run with Docker](#7-build-and-run-with-docker).

<br/>

# 5. Configuration Guide

This chapter explains each configuration value included in the server's configuration files.  
These settings are critical for controlling the server's behavior and environment.  
Proper configuration is essential for stable server operation.  
Refer to each item’s description and examples to apply settings that fit your environment.

🔒 Settings marked with this icon are either fixed by default or generally do not need to be modified.

## 5.1. application.yml

- Purpose: The `application.yml` file defines the basic settings for a Spring Boot application.  
  It is used to configure the application name, database settings, profile settings, and other environment variables that affect how the application operates.

- Location: `src/main/resources/config`

### 5.1.1. Basic Spring Settings

Spring's basic settings define the application's name and active profiles, playing a key role in configuring the environment.

* `spring.application.name`: 🔒  
    - Specifies the name of the application.  
    - Usage: Mainly used in log messages, monitoring tools, or to identify the application in Spring Cloud environments.  
    - Example: `tas`

* `spring.profiles.active`:  
  - Specifies the active profile.  
  - Usage: Choose either the sample or development profile to load appropriate settings.  
  - For more details about profiles, refer to [6. Profile Configuration and Usage](#6-profile-configuration-and-usage).  
  - Supported profiles: `sample`, `dev`  
  - Example: `sample`, `dev`

* `spring.profiles.group.dev`: 🔒  
  - Defines the individual profiles included in the `dev` profile group.  
  - Usage: Used to manage settings grouped for development environments.  
  - Profile naming rule: The settings file name must match the entry in the group. For example, the `auth` profile must be defined in `application-auth.yml`, and the `databases` profile in `application-databases.yml`.

* `spring.profiles.group.sample`: 🔒  
  - Defines the individual profiles included in the `sample` profile group.  
  - Usage: Used to manage settings grouped for sample/demo environments.  
  - Profile naming rule: Same as above — use the names exactly as they appear in the group (e.g., `application-auth.yml`, `application-databases.yml`).

<br/>

### 5.1.2. Jackson Settings

Jackson is the default JSON serialization/deserialization library used in Spring Boot.  
Its settings allow control over how JSON data is serialized and formatted, which can improve performance and efficiency in data transmission.

* `spring.jackson.default-property-inclusion`: 🔒  
    - Configures the application to skip serializing fields with null values.  
    - Example: `non_null`

* `spring.jackson.default-property-inclusion`: 🔒  
    - Prevents errors when serializing empty objects.  
    - Example: `false`

<br/>

### 5.1.3. Servlet Settings

Servlet settings control web requests such as file uploads.

* `spring.servlet.multipart.enabled`: 🔒  
    - Enables multipart upload functionality.  
    - Usage: Required to support file uploads.  
    - Example: `true`

* `spring.servlet.multipart.max-file-size`:  
    - Specifies the maximum size for an uploaded file.  
    - Example: `10MB`

* `spring.servlet.multipart.max-request-size`:  
    - Specifies the maximum total size for a multipart request.  
    - Usage: Used to restrict the size of file upload requests.  
    - Example: `10MB`

<br/>

### 5.1.4. Actuator Configuration

Spring Boot Actuator provides various endpoints for monitoring and managing the application’s status.  
Since these endpoints may expose sensitive information, it is common practice to expose only the necessary ones and restrict detailed data.

* `management.endpoints.web.exposure.include`: 🔒  
    - Specifies which Actuator endpoints will be exposed externally.  
    - Example: `health`, `shutdown`

* `management.endpoint.health.show-details`: 🔒  
    - Determines how much detail is shown in the `health` endpoint.  
    - Example: `never` (default, hides details from unauthenticated users)

* `management.endpoint.shutdown.enabled`: 🔒  
    - Enables or disables the `/actuator/shutdown` endpoint.  
    - Example: `true` (allows remote shutdown of the server)

---
> For security reasons, it is not recommended to enable the `shutdown` endpoint in production environments.  
> However, if you are using the Orchestrator, this Actuator configuration is required to start and stop the server through it.
---

### 5.1.5. Server Configuration

This section defines the port number on which the application will receive incoming requests.

* `server.port`:  
    - Specifies the port the application runs on. The default port for the TA Server is 8090.  
    - Value: `8090`

<br/>

## 5.2. application-auth.yml

- Purpose: The `application-auth.yml` file manages authentication-related settings, such as whether to enable token-based authentication.

- Location: `src/main/resources/config`

### 5.2.1. Token Usage Configuration

* `spring.servlet.multipart.enabled`: 🔒  
    - Sets whether token-based authentication should be enabled.  
    - Usage: If set to `true`, token authentication is enabled. If set to `false`, it is disabled.  
    - Example: `false`

<br/>

## 5.3. database.yml

- Purpose: This file defines how the server connects to and manages the database, including connection details, Liquibase migration settings, and JPA behavior.

- Location: `src/main/resources/config`

### 5.3.1. Spring Liquibase Settings

Liquibase is a database migration tool that tracks and automatically applies schema changes.  
It ensures database consistency across development and production environments.

* `spring.liquibase.change-log`: 🔒  
    - Specifies the location of the Liquibase change log file. This file tracks and applies schema changes.  
    - Example: `classpath:/db/changelog/master.xml`

* `spring.liquibase.enabled`: 🔒  
    - Enables or disables Liquibase. When set to `true`, Liquibase will apply migrations on application startup.  
    - For the `sample` profile, it must be set to `false` because it doesn't use a real database.  
    - Example: `true` [dev], `false` [sample]

* `spring.liquibase.fall-on-error`: 🔒  
    - Controls the behavior when an error occurs during migration. Only used in the `sample` profile.  
    - Example: `false` [sample]

<br/>

### 5.3.2. Datasource Configuration

Datasource settings define the basic database connection details used by the application.  
This includes the database driver, URL, username, and password.

* `spring.datasource.driver-class-name`: 🔒  
    - Specifies the JDBC driver class for connecting to the database.  
    - Example: `org.postgresql.Driver`

* `spring.datasource.url`:  
    - The database connection URL specifying the location and name of the database.  
    - Example: `jdbc:postgresql://localhost:5432/tas`

* `spring.datasource.username`:  
    - The username for accessing the database.  
    - Example: `omn`

* `spring.datasource.password`:  
    - The password for accessing the database.  
    - Example: `omn`

---
> If the database is installed via Orchestrator, the default connection information is as follows:  
> - `url`: `jdbc:postgresql://localhost:5432/tas`  
> - `username`: `omn`  
> - `password`: `omn`
---

<br/>

### 5.3.3. JPA Configuration

JPA (Java Persistence API) settings control how the application interacts with the database.  
These configurations affect performance, debugging, and how entities are mapped.

* `spring.jpa.open-in-view`: 🔒  
    - Enables or disables the Open Session In View (OSIV) pattern.  
      When `true`, the database session is kept open for the duration of the HTTP request.  
    - Example: `true`

* `spring.jpa.show-sql`: 🔒  
    - Logs executed SQL queries.  
      Useful for debugging during development.  
    - Example: `true`

* `spring.jpa.hibernate.ddl-auto`: 🔒  
    - Sets the schema generation strategy used by Hibernate.  
      Setting this to `none` disables automatic schema creation or updates.  
    - Example: `none`

* `spring.jpa.hibernate.naming.physical-strategy`: 🔒  
    - Configures the naming strategy for database objects.  
      This determines how entity class names are mapped to table names.  
    - Example: `org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy`

* `spring.jpa.properties.hibernate.format_sql`: 🔒  
    - Enables or disables SQL formatting in logs.  
      When set to `false`, SQL statements in logs are not formatted.  
    - Example: `false`

<br/>

## 5.4. application-logging.yml

- Purpose: Defines log groups and log levels. This configuration file allows you to group specific packages or modules under a log group and assign individual log levels to each group.

- Location: `src/main/resources/config`

### 5.4.1. Logging Configuration

- **Log Groups**: You can group desired packages using `logging.group`.  
  For example, the `util` group may include the package `org.omnione.did.base.util`, and other groups can be defined similarly.

- **Log Levels**: You can specify log levels for each group using `logging.level`.  
  Levels such as `debug`, `info`, `warn`, `error`, etc., can be set to control the verbosity of logs.  
  For example, setting the `tas` and `aop` groups to `debug` will output detailed debug information from those packages.

* `logging.level`:  
    - Sets the log level.  
    - Setting it to `debug` allows viewing all messages at DEBUG level and above (INFO, WARN, ERROR, FATAL).

Full example:
```yaml
logging:
  group:
    util:
      - org.omnione.did.base.util
    list:
      - org.omnione.did.list
    tas:
      - org.omnione.did.tas
    noti:
      - org.omnione.did.noti
    aop:
      - org.omnione.did.base.aop
  level:
    list: debug
    tas: debug
    aop: debug
    noti: debug
    util: debug
```

<br/>

## 5.5. application-spring-docs.yml

- Purpose: Manages settings for SpringDoc and Swagger UI in the application.

- Location: `src/main/resources/config`

* `springdoc.swagger-ui.path`: 🔒  
  - Defines the URL path for accessing Swagger UI.  
  - Example: `/swagger-ui.html`

* `springdoc.swagger-ui.groups-order`: 🔒  
  - Specifies the order in which API groups appear in Swagger UI.  
  - Example: `ASC`

* `springdoc.swagger-ui.operations-sorter`: 🔒  
  - Sorts API endpoints in Swagger UI by HTTP method.  
  - Example: `method`

* `springdoc.swagger-ui.disable-swagger-default-url`: 🔒  
  - Disables the default Swagger URL.  
  - Example: `true`

* `springdoc.swagger-ui.display-request-duration`: 🔒  
  - Whether to display request duration in Swagger UI.  
  - Example: `true`

* `springdoc.api-docs.path`: 🔒  
  - Defines the path where API documentation is served.  
  - Example: `/api-docs`

* `springdoc.show-actuator`: 🔒  
  - Determines whether to include Actuator endpoints in the API documentation.  
  - Example: `true`

* `springdoc.default-consumes-media-type`: 🔒  
  - Sets the default media type for request bodies in the API docs.  
  - Example: `application/json`

* `springdoc.default-produces-media-type`: 🔒  
  - Sets the default media type for response bodies in the API docs.  
  - Example: `application/json`

<br/>


## 5.6. application-wallet.yml

- Purpose: Defines wallet file information used by the server.

- Location: `src/main/resources/config`

### 5.6.1. Wallet Access Configuration

* `wallet.file-path`:  
    - Specifies the path to the wallet file. This file stores the wallet and may contain sensitive information such as private keys.  
      *You must use an absolute path.*  
    - Example: `/path/to/your/tas.wallet`

* `wallet.password`:  
    - Password used to access the wallet. This is a sensitive value required when accessing the wallet file.  
    - Example: `your_secure_wallet_password`

---
> If the wallet file is created using the Orchestrator, the default password is as follows:  
> - `password`: `omnioneopendid12!@`
---

<br/>

## 5.7. application-blockchain.yml

- Purpose: Specifies the path to the file that contains blockchain integration information.

* `blockchain.file-path`:  
    - Defines the path to the blockchain configuration file (`blockchain.properties`).  
    - Both absolute and relative paths are supported.  
    - Example: `/path/to/your/blockchain.properties`

<br/>

## 5.8. blockchain.properties

- Role: Configures blockchain server information for integration with the TA server. When you install the Hyperledger Besu network according to '5.3. Step 3: Blockchain Installation' in [Open DID Installation Guide], private keys, certificates, and server connection information configuration files are automatically generated. In blockchain.properties, you set the paths where these files are located and the network name entered during Hyperledger Besu installation.

- Location: `src/main/resources/properties`

### 5.8.1. Blockchain Integration Configuration

#### EVM Network Configuration

- `evm.network.url`:
  - EVM Network address. Use this fixed value when running Besu on the same local as the client. (Default Port: 8545)
  - Example: http://localhost:8545

- `evm.chainId`:
  - Chain ID identifier. Currently uses a fixed value of 1337. (Default Value: 1337)
  - Example: 1337

- `evm.gas.limit`:
  - Maximum gas limit allowed for Hyperledger Besu EVM transactions. Currently uses a fixed value as Free Gas. (Default Value: 100000000)
  - Example: 100000000

- `evm.gas.price`:
  - Gas price per unit. Currently uses a fixed value of 0 as Free Gas. (Default Value: 0)
  - Example: 0

- `evm.connection.timeout`: 
  - Network connection timeout value (milliseconds). Currently uses the recommended fixed value of 10000. (Default Value: 10000)
  - Example: 10000

#### EVM Contract Configuration

- `evm.connection.address`: 
  - Address value of the OpenDID Contract returned when deploying Smart Contract with Hardhat. For detailed guide, refer to [DID Besu Contract].
  - Example: 0xa0E49611FB410c00f425E83A4240e1681c51DDf4

- `evm.connection.privateKey`: 
  - k1 key used for API access control. Enter the key string defined in accounts inside hardhat.config.js (remove the 0x string at the beginning) to enable API calls with Owner privileges (Default setting). For detailed guide, refer to [DID Besu Contract].
  - Example: 0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63
<br/>

# 6. Profile Configuration and Usage

## 6.1. Profile Overview (`sample`, `dev`)

The TA Server supports two profiles, `dev` and `sample`, to support execution in various environments.

Each profile is designed to load environment-specific configurations. By default, the TA Server is set to use the `sample` profile, which allows it to run independently without integrating with external services such as databases or blockchains. The `sample` profile is ideal for testing API calls and verifying the basic behavior of the application. It returns fixed response data for all API calls, making it useful in early development phases.

Sample API calls are provided as JUnit tests, which can be used as references when writing new tests.

In contrast, the `dev` profile is designed to operate with real services. It enables testing and verification with actual data. When using the `dev` profile, the application connects to real external services such as a database or blockchain network, allowing you to test real-world behaviors.

### 6.1.1. `sample` Profile

The `sample` profile is configured to run the server independently without connecting to external services like databases or blockchains. It is ideal for testing API calls and allows developers to quickly verify the core functionality of the application. All APIs return fixed responses, making this profile useful for feature testing or initial development. Since no external services are required, the server can be launched and tested in complete isolation.

---
> Note: The Admin Console does not function with the `sample` profile.
---

### 6.1.2. `dev` Profile

The `dev` profile includes settings appropriate for development environments and is intended for use on development servers. This profile requires proper configuration of a real database and blockchain nodes.

## 6.2. How to Set a Profile

This section explains how to activate a specific profile depending on how the server is launched.

### 6.2.1. When Using an IDE

- **Select configuration file:** Open `application.yml` located in `src/main/resources`.
- **Set profile:** In the IDE's Run/Debug Configuration, add the option `--spring.profiles.active={profile}` to activate the desired profile.
- **Apply settings:** The configuration for the specified profile will be applied at runtime.

### 6.2.2. When Using Console Commands

- **Prepare configuration file:** Ensure the configuration files for the selected profile are available in the same directory as the JAR or in the appropriate config path.
- **Set profile:** Run the following command to activate the desired profile:

  ```bash
  java -jar build/libs/did-tas-server-2.0.0.jar --spring.profiles.active={profile}
  ```

- **Apply settings:** The specified profile will be used during execution.

### 6.2.3. When Using Docker

- **Select configuration file:** During Docker image build, specify the configuration path in the Dockerfile, or mount the config file into the container.
- **Set profile:** Use the `SPRING_PROFILES_ACTIVE` environment variable in the Docker Compose file or Docker run command:

  ```yaml
  environment:
    - SPRING_PROFILES_ACTIVE={profile}
  ```

- **Apply settings:** The configuration for the specified profile will be used when the container starts.

You can flexibly switch between profiles based on the method of execution and apply appropriate settings for different environments.

<br/>

# 7. Running After Building with Docker

## 7.1. Docker Image Build Method (Based on `Dockerfile`)

### 7.1.1. Build Docker image
Build the Docker image with the following command:

```bash
cd {source_directory}
docker build -t did-ta-server -f did-ta-server/Dockerfile .
```

<br/>

## 7.2. Running with Docker Compose

### 7.2.1. Preparing Directories and Configuration Files

#### 1. Create docker-compose directory and config directory
```bash
mkdir -p {docker_compose_directory}/config
```

#### 2. Copy configuration files (yml) to config directory
```bash
cp {application_yml_directory}/* {docker_compose_directory}/config/
cp {blockchain_properties_path} {docker_compose_directory}/config/
```

#### 3. Modify blockchain.properties file
```yml
evm.network.url=http://host.docker.internal:8545
... (omitted)
```

> **host.docker.internal** is a special address that points to the host machine from within a Docker container.  
> Since localhost inside a container refers to the container itself, you must use host.docker.internal to access services (PostgreSQL, blockchain) running on the host.

#### 4. Modify application-database.yml file
```yml
spring:
 ... (omitted)
 datasource:
   driver-class-name: org.postgresql.Driver
   url: jdbc:postgresql://host.docker.internal:5430/tas
   username: omn
   password: omn
 ... (omitted)
```

### 7.2.2. Create `docker-compose.yml` file
You can easily manage multiple containers using the `docker-compose.yml` file.

```yml
version: '3'
services:
 app:
   image: did-ta-server
   ports:
     - "8090:8090"
   volumes:
     - {config_directory}:/app/config
   environment:
     - SPRING_PROFILES_ACTIVE=dev
   extra_hosts:
     - "host.docker.internal:host-gateway"
```

> - In the example above, the `config_directory` is mounted to `/app/config` inside the container to share configuration files.
>   - Configuration files located in `config_directory` take priority over default configuration files.
>   - For detailed configuration instructions, please refer to [5. Configuration Guide](#5-configuration-guide).


### 7.2.3. Run Container
```bash
cd {docker_compose_directory}
docker-compose up -d
```

<br/>

# 8. Installing PostgreSQL with Docker

This section explains how to install PostgreSQL using Docker. This method allows you to easily set up PostgreSQL and connect it to the server.

## 8.1. Installing PostgreSQL with Docker Compose

Below is an example of how to install PostgreSQL using Docker Compose.

```yml
services:
  postgres:
    container_name: postgre-tas
    image: postgres:16.4
    restart: always
    volumes:
      - postgres_data_tas:/var/lib/postgresql/data
    ports:
      - 5430:5432
    environment:
      POSTGRES_USER: ${USER}
      POSTGRES_PASSWORD: ${PW}
      POSTGRES_DB: tas

volumes:
  postgres_data:
```

This Docker Compose file installs PostgreSQL version 16.4 with the following configurations:

- **container_name**: Sets the container name to `postgre-tas`.
- **volumes**: Mounts the `postgres_data_tas` volume to PostgreSQL’s data directory (`/var/lib/postgresql/data`). This ensures data persistence.
- **ports**: Maps port 5430 on the host to port 5432 in the container.
- **environment**: Sets the PostgreSQL username, password, and database name. `${USER}` and `${PW}` are environment variables you can define.

## 8.2. Running the PostgreSQL Container

To run the PostgreSQL container using the Docker Compose file above, execute the following command in the terminal:

```bash
docker-compose up -d
```

This command runs the PostgreSQL container in the background. Based on the configured environment variables, the PostgreSQL server will start and the database will be ready for use. You can then proceed to connect your application to this database.

<!-- References -->
[Open DID Installation Guide]: https://github.com/OmniOneID/did-release/blob/develop/release-V2.0.0.0/OpenDID_Installation_Guide-V2.0.0.0_ko.md
[DID Besu Contract]: https://github.com/OmniOneID/did-besu-contract
[Open DID Admin Console Guide]: ../admin/OpenDID_TAAdmin_InstallationAndOperation_Guide_ko.md
