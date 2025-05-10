package com.example.api_1.pojo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyerBody {
    @JsonProperty("bName")
    private String bName;

    @JsonProperty("bLastname")
    private String bLastname;

    @JsonProperty("uPassword")
    private String uPassword;

    @JsonProperty("uPhoneEmail")
    private String uPhoneEmail;

    @JsonProperty("uBoolPhone")
    private boolean uBoolPhone;
}
