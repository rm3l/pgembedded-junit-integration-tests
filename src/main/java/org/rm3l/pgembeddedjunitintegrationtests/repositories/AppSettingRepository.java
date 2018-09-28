package org.rm3l.pgembeddedjunitintegrationtests.repositories;

import org.rm3l.pgembeddedjunitintegrationtests.domain.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface AppSettingRepository extends JpaRepository<AppSetting, Long> {
}
