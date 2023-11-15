package com.example.ProductReviewsWebApp;

import com.example.ProductReviewsWebApp.configuration.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityConfig securityConfig;

    @Test
    public void givenAuthRequestOnPrivateService_shouldSucceedWith200() {
        ResponseEntity<String> result = template.withBasicAuth("admin", "password")
                .getForEntity("/private/hello", String.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void givenBasicUser_whenGetFooSalute_then3xxRedirection() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.get("/foo/salute")
                        .accept(MediaType.ALL))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void givenManagerUser_whenGetFooSalute_thenOk() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.get("/api")
                        .accept(MediaType.ALL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test_UserPassword() {
        String password = securityConfig.userDetailsService().loadUserByUsername("user").getPassword();
        assertEquals(password, "{noop}password");
    }

    @Test
    public void test_User_isEnabled() {
        assertTrue(securityConfig.userDetailsService().loadUserByUsername("user").isEnabled());
    }
    @Test
    public void test_AdminPassword() {
        String password = securityConfig.userDetailsService().loadUserByUsername("admin").getPassword();
        assertEquals(password, "{noop}password");
    }
    @Test
    public void test_Admin_isEnabled() {
        assertTrue(securityConfig.userDetailsService().loadUserByUsername("admin").isEnabled());
    }

    @Test
    public void test_NotLoggedInUser_isNotAuthenticated() {
        assertFalse(securityConfig.isAuthenticated());
    }
}
