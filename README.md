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



## Authentication의 기본 구조

- 스프링 프레임워크에서 로그인을 한다는 것은 authenticated가 true인 Authentication 객체를 SecurityContext에 갖고 있는 상태를 말한다.
단, Authentication이 AnonymouseAuthenticationToken만 아니면 된다.

AuthenticationManager, ProviderManager, AuthenticationProvider

-AuthenticationManager (interface)

 : Authentication 객체를 받아 인증하고 인증되었다면 인증된 Authentication 객체를 돌려주는 메서드를 구현하도록 하는 인터페이스다.

이 메서드를 통해 인증되면 isAuthenticated(boolean)값을 TRUE로 바꿔준다.

 
-ProvicderManager (class)

: AuthenticationManager의 구현체로 스프링에서 인증을 담당하는 클래스로 볼 수 있다.

(스프링 시큐리티가 생성하고 등록하고 관리하는 스프링 빈이므로  직접 구현할 필요X)


-AuthenticationProvider

:  인증과정이 이 메서드를 통해서 진행된다.


supports(Class<?>):boolean :  앞에서 보내준 Authentication 객체를 이 AuthenticationProvider가 인증 가능한 클래스인지 확인한다.


UsernamePasswordAuthenticationToken이 ProviderManager에 도착한다면  ProviderManager는 자기가 갖고 있는 AuthenticationProvider 목록을 순회하면서 '너가 이거 해결해줄 수 있어?' 하고 물어보고(supports()) 해결 가능하다고 TRUE를 리턴해주는 AuthenticationProvider에게 authenticate() 메서드를 실행한다.


[ 인증토큰을 제공하는 필터들 ]
- UsernamePasswordAuthenticationFilter : 폼 로그인
- RememberMeAuthenticationFilter : remember-me 쿠키 로그인
- AnonymouseAuthentivationFilter : 로그인하지 않았다는 것을 인증함
- SecurityContextPersistenceFilter : 기존 로그인을 유지함 -> 기본적으로 session을 이용함
- BearerTokenAuthenticationFilter : JWT 로그인
- BasicAuthenticationFilter : ajax로그인
- OAuth2LoginAuthenticationFilter : 소셜 로그인
- OpenIDAuthenticationFilter : OpenID 로그인
등등..

- Authentication을 제공하는 인증 제공자는 여러개가 동시에 존재할 수 있고 인증 방식에 따라 ProviderManager도 복수로 존재할 수 있다.
- Authentication은 인터페이스로 아래와 같은 정보들을 갖고 있다.

1. Set<GrantedAuthority> authorities : 인증된 권한 정보
2. principal : 인증 대상에 관한 정보. 주로 UserDetails 객체
3. credentials : 인증 확인을 위한 정보, 주로 비밀번호가 오지만 인증후에는 보안을 위해 삭제한다.
4. details : 그 밖에 필요한 정보. IP, 세션정보, 기타 인증 요청에서 사용했던 정보들
5. boolean authenticated : 인증되었는가? 를 확인해줌


## Form login

[DefaultLoginPageGeneratingFilter]
- GET /login 을 처리한다.
- 별도의 로그인 페이지 설정을 하지 않으면 제공되는 필터
- 기본 로그인 폼을 제공한다.
- OAuth2 / OpenID / Saml2 로그인과도 같이 사용할 수 있다.

[UsernamePasswordAuthenticationFilter]
- POST /login을 처리한다. processingUrl을 변경하면 주소를 바꿀 수 있다.
- form 인증을 처리해주는 필터로 스프링 시큐리티에서 가장 일반적으로 쓰인다.
- 주요 설정 정보
 1. filterProcessingUrl : 로그인을 처리해 줄 URL (POST)
 2. username parameter : POST에 username에 대한 값을 넘겨줄 인자의 이름
 3. password parameter : POST에 password에 대한 값을 넘겨줄 인자의 이름
 4. 로그인 성공시 처리방법 : defaultSuccessUrl(alwaysUse 옵션 설정이 중요), successHandler
 5. 로그인 실패시 처리방법 : failureUrl, failureHandler
 6. authenticationDetailSource : Authentication 객체의 details에 들어갈 정보를 직접 만들어 줌

[DefaultLogoutPageGeneratingFilter]
- GET /logout을 처리한다.
- POST /logout을 요청할 수 있는 UI를 제공한다.
- csrf 토큰이 처리된다.

[LogoutFilter]
- POST /logout을 처리한다. processingUrl을 변경하면 바꿀 수 있다.
- 로그 아웃 처리 -> session, SecurityContext, csrf, 쿠키, remember-me 쿠키 등을 삭제한다.


## Authentication 매커니즘

[ 인증 ]
Authorities안에 어떤 권한을 갖고있는지 정보를 담음
Authentication은 인증된 결과만 저장하는것이 아니고, 인증을 하기 위한 정보와 인증을 받기 이한 정보가 동시에 들어있다.

- Credentials : 인증을 받기 위해 필요한 정보, 비번 등 (Input등)
- Principal : 인증된 결과. 인증 대상 (Output)
- Details : 기타 정보, 인증에 관여된 주변 정보들
- Authorities : 권한 정보들

Authentication을 구현한 객체들은 일반적으로 Token이라는 이름의 객체로 구현함

[ 인증 제공자 AuthenticationProvider ]
Autentication을 제공해줌
AuthenticationProvider는 기본적으로 Authentication을 받아서 인증을 하고 인증된 결과를 다시 Authentication 객체로 전달한다.
인증 대상과 방식이 다양할 수 있기 때문에 인증 제공자도 여러개 올 수 있다.




