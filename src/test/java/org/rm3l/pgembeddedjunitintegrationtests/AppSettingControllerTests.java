package org.rm3l.pgembeddedjunitintegrationtests;

import org.junit.Test;
import org.rm3l.pgembeddedjunitintegrationtests.domain.AppSetting;
import org.rm3l.pgembeddedjunitintegrationtests.domain.Book;
import org.rm3l.pgembeddedjunitintegrationtests.domain.Person;
import org.rm3l.pgembeddedjunitintegrationtests.repositories.AppSettingRepository;
import org.rm3l.pgembeddedjunitintegrationtests.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.UUID;

public class AppSettingControllerTests extends AbstractIT {

    @Autowired
    private AppSettingRepository appSettingRepository;

    private void injectTestData() {
        final AppSetting appSetting = new AppSetting.Builder()
                .withKey("key1").withValue("value 1").build();
        final AppSetting appSetting2 = new AppSetting.Builder()
                .withKey("another.property.key").withValue("some thing").build();

        this.appSettingRepository.saveAll(Arrays.asList(appSetting, appSetting2));
    }

    @Test
    public void testGetMethod() {
        this.injectTestData();
    }
}
