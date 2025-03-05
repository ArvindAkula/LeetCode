# Agent-User Referral System

A Spring Boot application using GraphQL and DynamoDB to manage agent-user referrals.

## Overview

This system handles agent referrals of users based on phone numbers:

- When an agent refers a user, the system saves the agent ID and phone number mapping
- When a user is created, the system checks if their phone number is associated with an agent and assigns accordingly
- If multiple agents have referred the same phone number, the system assigns the most recent referral

## Technical Stack

- **Spring Boot 3.1.0**: Core application framework
- **GraphQL Java Kickstart**: GraphQL implementation
- **AWS DynamoDB**: NoSQL database for data storage
- **Lombok**: Reduce boilerplate code
- **TestContainers**: Integration testing with containerized DynamoDB

## Data Model

The system contains three main entities:

1. **Agent**: Represents a referring agent
   - Properties: id, name, email, createdAt, updatedAt

2. **User**: Represents a user who may have been referred
   - Properties: id, name, phoneNumber, email, agentId, createdAt, updatedAt

3. **AgentReferral**: Maps phone numbers to referring agents
   - Properties: phoneNumber (PK), agentId, referralDate

## Key Features

- Agent creation and management
- User creation with automatic agent assignment
- Referral tracking with timestamps
- GraphQL API for all operations
- Complete test coverage for referral scenarios

## GraphQL API

The API provides the following operations:

### Queries
- Get agent by ID
- Get all agents
- Get user by ID
- Get user by phone number
- Get all users
- Get referral by phone number
- Get referrals by agent ID
- Get all referrals
- Get latest referral by agent ID

### Mutations
- Create/update/delete agent
- Create/update/delete user
- Create/delete referral

## Setup and Running

### Prerequisites
- Java 17+
- Gradle
- DynamoDB Local (for development)

### Running Locally

1. Start DynamoDB Local:
   ```
   docker run -p 8000:8000 amazon/dynamodb-local
   ```

2. Build and run the application:
   ```
   ./gradlew bootRun
   ```

3. Access GraphiQL interface:
   ```
   http://localhost:8080/graphiql
   ```

### Configuration

The application can be configured through the `application.properties` file:

- DynamoDB endpoint
- AWS region
- AWS credentials
- Server port
- Logging levels

## Testing

Run the tests with:
```
./gradlew test
```

The integration tests use TestContainers to spin up a DynamoDB container automatically.

## Example Queries

### Create an Agent
```graphql
mutation {
  createAgent(input: {
    name: "John Doe",
    email: "john@example.com"
  }) {
    id
    name
    email
  }
}
```

### Create a Referral
```graphql
mutation {
  createReferral(
    agentId: "agent-id-here",
    phoneNumber: "+1234567890"
  ) {
    phoneNumber
    agentId
    referralDate
  }
}
```

### Create a User (will be automatically linked to agent if phone number was referred)
```graphql
mutation {
  createUser(input: {
    name: "Jane Smith",
    phoneNumber: "+1234567890",
    email: "jane@example.com"
  }) {
    id
    name
    phoneNumber
    agent {
      id
      name
    }
  }
}
```
