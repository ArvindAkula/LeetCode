package com.example.referral.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.referral.model.Agent;
import com.example.referral.model.AgentReferral;
import com.example.referral.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class AgentRepository {

    private final DynamoDBMapper dynamoDBMapper;

    @Autowired
    public AgentRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public Agent save(Agent agent) {
        dynamoDBMapper.save(agent);
        return agent;
    }

    public Optional<Agent> findById(String id) {
        return Optional.ofNullable(dynamoDBMapper.load(Agent.class, id));
    }

    public List<Agent> findAll() {
        return dynamoDBMapper.scan(Agent.class, new DynamoDBScanExpression());
    }

    public void delete(Agent agent) {
        dynamoDBMapper.delete(agent);
    }
}

@Repository
public class UserRepository {

    private final DynamoDBMapper dynamoDBMapper;

    @Autowired
    public UserRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public User save(User user) {
        dynamoDBMapper.save(user);
        return user;
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(dynamoDBMapper.load(User.class, id));
    }

    public Optional<User> findByPhoneNumber(String phoneNumber) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":phoneNumber", new AttributeValue().withS(phoneNumber));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("phoneNumber = :phoneNumber")
                .withExpressionAttributeValues(eav);

        List<User> users = dynamoDBMapper.scan(User.class, scanExpression);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public List<User> findAll() {
        return dynamoDBMapper.scan(User.class, new DynamoDBScanExpression());
    }

    public void delete(User user) {
        dynamoDBMapper.delete(user);
    }
}

@Repository
public class AgentReferralRepository {

    private final DynamoDBMapper dynamoDBMapper;

    @Autowired
    public AgentReferralRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public AgentReferral save(AgentReferral agentReferral) {
        dynamoDBMapper.save(agentReferral);
        return agentReferral;
    }

    public Optional<AgentReferral> findByPhoneNumber(String phoneNumber) {
        return Optional.ofNullable(dynamoDBMapper.load(AgentReferral.class, phoneNumber));
    }

    public List<AgentReferral> findByAgentId(String agentId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":agentId", new AttributeValue().withS(agentId));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("agentId = :agentId")
                .withExpressionAttributeValues(eav);

        return dynamoDBMapper.scan(AgentReferral.class, scanExpression);
    }

    public Optional<AgentReferral> findLatestReferralByAgentId(String agentId) {
        List<AgentReferral> referrals = findByAgentId(agentId);
        
        return referrals.stream()
                .max((r1, r2) -> r1.getReferralDate().compareTo(r2.getReferralDate()));
    }

    public List<AgentReferral> findAll() {
        return dynamoDBMapper.scan(AgentReferral.class, new DynamoDBScanExpression());
    }

    public void delete(AgentReferral agentReferral) {
        dynamoDBMapper.delete(agentReferral);
    }
}
