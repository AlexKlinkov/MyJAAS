package ru.inner.project.MyJAAS.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.inner.project.MyJAAS.entities.InitialUrl;

@Repository
public interface InitialUrlRepository extends JpaRepository<InitialUrl, String> {

}
