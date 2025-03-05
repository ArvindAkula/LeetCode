package com.example.referral.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "Agent")
public class Agent {
    
    @DynamoDBHashKey
    private String id;
    
    @DynamoDBAttribute
    private String name;
    
    @DynamoDBAttribute
    private String email;
    
    @DynamoDBAttribute
    private LocalDateTime createdAt;
    
    @DynamoDBAttribute
    private LocalDateTime updatedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "User")
public class User {
    
    @DynamoDBHashKey
    private String id;
    
    @DynamoDBAttribute
    private String name;
    
    @DynamoDBAttribute
    private String phoneNumber;
    
    @DynamoDBAttribute
    private String email;
    
    @DynamoDBAttribute
    private String agentId;
    
    @DynamoDBAttribute
    private LocalDateTime createdAt;
    
    @DynamoDBAttribute
    private LocalDateTime updatedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "AgentReferral")
public class AgentReferral {
    
    @DynamoDBHashKey
    private String phoneNumber;
    
    @DynamoDBAttribute
    private String agentId;
    
    @DynamoDBAttribute
    private LocalDateTime referralDate;
}
