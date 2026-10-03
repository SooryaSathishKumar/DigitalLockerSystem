package com.examly.springapp.service;

import com.examly.springapp.dto.FolderRequest;
import com.examly.springapp.dto.FolderResponse;
import com.examly.springapp.model.Folder;
import com.examly.springapp.model.User;

import java.util.List;

public interface FolderService {

    FolderResponse createFolder(String userEmail, FolderRequest request);

    FolderResponse renameFolder(String userEmail, Long folderId, FolderRequest request);

    void deleteFolder(String userEmail, Long folderId);

    List<FolderResponse> getFolders(String userEmail);

    List<FolderResponse> getChildFolders(String userEmail, Long parentFolderId);

    Folder getFolderEntity(Long folderId, User user);
}
