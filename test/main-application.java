package com.example.referral;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.example.referral.model.Agent;
import com.example.referral.model.AgentReferral;
import com.example.referral.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ReferralApplication {

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public static void main(String[] args) {
        SpringApplication.run(ReferralApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDynamoDB() {
        return args -> {
            // Create Agent table if not exists
            CreateTableRequest createAgentTableRequest = dynamoDBMapper.generateCreateTableRequest(Agent.class)
                    .withProvisionedThroughput(new ProvisionedThroughput(5L, 5L));
            TableUtils.createTableIfNotExists(amazonDynamoDB, createAgentTableRequest);

            // Create User table if not exists
            CreateTableRequest createUserTableRequest = dynamoDBMapper.generateCreateTableRequest(User.class)
                    .withProvisionedThroughput(new ProvisionedThroughput(5L, 5L));
            TableUtils.createTableIfNotExists(amazonDynamoDB, createUserTableRequest);

            // Create AgentReferral table if not exists
            CreateTableRequest createReferralTableRequest = dynamoDBMapper.generateCreateTableRequest(AgentReferral.class)
                    .withProvisionedThroughput(new ProvisionedThroughput(5L, 5L));
            TableUtils.createTableIfNotExists(amazonDynamoDB, createReferralTableRequest);
        };
    }
}
