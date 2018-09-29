package org.rm3l.pgembeddedjunitintegrationtests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.rm3l.pgembeddedjunitintegrationtests.domain.AppSetting;
import org.rm3l.pgembeddedjunitintegrationtests.domain.Book;
import org.rm3l.pgembeddedjunitintegrationtests.domain.Person;
import org.rm3l.pgembeddedjunitintegrationtests.repositories.AppSettingRepository;
import org.rm3l.pgembeddedjunitintegrationtests.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    public void testCreate() throws Exception {
        this.injectTestData();
        this.mvc.perform(post("/AppSettings")
                .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(new AppSetting.Builder()
        .withKey("new.key.created").withValue("value").build())))
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.key", is("new.key.created")))
                .andExpect(
                        jsonPath("$.value", is("value")));

        this.mvc.perform(get("/AppSettings"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$._embedded.AppSettings", hasSize(3)));
    }

    @Test
    public void testRead() throws Exception {
        this.injectTestData();
        this.mvc.perform(get("/AppSettings"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$._embedded.AppSettings", hasSize(2)));
    }

    @Test
    public void testUpdate_Full() throws Exception {
        this.injectTestData();

        this.mvc.perform(get("/AppSettings"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$._embedded.AppSettings", hasSize(2)));

        final Optional<AppSetting> appSettingOptional =
                this.appSettingRepository.findByKeyAndValue("another.property.key", "some thing");
        Assert.assertTrue(appSettingOptional.isPresent());

        final AppSetting appSetting = appSettingOptional.get();
        Assert.assertNotNull(appSetting.getId());

        this.mvc.perform(put("/AppSettings/" + appSetting.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new AppSetting.Builder()
                        .withKey("another.property.key.renamed")
                        .withValue(appSetting.getValue() + " updated").build())))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.key", is("another.property.key.renamed")))
                .andExpect(
                        jsonPath("$.value", is("some thing updated")));

        this.mvc.perform(get("/AppSettings"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$._embedded.AppSettings", hasSize(2)));

        Assert.assertFalse(this.appSettingRepository
                .findByKeyAndValue(appSetting.getKey(), appSetting.getValue())
                .isPresent());

        Assert.assertTrue(this.appSettingRepository
                .findByKeyAndValue("another.property.key.renamed",
                        "some thing updated")
                .isPresent());
    }

    @Test
    public void testUpdate_Patch() throws Exception {
        this.injectTestData();

        this.mvc.perform(get("/AppSettings"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$._embedded.AppSettings", hasSize(2)));

        final Optional<AppSetting> appSettingOptional =
                this.appSettingRepository.findByKeyAndValue("another.property.key", "some thing");
        Assert.assertTrue(appSettingOptional.isPresent());

        final AppSetting appSetting = appSettingOptional.get();
        Assert.assertNotNull(appSetting.getId());

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        this.mvc.perform(patch("/AppSettings/" + appSetting.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AppSetting.Builder()
                        .withKey("another.property.key.renamed").build())))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.key",
                                is("another.property.key.renamed")))
                .andExpect(
                        jsonPath("$.value", is("some thing")));

        this.mvc.perform(get("/AppSettings"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$._embedded.AppSettings", hasSize(2)));

        Assert.assertFalse(this.appSettingRepository
                .findByKeyAndValue(appSetting.getKey(), appSetting.getValue())
                .isPresent());

        Assert.assertTrue(this.appSettingRepository
                .findByKeyAndValue("another.property.key.renamed", appSetting.getValue())
                .isPresent());
    }

    @Test
    public void testDelete() throws Exception {
        this.injectTestData();

        this.mvc.perform(get("/AppSettings"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$._embedded.AppSettings", hasSize(2)));

        final Optional<AppSetting> appSettingOptional =
                this.appSettingRepository.findByKeyAndValue("another.property.key", "some thing");
        Assert.assertTrue(appSettingOptional.isPresent());

        final AppSetting appSetting = appSettingOptional.get();
        Assert.assertNotNull(appSetting.getId());
        this.mvc.perform(delete("/AppSettings/" + appSetting.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        this.mvc.perform(get("/AppSettings"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$._embedded.AppSettings", hasSize(1)))
                .andExpect(
                        jsonPath("$._embedded.AppSettings[0].key", is("key1")))
                .andExpect(
                        jsonPath("$._embedded.AppSettings[0].value", is("value 1")));
    }
}
