package com.mdd.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueWithoutAuthentication_whenNoAuthorizationHeader() throws ServletException, IOException {
        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldContinueWithoutAuthentication_whenHeaderIsNotBearer() throws ServletException, IOException {
        request.addHeader("Authorization", "Basic abc123");

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldContinueWithoutAuthentication_whenTokenIsInvalid() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer invalid-token");
        when(jwtService.isTokenValid("invalid-token")).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, never()).extractEmail(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldAuthenticateUser_whenTokenIsValid() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer valid-token");
        when(jwtService.isTokenValid("valid-token")).thenReturn(true);
        when(jwtService.extractEmail("valid-token")).thenReturn("john@example.com");

        filter.doFilter(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("john@example.com", auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotOverrideExistingAuthentication() throws ServletException, IOException {
        Authentication existing = new UsernamePasswordAuthenticationToken("existing@example.com", null);
        SecurityContextHolder.getContext().setAuthentication(existing);

        request.addHeader("Authorization", "Bearer valid-token");
        when(jwtService.isTokenValid("valid-token")).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertSame(existing, SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, never()).extractEmail(anyString());
        verify(filterChain).doFilter(request, response);
    }
}