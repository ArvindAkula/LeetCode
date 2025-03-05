package com.example.referral;

import com.example.referral.model.Agent;
import com.example.referral.model.AgentReferral;
import com.example.referral.model.User;
import com.example.referral.service.AgentService;
import com.example.referral.service.ReferralService;
import com.example.referral.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class ReferralIntegrationTest {

    @Container
    static GenericContainer<?> dynamoDbContainer = new GenericContainer<>(
            DockerImageName.parse("amazon/dynamodb-local:latest"))
            .withExposedPorts(8000);
    
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.dynamodb.endpoint", 
                () -> "http://" + dynamoDbContainer.getHost() + ":" + dynamoDbContainer.getMappedPort(8000));
    }
    
    @Autowired
    private AgentService agentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ReferralService referralService;
    
    @Test
    void testReferralAndUserCreation() {
        // Create an agent
        Agent agent = Agent.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();
        
        Agent savedAgent = agentService.createAgent(agent);
        assertNotNull(savedAgent.getId());
        
        // Create a referral
        String phoneNumber = "+1234567890";
        AgentReferral referral = referralService.createReferral(savedAgent.getId(), phoneNumber);
        
        assertEquals(phoneNumber, referral.getPhoneNumber());
        assertEquals(savedAgent.getId(), referral.getAgentId());
        
        // Verify referral retrieval
        Optional<AgentReferral> retrievedReferral = referralService.getReferralByPhoneNumber(phoneNumber);
        assertTrue(retrievedReferral.isPresent());
        
        // Create a user with the referred phone number
        User user = User.builder()
                .name("Jane Smith")
                .phoneNumber(phoneNumber)
                .email("jane@example.com")
                .build();
        
        User savedUser = userService.createUser(user);
        
        // Verify agent was assigned to user
        assertNotNull(savedUser.getAgentId());
        assertEquals(savedAgent.getId(), savedUser.getAgentId());
    }
    
    @Test
    void testUserCreationWithoutReferral() {
        // Create a user with a phone number that has no referral
        String phoneNumber = "+9876543210";
        User user = User.builder()
                .name("Bob Johnson")
                .phoneNumber(phoneNumber)
                .email("bob@example.com")
                .build();
        
        User savedUser = userService.createUser(user);
        
        // Verify no agent was assigned
        assertNull(savedUser.getAgentId());
    }
    
    @Test
    void testMultipleReferralsLatestAssigned() throws InterruptedException {
        // Create two agents
        Agent agent1 = agentService.createAgent(Agent.builder().name("Agent One").build());
        Agent agent2 = agentService.createAgent(Agent.builder().name("Agent Two").build());
        
        String phoneNumber = "+5555555555";
        
        // First referral from agent1
        referralService.createReferral(agent1.getId(), phoneNumber);
        
        // Wait to ensure different timestamps
        Thread.sleep(100);
        
        // Second referral from agent2 (later timestamp)
        referralService.createReferral(agent2.getId(), phoneNumber);
        
        // Create user with the referred phone number
        User user = User.builder()
                .name("Multiple Test")
                .phoneNumber(phoneNumber)
                .build();
        
        User savedUser = userService.createUser(user);
        
        // Verify latest agent (agent2) was assigned
        assertEquals(agent2.getId(), savedUser.getAgentId());
    }
}
