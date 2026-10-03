package com.examly.springapp.service;

import com.examly.springapp.dto.FolderRequest;
import com.examly.springapp.dto.FolderResponse;
import com.examly.springapp.exception.FolderNotFoundException;
import com.examly.springapp.exception.UnauthorizedAccessException;
import com.examly.springapp.exception.UserNotFoundException;
import com.examly.springapp.model.Folder;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.FolderRepository;
import com.examly.springapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    public FolderServiceImpl(FolderRepository folderRepository, UserRepository userRepository) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public FolderResponse createFolder(String userEmail, FolderRequest request) {
        User user = getUser(userEmail);

        Folder parentFolder = null;
        if (request.getParentFolderId() != null) {
            parentFolder = folderRepository.findById(request.getParentFolderId())
                    .orElseThrow(() -> new FolderNotFoundException("Parent folder with id " + request.getParentFolderId() + " not found"));

            if (!parentFolder.getOwner().getId().equals(user.getId())) {
                throw new UnauthorizedAccessException("Cannot create subfolder inside another user's folder");
            }
        }

        Folder folder = Folder.builder()
                .name(request.getName().trim())
                .owner(user)
                .parentFolder(parentFolder)
                .createdAt(LocalDateTime.now())
                .build();

        Folder saved = folderRepository.save(folder);
        return mapToResponse(saved);
    }

    @Override
    public FolderResponse renameFolder(String userEmail, Long folderId, FolderRequest request) {
        User user = getUser(userEmail);
        Folder folder = getFolderEntity(folderId, user);

        folder.setName(request.getName().trim());

        if (request.getParentFolderId() != null) {
            if (request.getParentFolderId().equals(folderId)) {
                throw new IllegalArgumentException("A folder cannot be its own parent");
            }
            Folder newParent = folderRepository.findById(request.getParentFolderId())
                    .orElseThrow(() -> new FolderNotFoundException("Parent folder not found"));
            if (!newParent.getOwner().getId().equals(user.getId())) {
                throw new UnauthorizedAccessException("Cannot move folder into another user's folder");
            }
            folder.setParentFolder(newParent);
        }

        Folder updated = folderRepository.save(folder);
        return mapToResponse(updated);
    }

    @Override
    public void deleteFolder(String userEmail, Long folderId) {
        User user = getUser(userEmail);
        Folder folder = getFolderEntity(folderId, user);
        folderRepository.delete(folder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FolderResponse> getFolders(String userEmail) {
        User user = getUser(userEmail);
        return folderRepository.findByOwner(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FolderResponse> getChildFolders(String userEmail, Long parentFolderId) {
        User user = getUser(userEmail);
        return folderRepository.findByOwnerAndParentFolderId(user, parentFolderId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Folder getFolderEntity(Long folderId, User user) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException("Folder not found with id: " + folderId));

        if (!folder.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to access this folder");
        }

        return folder;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    private FolderResponse mapToResponse(Folder folder) {
        return FolderResponse.builder()
                .id(folder.getId())
                .name(folder.getName())
                .parentFolderId(folder.getParentFolder() != null ? folder.getParentFolder().getId() : null)
                .createdAt(folder.getCreatedAt())
                .build();
    }
}
