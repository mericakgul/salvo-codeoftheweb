package com.codeoftheweb.salvo.repository;

import com.codeoftheweb.salvo.model.entity.SalvoLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface SalvoLocationRepository extends JpaRepository<SalvoLocation, Long> {
}
