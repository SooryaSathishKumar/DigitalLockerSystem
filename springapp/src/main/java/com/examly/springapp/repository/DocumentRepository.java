package com.examly.springapp.repository;

import com.examly.springapp.model.Document;
import com.examly.springapp.model.Folder;
import com.examly.springapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

    List<Document> findByOwnerAndIsArchived(User owner, Boolean isArchived);

    List<Document> findByOwnerAndIsArchivedFalse(User owner);

    List<Document> findByOwnerAndIsArchivedTrue(User owner);

    Optional<Document> findByIdAndOwner(Long id, User owner);

    List<Document> findByParentFolderAndIsArchivedFalse(Folder parentFolder);

    List<Document> findByOwnerAndParentFolderIsNullAndIsArchivedFalse(User owner);

    List<Document> findByOwnerAndParentFolderIdAndIsArchivedFalse(User owner, Long parentFolderId);
}
