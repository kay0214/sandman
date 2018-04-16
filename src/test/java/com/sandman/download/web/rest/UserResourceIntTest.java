package com.sandman.download.web.rest;

import com.sandman.download.SandmanApp;

import com.sandman.download.domain.User;
import com.sandman.download.repository.UserRepository;
import com.sandman.download.service.UserService;
import com.sandman.download.service.dto.UserDTO;
import com.sandman.download.service.mapper.UserMapper;
import com.sandman.download.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.sandman.download.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SandmanApp.class)
public class UserResourceIntTest {

    private static final String DEFAULT_USER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_USER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String DEFAULT_MOBILE = "AAAAAAAAAA";
    private static final String UPDATED_MOBILE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final Integer DEFAULT_GOLD = 1;
    private static final Integer UPDATED_GOLD = 2;

    private static final Integer DEFAULT_AVAILABLE = 1;
    private static final Integer UPDATED_AVAILABLE = 2;

    private static final Long DEFAULT_CREATE_TIME = 1L;
    private static final Long UPDATED_CREATE_TIME = 2L;

    private static final Long DEFAULT_UPDATE_TIME = 1L;
    private static final Long UPDATED_UPDATE_TIME = 2L;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restUserMockMvc;

    private User user;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final UserResource userResource = new UserResource(userService);
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(userResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static User createEntity(EntityManager em) {
        User user = new User()
            .userName(DEFAULT_USER_NAME)
            .password(DEFAULT_PASSWORD)
            .mobile(DEFAULT_MOBILE)
            .email(DEFAULT_EMAIL)
            .gold(DEFAULT_GOLD)
            .available(DEFAULT_AVAILABLE)
            .createTime(DEFAULT_CREATE_TIME)
            .updateTime(DEFAULT_UPDATE_TIME);
        return user;
    }

    @Before
    public void initTest() {
        user = createEntity(em);
    }

    @Test
    @Transactional
    public void createUser() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        // Create the User
        UserDTO userDTO = userMapper.toDto(user);
        restUserMockMvc.perform(post("/api/users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isCreated());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeCreate + 1);
        User testUser = userList.get(userList.size() - 1);
        assertThat(testUser.getUserName()).isEqualTo(DEFAULT_USER_NAME);
        assertThat(testUser.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testUser.getMobile()).isEqualTo(DEFAULT_MOBILE);
        assertThat(testUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUser.getGold()).isEqualTo(DEFAULT_GOLD);
        assertThat(testUser.getAvailable()).isEqualTo(DEFAULT_AVAILABLE);
        assertThat(testUser.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
        assertThat(testUser.getUpdateTime()).isEqualTo(DEFAULT_UPDATE_TIME);
    }

    @Test
    @Transactional
    public void createUserWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        // Create the User with an existing ID
        user.setId(1L);
        UserDTO userDTO = userMapper.toDto(user);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserMockMvc.perform(post("/api/users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllUsers() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        // Get all the userList
        restUserMockMvc.perform(get("/api/users?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(user.getId().intValue())))
            .andExpect(jsonPath("$.[*].userName").value(hasItem(DEFAULT_USER_NAME.toString())))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD.toString())))
            .andExpect(jsonPath("$.[*].mobile").value(hasItem(DEFAULT_MOBILE.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].gold").value(hasItem(DEFAULT_GOLD)))
            .andExpect(jsonPath("$.[*].available").value(hasItem(DEFAULT_AVAILABLE)))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(DEFAULT_CREATE_TIME.intValue())))
            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(DEFAULT_UPDATE_TIME.intValue())));
    }

    @Test
    @Transactional
    public void getUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        // Get the user
        restUserMockMvc.perform(get("/api/users/{id}", user.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(user.getId().intValue()))
            .andExpect(jsonPath("$.userName").value(DEFAULT_USER_NAME.toString()))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD.toString()))
            .andExpect(jsonPath("$.mobile").value(DEFAULT_MOBILE.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.gold").value(DEFAULT_GOLD))
            .andExpect(jsonPath("$.available").value(DEFAULT_AVAILABLE))
            .andExpect(jsonPath("$.createTime").value(DEFAULT_CREATE_TIME.intValue()))
            .andExpect(jsonPath("$.updateTime").value(DEFAULT_UPDATE_TIME.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingUser() throws Exception {
        // Get the user
        restUserMockMvc.perform(get("/api/users/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);
        int databaseSizeBeforeUpdate = userRepository.findAll().size();

        // Update the user
        User updatedUser = userRepository.findOne(user.getId());
        // Disconnect from session so that the updates on updatedUser are not directly saved in db
        em.detach(updatedUser);
        updatedUser
            .userName(UPDATED_USER_NAME)
            .password(UPDATED_PASSWORD)
            .mobile(UPDATED_MOBILE)
            .email(UPDATED_EMAIL)
            .gold(UPDATED_GOLD)
            .available(UPDATED_AVAILABLE)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME);
        UserDTO userDTO = userMapper.toDto(updatedUser);

        restUserMockMvc.perform(put("/api/users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isOk());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate);
        User testUser = userList.get(userList.size() - 1);
        assertThat(testUser.getUserName()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testUser.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testUser.getMobile()).isEqualTo(UPDATED_MOBILE);
        assertThat(testUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUser.getGold()).isEqualTo(UPDATED_GOLD);
        assertThat(testUser.getAvailable()).isEqualTo(UPDATED_AVAILABLE);
        assertThat(testUser.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
        assertThat(testUser.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);
    }

    @Test
    @Transactional
    public void updateNonExistingUser() throws Exception {
        int databaseSizeBeforeUpdate = userRepository.findAll().size();

        // Create the User
        UserDTO userDTO = userMapper.toDto(user);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restUserMockMvc.perform(put("/api/users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isCreated());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);
        int databaseSizeBeforeDelete = userRepository.findAll().size();

        // Get the user
        restUserMockMvc.perform(delete("/api/users/{id}", user.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(User.class);
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(user1.getId());
        assertThat(user1).isEqualTo(user2);
        user2.setId(2L);
        assertThat(user1).isNotEqualTo(user2);
        user1.setId(null);
        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserDTO.class);
        UserDTO userDTO1 = new UserDTO();
        userDTO1.setId(1L);
        UserDTO userDTO2 = new UserDTO();
        assertThat(userDTO1).isNotEqualTo(userDTO2);
        userDTO2.setId(userDTO1.getId());
        assertThat(userDTO1).isEqualTo(userDTO2);
        userDTO2.setId(2L);
        assertThat(userDTO1).isNotEqualTo(userDTO2);
        userDTO1.setId(null);
        assertThat(userDTO1).isNotEqualTo(userDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(userMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(userMapper.fromId(null)).isNull();
    }
}
