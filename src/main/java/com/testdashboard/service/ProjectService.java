package com.testdashboard.service;

import com.testdashboard.dto.request.ProjectRequest;
import com.testdashboard.entity.Project;
import com.testdashboard.exception.BadRequestException;
import com.testdashboard.exception.ResourceNotFoundException;
import com.testdashboard.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findByIsActiveTrue();
    }

    public Project getById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    public Project getByName(String name) {
        return projectRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project not found: " + name));
    }

    // Finds project by name or creates it if it doesn't exist
    // This way automation frameworks don't need to pre-register projects
    @Transactional
    public Project findOrCreate(String name) {
        return projectRepository.findByName(name)
                .orElseGet(() -> projectRepository.save(
                        Project.builder().name(name).isActive(true).build()));
    }

    @Transactional
    public Project create(ProjectRequest request) {
        if (projectRepository.existsByName(request.getName())) {
            throw new BadRequestException(
                    "Project already exists: " + request.getName());
        }
        return projectRepository.save(Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(request.getOwner())
                .isActive(true)
                .build());
    }

    @Transactional
    public Project update(Long id, ProjectRequest request) {
        Project project = getById(id);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setOwner(request.getOwner());
        return projectRepository.save(project);
    }

    @Transactional
    public void delete(Long id) {
        Project project = getById(id);
        project.setIsActive(false);
        projectRepository.save(project);
    }
}