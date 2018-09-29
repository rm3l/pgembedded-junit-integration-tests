package org.rm3l.pgembeddedjunitintegrationtests.repositories;

import org.rm3l.pgembeddedjunitintegrationtests.domain.AppSetting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(
        collectionResourceRel = "AppSettings",
        itemResourceRel = "AppSetting",
        path = "AppSettings"
)
public interface AppSettingRepository extends JpaRepository<AppSetting, Long> {

    Page<AppSetting> findAllByKeyIs(@Param("key") String key, Pageable pageable);

    Optional<AppSetting> findByKeyAndValue(@Param("key") String key, @Param("value") String value);
}
