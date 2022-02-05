package com.example.demo.auth;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 시큐리티 설정에 loginProcessingUrl("/login")을 걸어둔 상태
// /login요청이 오면 자동으로 loadUserByUsername이 실행된다
// DB에서 인증정보를 회득한다

// UserDetailsService : 유저의 정보를 가져오는 인터페이스
// 유저의 정보를 가져오기 위해 구현해야한다. 정보를 불러온 후 UserDetails로 리턴한다
@Service
public class PrincipalDetailsService implements UserDetailsService {
    @Autowired private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //파라미터의 username은 login.html의 form의 input name과 같아야함

        User byUsername = userRepository.findByUsername(username);
        if(byUsername != null){
            return new PrincipalDetails(byUsername);
        }
        return null;
    }
}
