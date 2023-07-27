# BiteSpeed BackEnd Task : Identity Rec 

This is a dockerized Spring Boot Application with postgres@14 as its database, 
that can be usedb to add Contacts of users to the table with linkage to all previous 
contacts he used and any upcoming ones.

## Getting Started

### Prerequisites

Before getting started, make sure you have the following tools installed:

- Git
- Docker
- Docker Compose
## Installation and Running 
1. Clone the repository:

   ```bash
   git clone https://github.com/cryptoblizz/BitespeedIdRec.git
2. Step into directory and run
    ```bash
    cd BitespeedIdRec
    docker-compose up
   
This will spin up the application and you can test the endpoint provided below
3. To remove all previous runs data and have a clean start of database use this step before docker compose up
   ```bash
   docker compose down -v
   
*******

Exposed endpoint: http://localhost:8080/identity (only POST Request in format http://localhost:8080/identity?email=mailb&phoneNumber=111)

****

To learn more about the problem statement go to : https://bitespeed.notion.site/Bitespeed-Backend-Task-Identity-Reconciliation-53392ab01fe149fab989422300423199

