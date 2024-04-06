package com.oidc.authenticator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@SuppressWarnings("deprecation")
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/login*").permitAll();
		http.authorizeRequests().antMatchers("/", "/login**");
//		http.authorizeRequests(authorizeRequests -> authorizeRequests.antMatchers("/", "/login**").permitAll()
//				.anyRequest().authenticated()).oauth2Login(oauth2 -> oauth2.loginPage("/login"));
		return http.build();
	}

	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		return new InMemoryClientRegistrationRepository();
	}

	private static class InMemoryClientRegistrationRepository implements ClientRegistrationRepository {

		@Override
		public ClientRegistration findByRegistrationId(String registrationId) {
			return registrationId.equals("oidc") ? oidcClientRegistration() : null;
		}

		private ClientRegistration oidcClientRegistration() {
			return ClientRegistration.withRegistrationId("oidc").clientId("your-client-id")
					.clientSecret("your-client-secret")
					.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
					.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
					.redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
					.scope(OidcScopes.OPENID, "profile", "email").authorizationUri("authorization-uri")
					.tokenUri("token-uri").userInfoUri("user-info-uri").userNameAttributeName(IdTokenClaimNames.SUB)
					.jwkSetUri("jwk-set-uri").clientName("OIDC Client").build();
		}
	}
}
