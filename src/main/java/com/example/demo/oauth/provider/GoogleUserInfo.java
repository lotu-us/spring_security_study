package com.example.demo.oauth.provider;

import lombok.ToString;

import java.util.Map;

@ToString
public class GoogleUserInfo implements OAuth2UserInfo{

    private Map<String, Object> attributes; //OAuth2User.getAttributes();
    //System.out.println(super.loadUser(userRequest).getAttributes());
    //{sub=105856857277272444563(PK값 같은거), name=tw moon, given_name=tw, family_name=moon,
    // picture=https://lh3.googleusercontent.com/a/AATXAJxK9N1xXACzmw4TiuZuDK9EJocB8906XfOc98LN=s96-c,
    // email=msj980710@gmail.com, email_verified=true, locale=ko}

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("sub").toString();
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }


}
