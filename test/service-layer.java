package com.example.referral.service;

import com.example.referral.model.Agent;
import com.example.referral.model.AgentReferral;
import com.example.referral.model.User;
import com.example.referral.repository.AgentRepository;
import com.example.referral.repository.AgentReferralRepository;
import com.example.referral.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AgentService {

    private final AgentRepository agentRepository;

    @Autowired
    public AgentService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public Agent createAgent(Agent agent) {
        if (agent.getId() == null) {
            agent.setId(UUID.randomUUID().toString());
        }
        
        LocalDateTime now = LocalDateTime.now();
        agent.setCreatedAt(now);
        agent.setUpdatedAt(now);
        
        return agentRepository.save(agent);
    }

    public Optional<Agent> getAgentById(String id) {
        return agentRepository.findById(id);
    }

    public List<Agent> getAllAgents() {
        return agentRepository.findAll();
    }

    public Agent updateAgent(Agent agent) {
        agent.setUpdatedAt(LocalDateTime.now());
        return agentRepository.save(agent);
    }

    public void deleteAgent(String id) {
        agentRepository.findById(id).ifPresent(agentRepository::delete);
    }
}

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AgentReferralRepository agentReferralRepository;

    @Autowired
    public UserService(
            UserRepository userRepository,
            AgentReferralRepository agentReferralRepository) {
        this.userRepository = userRepository;
        this.agentReferralRepository = agentReferralRepository;
    }

    public User createUser(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID().toString());
        }
        
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        
        // Check if phone number is associated with an agent
        if (user.getPhoneNumber() != null) {
            Optional<AgentReferral> agentReferral = agentReferralRepository.findByPhoneNumber(user.getPhoneNumber());
            agentReferral.ifPresent(referral -> user.setAgentId(referral.getAgentId()));
        }
        
        return userRepository.save(user);
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        userRepository.findById(id).ifPresent(userRepository::delete);
    }
}

@Service
public class ReferralService {

    private final AgentReferralRepository agentReferralRepository;
    private final AgentRepository agentRepository;

    @Autowired
    public ReferralService(
            AgentReferralRepository agentReferralRepository,
            AgentRepository agentRepository) {
        this.agentReferralRepository = agentReferralRepository;
        this.agentRepository = agentRepository;
    }

    public AgentReferral createReferral(String agentId, String phoneNumber) {
        // Validate agent exists
        if (!agentRepository.findById(agentId).isPresent()) {
            throw new IllegalArgumentException("Agent not found with ID: " + agentId);
        }
        
        AgentReferral referral = AgentReferral.builder()
                .phoneNumber(phoneNumber)
                .agentId(agentId)
                .referralDate(LocalDateTime.now())
                .build();
        
        return agentReferralRepository.save(referral);
    }

    public Optional<AgentReferral> getReferralByPhoneNumber(String phoneNumber) {
        return agentReferralRepository.findByPhoneNumber(phoneNumber);
    }

    public List<AgentReferral> getReferralsByAgentId(String agentId) {
        return agentReferralRepository.findByAgentId(agentId);
    }
    
    public Optional<AgentReferral> getLatestReferralByAgentId(String agentId) {
        return agentReferralRepository.findLatestReferralByAgentId(agentId);
    }

    public List<AgentReferral> getAllReferrals() {
        return agentReferralRepository.findAll();
    }

    public void deleteReferral(String phoneNumber) {
        agentReferralRepository.findByPhoneNumber(phoneNumber)
                .ifPresent(agentReferralRepository::delete);
    }
}
