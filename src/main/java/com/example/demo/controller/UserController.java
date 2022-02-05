package com.example.demo.controller;

import com.example.demo.auth.PrincipalDetails;
import com.example.demo.auth.PrincipalDetailsService;
import com.example.demo.domain.Member;
import com.example.demo.domain.Role;
import com.example.demo.domain.Team;
import com.example.demo.domain.User;
import com.example.demo.repository.MemberJpaRepository;
import com.example.demo.repository.MemberMapperRepository;
import com.example.demo.repository.TeamJpaRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class UserController {
    //@Autowired private MemberMapperRepository memberRepository;
    @Autowired private MemberJpaRepository memberRepository;
    @Autowired private TeamJpaRepository teamRepository;

    @Autowired private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;


    @GetMapping("/")
    @ResponseBody
    public String index(){
        //return "redirect:/loginForm";
        return "index";
    }

    // SecurityConfig 생성 이전엔 시큐리티가 낚아챘는데
    // SecurityConfig 생숭 후에는 login이 그대로 나온다다
   @GetMapping("/loginForm")
    public String loginForm(){
        return "login";
    }

    @GetMapping("/form/loginInfo")
    @ResponseBody
    public String formLoginInfo(Authentication authentication, @AuthenticationPrincipal PrincipalDetails principalDetails){
        // 이 메서드 테스트 시 PrincipalDetails는 UserDetails만 구현했었다
        //public class PrincipalDetails implements UserDetail

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        User user = principal.getUser();
        System.out.println(user);

        User user1 = principalDetails.getUser();
        System.out.println(user1);
//        User(id=2, username=11, password=$2a$10$m/1Alpm180jjsBpYReeml.AzvGlx/Djg4Z9/JDZYz8TJF1qUKd1fW, email=11@11, role=ROLE_USER, createTime=2022-01-30 19:07:43.213, provider=null, providerId=null)
//        User(id=2, username=11, password=$2a$10$m/1Alpm180jjsBpYReeml.AzvGlx/Djg4Z9/JDZYz8TJF1qUKd1fW, email=11@11, role=ROLE_USER, createTime=2022-01-30 19:07:43.213, provider=null, providerId=null)
        //user == user1

        // !!!! OAuth로 로그인 시 이 방식대로 하면 CastException 발생함
        return user.toString();     //세션에 담긴 user가져올 수 있음음
   }

    @GetMapping("/oauth/loginInfo")
    @ResponseBody
    public String oauthLoginInfo(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2UserPrincipal){
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println(attributes);
        // PrincipalOauth2UserService의 getAttributes내용과 같음

        Map<String, Object> attributes1 = oAuth2UserPrincipal.getAttributes();
        // attributes == attributes1

        // Oauth2로 로그인 할때는 (OAuth2User)로 캐스팅해야함함
       return attributes.toString();     //세션에 담긴 user가져올 수 있음음
    }

    /**
     * userDetailLoginInfo
     * oauthLoginInfo
     *
     * 스프링 시큐리티는 자체 시큐리티 세션 영역이 있다 = SecurityContextHolder
     * 시큐리티 세션에 들어갈 수 있는 타입은 Authentication만 가능하다.
     * Authentication은 UserDetail타입과 OAuth2User타입이 들어갈 수 있다.
     *
     * 일반 회원가입으로 로그인한 유저를 가져오는 방법과 (UserDetail)
     * OAuth2 방법으로 로그인한 유저를 가져오는 방법이 다르다! (OAuth2User)
     *
     * 한번에 받아오는 방법은? PrincipalDetails가 UserDetail, OAuth2User를 둘다 구현하는 것
     */

    @GetMapping("/loginInfo")
    @ResponseBody
    public String loginInfo(Authentication authentication, @AuthenticationPrincipal PrincipalDetails principalDetails){
        String result = "";

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        if(principal.getUser().getProvider() == null) {
            result = result + "Form 로그인 : " + principal;
        }else{
            result = result + "OAuth2 로그인 : " + principal;
        }

        return result;     //세션에 담긴 user가져올 수 있음음
    }





    @GetMapping("/joinForm")
    public String joinForm(){
        return "join";
    }

    @PostMapping("/join")
    public String join(@ModelAttribute User user){
        user.setRole(Role.ROLE_USER);

        String encodePwd = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodePwd);

        userRepository.save(user);  //반드시 패스워드 암호화해야함
        return "redirect:/loginForm";
    }

    @GetMapping("/user")
    @ResponseBody
    public String user(){
        return "user";
    }

    @GetMapping("/manager")
    @ResponseBody
    public String manager(){
        return "manager";
    }

    @GetMapping("/admin")
    @ResponseBody
    public String admin(){
        return "admin";
    }


    @Secured("ROLE_ADMIN")  //특정 메서드에 권한을 부여할 때 사용
    @GetMapping("/info")
    @ResponseBody
    public String info(){
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")  //data()가 실행되기 전에 실행됨. 권한 2개 걸고싶을 때 사용
    @GetMapping("/data")
    @ResponseBody
    public String data(){
        return "데이터";
    }



    @GetMapping("/member")
    @ResponseBody
    public ResponseEntity memberTest(){

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        Member member1 = Member.builder().name("Lisa").age(20).team(teamA).build();
        Member member2 = Member.builder().name("Cardi").age(25).team(teamA).build();
        Member member3 = Member.builder().name("Jane").age(30).team(teamB).build();


        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        teamA.getMembers().add(member1);
        teamA.getMembers().add(member2);
        teamB.getMembers().add(member3);


//        Team findTeamA = teamRepository.findByName("teamA").orElse(null);
//        System.out.println(findTeamA);
//
//        List<Member> members = findTeamA.getMembers();
//        System.out.println(members);


        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }
}

