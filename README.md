## springBoot-security study


### 스프링 시큐리티가 필요한 이유?

1. 인증

사이트에 접근하는 사람이 누구인지 -> 특정 리소스에 접근하거나 개인화된 사용성을 보장 받기 위해서는 로그인 과정이 필요하다
- UsernamePassword 인증
   - Session 관리
   - 토큰관리(sessionless)
- Sns 로그인 : 인증 위임


2. 인가

사용자가 누구인지 알았다면 사이트 관리자 혹은 시스템은 로그인한 사용자가 어떤 일을 할 수 있는지 권한을 설정해야 한다. -> 페이지 접근, 특정 리소스 접근
이것을 쉽게 작성할 수 있도록 프레임워크를 제공하는 것이 Spring security framework 이다.

- Secured : deprecated
- PrePostAuthorize
- AOP


