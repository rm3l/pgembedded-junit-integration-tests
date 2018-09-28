package org.rm3l.pgembeddedjunitintegrationtests.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "app_setting",
        uniqueConstraints =
            @UniqueConstraint(columnNames = {"setting_key", "setting_value"}))
public class AppSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic(optional = false)
    @Column(name = "setting_key")
    private String key;

    @Basic
    @Column(name = "setting_value")
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppSetting that = (AppSetting) o;
        return getKey().equals(that.getKey()) &&
                Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getValue());
    }

    public static class Builder {

        private final AppSetting entity = new AppSetting();

        public Builder withKey(String key) {
            this.entity.setKey(key);
            return this;
        }

        public Builder withValue(String value) {
            this.entity.setValue(value);
            return this;
        }

        public AppSetting build() {
            return this.entity;
        }

    }
}
