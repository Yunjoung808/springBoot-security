## 스프링 시큐리티 공부

- 2022.06.27 ~

## 스프링 시큐리티가 필요한 이유?

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


## 스프링 시큐리티의 큰 그림

- 서블릿 컨테이너 : 톰캣과 같은 웹 애플리케이션을 서블릿 컨테이너라고 부르는데, 이런 웹 애플리케이션은 기본적으로 필터와 서블릿으로 구성되어 있다.

1. request가 threadlocal로 실행되어 들어온다.
2. 필터를 차례대로 거친다음
3. url에 따라 서블릿이 분기되고
4. 실행될 메소드를 찾아 request, response를 넘긴다

- 필터는 체인처럼 엮여있기 때문에 필터 체인이라고도 불리는데, 모든 request는 이 필터 체인을 반드시 거쳐야만 서블릿 서비스에 도착하게 되어있다.

- 그래서 스프링 시큐리티는 필터들 중간에 DelegatingFilterProxy라는 필터를 만들어 메인 필터체인에 끼워넣고, 그 아래 다시 SecurityFilterChain 그룹을 등록한다.
- 이 필터체인은 반드시 한개 이상이고, url 패턴에 따라 적용되는 필터체인을 다르게 할 수 있다. => WebSecurityConfigurerAdapter가 그 역할


그렇다면 '이 시큐리티 필터 체인에 어떤 필터들을 넣을 수 있느냐'가 스프링 시큐리티의 가장 큰 부분이다.

각각의 필터는 대부분 한가지 일만 한다. 예를들면
-  HeaderWriterFilter : Http 헤더를 검사 -> 써야 할 건 잘 써있는지? 필요한 헤더를 더해줘야 할 건 없는지?
- CorsFilter : 허가된 사이트나 클라이언트의 요청인지?
- CsrFilter : 내가 내보낸 리소스에서 올라온 요청인지?
- LogoutFilter : 지금 로그아웃하겠다고 하는건지?
- UsernamePasswordAutheticationFilter : usename / password로 로그인 하려고 하는가? 로그인이면 어떤 처리하고 어디로 가야 할지
- ConcurrentSessionFilter : 동시 로그인 허용하는지?
- BearerTokenAuthenticationFilter :  Authentication 헤더에 Bearer 토큰이 오면 인증 처리 해줄게
- BasicAuthenticationFilter : Authenrication 헤더에 Basic 토큰을 주면 검새하서 인증처리 할게
등등...

각각의 필터는 넣거나 뺄 수 있고, 순서를 조절할 수 있다. -> 이때 필터의 순서가 매우 크리티컬 할 수 있기 때문에 기본 필터들은 그 순서가 어느정도 정해져 있다





