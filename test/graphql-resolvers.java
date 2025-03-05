package com.example.referral.resolver;

import com.example.referral.model.Agent;
import com.example.referral.model.AgentReferral;
import com.example.referral.model.User;
import com.example.referral.service.AgentService;
import com.example.referral.service.ReferralService;
import com.example.referral.service.UserService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class Query implements GraphQLQueryResolver {

    private final AgentService agentService;
    private final UserService userService;
    private final ReferralService referralService;

    @Autowired
    public Query(
            AgentService agentService,
            UserService userService,
            ReferralService referralService) {
        this.agentService = agentService;
        this.userService = userService;
        this.referralService = referralService;
    }

    // Agent queries
    public Optional<Agent> agent(String id) {
        return agentService.getAgentById(id);
    }

    public List<Agent> agents() {
        return agentService.getAllAgents();
    }

    // User queries
    public Optional<User> user(String id) {
        return userService.getUserById(id);
    }
    
    public Optional<User> userByPhoneNumber(String phoneNumber) {
        return userService.getUserByPhoneNumber(phoneNumber);
    }

    public List<User> users() {
        return userService.getAllUsers();
    }

    // Referral queries
    public Optional<AgentReferral> referral(String phoneNumber) {
        return referralService.getReferralByPhoneNumber(phoneNumber);
    }

    public List<AgentReferral> referralsByAgentId(String agentId) {
        return referralService.getReferralsByAgentId(agentId);
    }
    
    public Optional<AgentReferral> latestReferralByAgentId(String agentId) {
        return referralService.getLatestReferralByAgentId(agentId);
    }

    public List<AgentReferral> referrals() {
        return referralService.getAllReferrals();
    }
}

@Component
public class Mutation implements GraphQLMutationResolver {

    private final AgentService agentService;
    private final UserService userService;
    private final ReferralService referralService;

    @Autowired
    public Mutation(
            AgentService agentService,
            UserService userService,
            ReferralService referralService) {
        this.agentService = agentService;
        this.userService = userService;
        this.referralService = referralService;
    }

    // Agent mutations
    public Agent createAgent(AgentInput input) {
        Agent agent = Agent.builder()
                .name(input.getName())
                .email(input.getEmail())
                .build();
        return agentService.createAgent(agent);
    }

    public Agent updateAgent(String id, AgentInput input) {
        return agentService.getAgentById(id)
                .map(agent -> {
                    agent.setName(input.getName());
                    agent.setEmail(input.getEmail());
                    return agentService.updateAgent(agent);
                })
                .orElseThrow(() -> new IllegalArgumentException("Agent not found with ID: " + id));
    }

    public boolean deleteAgent(String id) {
        agentService.deleteAgent(id);
        return true;
    }

    // User mutations
    public User createUser(UserInput input) {
        User user = User.builder()
                .name(input.getName())
                .phoneNumber(input.getPhoneNumber())
                .email(input.getEmail())
                .build();
        return userService.createUser(user);
    }

    public User updateUser(String id, UserInput input) {
        return userService.getUserById(id)
                .map(user -> {
                    user.setName(input.getName());
                    user.setPhoneNumber(input.getPhoneNumber());
                    user.setEmail(input.getEmail());
                    return userService.updateUser(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    public boolean deleteUser(String id) {
        userService.deleteUser(id);
        return true;
    }

    // Referral mutations
    public AgentReferral createReferral(String agentId, String phoneNumber) {
        return referralService.createReferral(agentId, phoneNumber);
    }

    public boolean deleteReferral(String phoneNumber) {
        referralService.deleteReferral(phoneNumber);
        return true;
    }
}

// Input types for mutations
class AgentInput {
    private String name;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

class UserInput {
    private String name;
    private String phoneNumber;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

// Field resolvers for data fetching
@Component
public class UserResolver implements GraphQLResolver<User> {
    
    private final AgentService agentService;
    
    @Autowired
    public UserResolver(AgentService agentService) {
        this.agentService = agentService;
    }
    
    public Agent agent(User user) {
        if (user.getAgentId() == null) {
            return null;
        }
        return agentService.getAgentById(user.getAgentId()).orElse(null);
    }
}

@Component
public class AgentResolver implements GraphQLResolver<Agent> {
    
    private final ReferralService referralService;
    
    @Autowired
    public AgentResolver(ReferralService referralService) {
        this.referralService = referralService;
    }
    
    public List<AgentReferral> referrals(Agent agent) {
        return referralService.getReferralsByAgentId(agent.getId());
    }
}

@Component
public class AgentReferralResolver implements GraphQLResolver<AgentReferral> {
    
    private final AgentService agentService;
    
    @Autowired
    public AgentReferralResolver(AgentService agentService) {
        this.agentService = agentService;
    }
    
    public Agent agent(AgentReferral referral) {
        return agentService.getAgentById(referral.getAgentId()).orElse(null);
    }
}
