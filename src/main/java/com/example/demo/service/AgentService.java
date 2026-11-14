package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Agent;
import com.example.demo.repository.AgentRepository;

@Service
public class AgentService {

    @Autowired
    private AgentRepository agentRepository;

    public Agent saveAgent(Agent agent) {
        return agentRepository.save(agent);
    }

    public List<Agent> getAllAgents() {
        return agentRepository.findAll();
    }

    public Agent getAgentById(Long id) {
        return agentRepository.findById(id).orElse(null);
    }

    public Agent updateAgent(Long id, Agent agent) {
        Agent existing = getAgentById(id);

        if (existing == null) {
            return null;
        }

        existing.setName(agent.getName());
        existing.setEmail(agent.getEmail());
        existing.setAgentType(agent.getAgentType());
        existing.setDescription(agent.getDescription());

        return agentRepository.save(existing);
    }

    public void deleteAgent(Long id) {
        if (!agentRepository.existsById(id)) {
            throw new IllegalArgumentException("Agent not found");
        }
        agentRepository.deleteById(id);
    }
}