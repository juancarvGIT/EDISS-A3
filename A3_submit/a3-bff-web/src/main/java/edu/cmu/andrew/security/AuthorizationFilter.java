package edu.cmu.andrew.security;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.impl.DefaultClaims;

/**
 * <h1> Authorization filter</h1>
 * 
 * This class is an interceptor that will execute at each request it intents to validate the JWT access token
 * 
 * @author Juan Carlos Villegas Montiel
 * @version 1.0
 * @since 2024-03-29
 */
public class AuthorizationFilter extends OncePerRequestFilter {

	private final String HEADER = "Authorization";
	private final String CLIENT_TYPE_HEADER = "X-Client-Type";
	private final String PREFIX = "Bearer ";
	private final String SECRET = "mySecret";
	private final String ISS = "cmu.edu";

	/**
	 * Override method that executes once at every request passing through this BFF.
	 * It will validate the JWT token with auxiliary methods for validate expiration
	 * and claims existence
	 * 
	 * @param request  HttpServletRequest object that represent the request from web
	 *                 client
	 * @param response HttpServletResponse object that represent the response from
	 *                 the service
	 * @param chain    object to access to blobal filter chain
	 * @return void
	 * @throws ServletException, IOException
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		if (request.getHeader(CLIENT_TYPE_HEADER) == null || request.getHeader(CLIENT_TYPE_HEADER).isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Missing " + CLIENT_TYPE_HEADER + " header ");
		} else {

			try {
				if (isJWTTokenMalformed(request, response)) {
					validateUnsecuredToken(request);
				} else {
					SecurityContextHolder.clearContext();
					throw new MalformedJwtException("Token non exists or malformed");
				}
				chain.doFilter(request, response);
			} catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | ParseException e) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());

			}
		}
	}

	/**
	 * Auxiliar method to validate JWT token secured with secret passkey, (not used
	 * in this implementation)
	 * 
	 * @param request HttpServletRequest object that represent the request from web
	 *                client
	 * @return The set of Claims contained in the JWT token
	 */
	private Claims validateToken(HttpServletRequest request) {
		String jwtToken = request.getHeader(HEADER).replace(PREFIX, "");
		return Jwts.parser().setSigningKey(SECRET.getBytes()).parseClaimsJws(jwtToken).getBody();
	}

	/**
	 * Auxiliar method to validate JWT token unsecured with a passkey. It will
	 * validate the existence of required claims, expiration, issuer and subject.
	 * 
	 * @param request HttpServletRequest object that represent the request from web
	 *                client
	 * @return The set of Claims contained in the JWT token
	 * @throws ParseException in case any of the validation fails
	 */
	private Claims validateUnsecuredToken(HttpServletRequest request) throws ParseException {
		String token = request.getHeader(HEADER).replace(PREFIX, "");

		Map<String, Object> claimsMap = null;
		JWT jwt;

		jwt = JWTParser.parse(token);
		claimsMap = jwt.getJWTClaimsSet().getClaims();
		Claims claims = new DefaultClaims(claimsMap);

		if (claims.getExpiration() == null || claims.getSubject() == null || claims.getIssuer() == null) {
			throw new MalformedJwtException("Token invalid");
		}

		if (!contains(claims.getSubject())) {
			System.out.println("Invalid subject");
			throw new MalformedJwtException("Token with invalid subject");
		}

		Date currentDate = new Date();
		if (claims.getExpiration() != null && currentDate.after(new Date(claims.getExpiration().getTime()))) {
			System.out.println("invalid date");
			throw new MalformedJwtException("Token expired");
		}

		if (!claims.getIssuer().equals(ISS)) {
			System.out.println("invalid iss");
			throw new MalformedJwtException("Token non issued by official authority");
		}

		for (String key : claimsMap.keySet()) {
			System.out.printf("%s: %s\n", key, claims.get(key));
		}

		return claims;
	}

	/**
	 * Auxiliar method that extract the Authorization header and validate the JWT
	 * token structure
	 * 
	 * @param request HttpServletRequest object that represent the request from web
	 *                client
	 * @param res     HttpServletResponse object that represent the response from
	 *                the service
	 * @return true if the token is well formed, false otherwise
	 */
	private boolean isJWTTokenMalformed(HttpServletRequest request, HttpServletResponse res) {
		String authenticationHeader = request.getHeader(HEADER);
		if (authenticationHeader != null && !authenticationHeader.startsWith(PREFIX)) {
			return false;
		}
		if (authenticationHeader == null) {
			return false;
		}
		return true;
	}

	/**
	 * Auxiliary method to validate that the given subject claim in one of:
	 * "starlord","gamora","drax","rocket","groot"
	 * 
	 * @param test the subject claim to validate
	 * @return true if the subject claim if within the valid set, false otherwise
	 */
	private boolean contains(String test) {

		for (SubClaim c : SubClaim.values()) {
			if (c.getValue().equals(test)) {
				return true;
			}
		}

		return false;
	}

}