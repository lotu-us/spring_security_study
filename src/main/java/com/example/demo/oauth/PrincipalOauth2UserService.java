package com.example.demo.oauth;

import com.example.demo.auth.PrincipalDetails;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.oauth.provider.GoogleUserInfo;
import com.example.demo.oauth.provider.NaverUserInfo;
import com.example.demo.oauth.provider.OAuth2UserInfo;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 구글에서 받은 userRequest에 대한 후처리 함수
     * @param userRequest
     * @return
     * @throws OAuth2AuthenticationException
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        //System.out.println(userRequest.getClientRegistration());
        //ClientRegistration{registrationId='google', clientId='203207734125-uop3n8hg1tl7h3p0m3ai1mtknavo8fvs.apps.googleusercontent.com', clientSecret='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', clientAuthenticationMethod=org.springframework.security.oauth2.core.ClientAuthenticationMethod@4fcef9d3, authorizationGrantType=org.springframework.security.oauth2.core.AuthorizationGrantType@5da5e9f3, redirectUri='{baseUrl}/{action}/oauth2/code/{registrationId}', scopes=[profile, email], providerDetails=org.springframework.security.oauth2.client.registration.ClientRegistration$ProviderDetails@5b8ec6e5, clientName='Google'}

        //System.out.println(userRequest.getAccessToken().getTokenValue());
        //ya29.A0ARrdaM8M3tGJYSiyLuaE4fvy-iXqoPhRAneZ38L4tLa3giec7QrGEN7kwW6Rx2dfUl16LWrtIt3zOokBL6Y2ZaloPQeVC05AlkAeRGUyuaTfgosN7GqPSBfX6HOV3WVAoBFc73pPmxiyNyszRMSWozWn-PrN

        //System.out.println(super.loadUser(userRequest).getAttributes());
        //{sub=105856857277272444563(PK값 같은거), name=tw moon, given_name=tw, family_name=moon,
        // picture=https://lh3.googleusercontent.com/a/AATXAJxK9N1xXACzmw4TiuZuDK9EJocB8906XfOc98LN=s96-c,
        // email=msj980710@gmail.com, email_verified=true, locale=ko}

        // username = google_105856857277272444563
        // password = "암호화(겟인데어)"       //어차피 이 비밀번호로 로그인 할 것이 아니기때문에 나만 알고있으면 된다다
       // email = email

        // 구글 로그인 완료 -> code 리턴(OAuth-Client 라이브러리) -> AccessToken요청(userRequest정보)
        // loadUser함수 : 구글로부터 userRequest정보를 받아준다



        //Oauth객체로 강제 회원가입 처리
        //google만 처리할 경우
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//        String provider = userRequest.getClientRegistration().getRegistrationId();    //google
//        String providerId = oAuth2User.getAttribute("sub");
//        //google의 경우엔 sub이지만, facebook의 경우는 id로 명명되어있다.
//        //따라서 OAuth2UserInfo 인터페이스를 참고하자
//        String username = provider+"_"+providerId;  // 사용자가 입력한 적은 없지만 일단 만들어준다
//
//        String uuid = UUID.randomUUID().toString().substring(0, 6);
//        String password = bCryptPasswordEncoder.encode("패스워드"+uuid);  // 사용자가 입력한 적은 없지만 일단 만들어준다
//
//        String email = oAuth2User.getAttribute("email");
//        Role role = Role.ROLE_USER;



        //OAuth2UserInfo를 만든 후
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = null;
        String provider = userRequest.getClientRegistration().getRegistrationId();

        //System.out.println(oAuth2User.getAttributes());

        if(provider.equals("google")){
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }
        else if(provider.equals("naver")){
            oAuth2UserInfo = new NaverUserInfo(oAuth2User.getAttributes());
        }

        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider+"_"+providerId;

        String uuid = UUID.randomUUID().toString().substring(0, 6);
        String password = bCryptPasswordEncoder.encode("패스워드"+uuid);  // 사용자가 입력한 적은 없지만 일단 만들어준다

        String email = oAuth2UserInfo.getEmail();
        Role role = Role.ROLE_USER;


        User byUsername = userRepository.findByUsername(username);
        if(byUsername == null){
            byUsername = User.oauth2Register()
                    .username(username).password(password).email(email).role(role)
                    .provider(provider).providerId(providerId)
                    .build();
            userRepository.save(byUsername);
        }

        //return new PrincipalDetails(byUsername, oAuth2User.getAttributes());
        return new PrincipalDetails(byUsername, oAuth2UserInfo);
    }
}
