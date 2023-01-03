package com.codeoftheweb.salvo.repository;

import com.codeoftheweb.salvo.model.entity.Ship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ShipRepository extends JpaRepository<Ship, Long> {
}
