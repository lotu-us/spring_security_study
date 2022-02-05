package com.example.demo.oauth.provider;

import lombok.ToString;

import java.util.Map;

@ToString
public class NaverUserInfo implements OAuth2UserInfo{

    private Map<String, Object> attributes; //OAuth2User.getAttributes();
    private Map<String, Object> attributesResponse;
    //System.out.println(oAuth2User.getAttributes());
    //{resultcode=00,
    // message=success,
    // response={id=aUMPK_QqMhQ1IS_wQp82LiygDjtlKno-08tYFxVo3MU, email=lotu_us@naver.com, name=문소정}}

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.attributesResponse = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getProviderId() {
        return attributesResponse.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getEmail() {
        return attributesResponse.get("email").toString();
    }

    @Override
    public String getName() {
        return attributesResponse.get("name").toString();
    }
}
