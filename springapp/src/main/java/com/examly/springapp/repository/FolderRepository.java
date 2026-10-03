package com.examly.springapp.repository;

import com.examly.springapp.model.Folder;
import com.examly.springapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    List<Folder> findByOwner(User owner);

    List<Folder> findByOwnerAndParentFolderIsNull(User owner);

    List<Folder> findByOwnerAndParentFolderId(User owner, Long parentFolderId);

    Optional<Folder> findByIdAndOwner(Long id, User owner);
}
