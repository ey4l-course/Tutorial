package com.reminder.Transactions.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CatChangeDto {
    Long category;
    @JsonProperty("isPermanent")
    Boolean isPermanent;

    public CatChangeDto() {
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }

    public Boolean getPermanent() {
        return isPermanent;
    }

    public void setPermanent(Boolean permanent) {
        isPermanent = permanent;
    }
}
