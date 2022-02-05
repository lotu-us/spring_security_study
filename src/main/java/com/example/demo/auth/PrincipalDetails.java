package com.example.demo.auth;

//시큐리티가 /login을 낚아채서 로그인을 진행시킨다
// 로그인이 진행이 완료되면 session에 저장 (시큐리티가 가진 자신만의 session공간(Security ContextHolder)에 저장한다.)
// 이 session공간에 저장하려면 객체는 반드시 Authentication 타입이어야한다.
// Authentication 안에 User정보가 있어야한다
// 이 저장될 User 타입도 UserDetails 타입 객체이어야 한다.  (spring이 자동으로 만들어준다)

import com.example.demo.domain.User;
import com.example.demo.oauth.provider.OAuth2UserInfo;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// DB인증정보를 전달해준다
// UserDetails : 사용자의 정보를 담는 인터페이스. UserDetailsService의 반환타입이다
@Getter //controller의 logininfo 확인
@ToString
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user;
    //private Map<String, Object> attributes;
    private OAuth2UserInfo oAuth2UserInfo;

    //UserDetails : 일반 로그인 시 사용
    public PrincipalDetails(User user) {
        this.user = user;
    }

    //OAuth2User : OAuth2 로그인 시 사용
//    public PrincipalDetails(User user, Map<String, Object> attributes) {
//        //PrincipalOauth2UserService의 loadUser에서 OAuth2를 바탕으로 user를 생성할 것임
//        this.user = user;
//        this.attributes = attributes;
//    }


    public PrincipalDetails(User user, OAuth2UserInfo oAuth2UserInfo) {
        this.user = user;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    /**
     * UserDetails 구현
     * 해당 유저의 권한목록 리턴
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole().toString();
            }
        });

//        jwt
//        user.getRoleList().forEach(role -> {
//            collect.add(() -> role);
//        });
        return collect;
    }


    /**
     * UserDetails 구현
     * 비밀번호를 리턴
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }


    /**
     * UserDetails 구현
     * PK값을 반환해준다
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }


    /**
     * UserDetails 구현
     * 계정 만료 여부
     *  true : 만료안됨
     *  false : 만료됨
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    /**
     * UserDetails 구현
     * 계정 잠김 여부
     *  true : 잠기지 않음
     *  false : 잠김
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    /**
     * UserDetails 구현
     * 계정 비밀번호 만료 여부
     *  true : 만료 안됨
     *  false : 만료됨
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    /**
     * UserDetails 구현
     * 계정 활성화 여부
     *  true : 활성화됨
     *  false : 활성화 안됨
     */
    @Override
    public boolean isEnabled() {
        return true;
    }







    /**
     * OAuth2User 구현
     * @return
     */
    @Override
    public Map<String, Object> getAttributes() {
        //return attributes;
        return oAuth2UserInfo.getAttributes();
    }


    /**
     * OAuth2User 구현
     * @return
     */
    @Override
    public String getName() {
        //String sub = attributes.get("sub").toString();
        String providerId = oAuth2UserInfo.getProviderId();
        return providerId;
    }


}
