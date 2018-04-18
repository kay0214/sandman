package com.sandman.download.web.rest;

import com.sandman.download.SandmanApp;

import com.sandman.download.domain.Resource;
import com.sandman.download.repository.ResourceRepository;
import com.sandman.download.service.ResourceService;
import com.sandman.download.service.dto.ResourceDTO;
import com.sandman.download.service.mapper.ResourceMapper;
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
 * Test class for the ResourceResource REST controller.
 *
 * @see ResourceResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SandmanApp.class)
public class ResourceResourceIntTest {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final String DEFAULT_RES_NAME = "AAAAAAAAAA";
    private static final String UPDATED_RES_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_RES_URL = "AAAAAAAAAA";
    private static final String UPDATED_RES_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_RES_GOLD = 1;
    private static final Integer UPDATED_RES_GOLD = 2;

    private static final String DEFAULT_RES_DESC = "AAAAAAAAAA";
    private static final String UPDATED_RES_DESC = "BBBBBBBBBB";

    private static final String DEFAULT_RES_SIZE = "AAAAAAAAAA";
    private static final String UPDATED_RES_SIZE = "BBBBBBBBBB";

    private static final String DEFAULT_RES_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_RES_TYPE = "BBBBBBBBBB";

    private static final Integer DEFAULT_DOWN_COUNT = 1;
    private static final Integer UPDATED_DOWN_COUNT = 2;

    private static final Integer DEFAULT_STATUS = 1;
    private static final Integer UPDATED_STATUS = 2;

    private static final Long DEFAULT_CREATE_TIME = 1L;
    private static final Long UPDATED_CREATE_TIME = 2L;

    private static final Long DEFAULT_UPDATE_TIME = 1L;
    private static final Long UPDATED_UPDATE_TIME = 2L;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restResourceMockMvc;

    private Resource resource;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ResourceResource resourceResource = new ResourceResource(resourceService);
        this.restResourceMockMvc = MockMvcBuilders.standaloneSetup(resourceResource)
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
    public static Resource createEntity(EntityManager em) {
        Resource resource = new Resource()
            .userId(DEFAULT_USER_ID)
            .resName(DEFAULT_RES_NAME)
            .resUrl(DEFAULT_RES_URL)
            .resGold(DEFAULT_RES_GOLD)
            .resDesc(DEFAULT_RES_DESC)
            .resSize(DEFAULT_RES_SIZE)
            .resType(DEFAULT_RES_TYPE)
            .downCount(DEFAULT_DOWN_COUNT)
            .status(DEFAULT_STATUS)
            .createTime(DEFAULT_CREATE_TIME)
            .updateTime(DEFAULT_UPDATE_TIME);
        return resource;
    }

    @Before
    public void initTest() {
        resource = createEntity(em);
    }

    @Test
    @Transactional
    public void createResource() throws Exception {
        int databaseSizeBeforeCreate = resourceRepository.findAll().size();

        // Create the Resource
        ResourceDTO resourceDTO = resourceMapper.toDto(resource);
        restResourceMockMvc.perform(post("/api/resources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(resourceDTO)))
            .andExpect(status().isCreated());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeCreate + 1);
        Resource testResource = resourceList.get(resourceList.size() - 1);
        assertThat(testResource.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testResource.getResName()).isEqualTo(DEFAULT_RES_NAME);
        assertThat(testResource.getResUrl()).isEqualTo(DEFAULT_RES_URL);
        assertThat(testResource.getResGold()).isEqualTo(DEFAULT_RES_GOLD);
        assertThat(testResource.getResDesc()).isEqualTo(DEFAULT_RES_DESC);
        assertThat(testResource.getResSize()).isEqualTo(DEFAULT_RES_SIZE);
        assertThat(testResource.getResType()).isEqualTo(DEFAULT_RES_TYPE);
        assertThat(testResource.getDownCount()).isEqualTo(DEFAULT_DOWN_COUNT);
        assertThat(testResource.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testResource.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
        assertThat(testResource.getUpdateTime()).isEqualTo(DEFAULT_UPDATE_TIME);
    }

    @Test
    @Transactional
    public void createResourceWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = resourceRepository.findAll().size();

        // Create the Resource with an existing ID
        resource.setId(1L);
        ResourceDTO resourceDTO = resourceMapper.toDto(resource);

        // An entity with an existing ID cannot be created, so this API call must fail
        restResourceMockMvc.perform(post("/api/resources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(resourceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllResources() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList
        restResourceMockMvc.perform(get("/api/resources?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resource.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].resName").value(hasItem(DEFAULT_RES_NAME.toString())))
            .andExpect(jsonPath("$.[*].resUrl").value(hasItem(DEFAULT_RES_URL.toString())))
            .andExpect(jsonPath("$.[*].resGold").value(hasItem(DEFAULT_RES_GOLD)))
            .andExpect(jsonPath("$.[*].resDesc").value(hasItem(DEFAULT_RES_DESC.toString())))
            .andExpect(jsonPath("$.[*].resSize").value(hasItem(DEFAULT_RES_SIZE.toString())))
            .andExpect(jsonPath("$.[*].resType").value(hasItem(DEFAULT_RES_TYPE.toString())))
            .andExpect(jsonPath("$.[*].downCount").value(hasItem(DEFAULT_DOWN_COUNT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(DEFAULT_CREATE_TIME.intValue())))
            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(DEFAULT_UPDATE_TIME.intValue())));
    }

    @Test
    @Transactional
    public void getResource() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get the resource
        restResourceMockMvc.perform(get("/api/resources/{id}", resource.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(resource.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.resName").value(DEFAULT_RES_NAME.toString()))
            .andExpect(jsonPath("$.resUrl").value(DEFAULT_RES_URL.toString()))
            .andExpect(jsonPath("$.resGold").value(DEFAULT_RES_GOLD))
            .andExpect(jsonPath("$.resDesc").value(DEFAULT_RES_DESC.toString()))
            .andExpect(jsonPath("$.resSize").value(DEFAULT_RES_SIZE.toString()))
            .andExpect(jsonPath("$.resType").value(DEFAULT_RES_TYPE.toString()))
            .andExpect(jsonPath("$.downCount").value(DEFAULT_DOWN_COUNT))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.createTime").value(DEFAULT_CREATE_TIME.intValue()))
            .andExpect(jsonPath("$.updateTime").value(DEFAULT_UPDATE_TIME.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingResource() throws Exception {
        // Get the resource
        restResourceMockMvc.perform(get("/api/resources/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateResource() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);
        int databaseSizeBeforeUpdate = resourceRepository.findAll().size();

        // Update the resource
        Resource updatedResource = resourceRepository.findOne(resource.getId());
        // Disconnect from session so that the updates on updatedResource are not directly saved in db
        em.detach(updatedResource);
        updatedResource
            .userId(UPDATED_USER_ID)
            .resName(UPDATED_RES_NAME)
            .resUrl(UPDATED_RES_URL)
            .resGold(UPDATED_RES_GOLD)
            .resDesc(UPDATED_RES_DESC)
            .resSize(UPDATED_RES_SIZE)
            .resType(UPDATED_RES_TYPE)
            .downCount(UPDATED_DOWN_COUNT)
            .status(UPDATED_STATUS)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME);
        ResourceDTO resourceDTO = resourceMapper.toDto(updatedResource);

        restResourceMockMvc.perform(put("/api/resources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(resourceDTO)))
            .andExpect(status().isOk());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
        Resource testResource = resourceList.get(resourceList.size() - 1);
        assertThat(testResource.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testResource.getResName()).isEqualTo(UPDATED_RES_NAME);
        assertThat(testResource.getResUrl()).isEqualTo(UPDATED_RES_URL);
        assertThat(testResource.getResGold()).isEqualTo(UPDATED_RES_GOLD);
        assertThat(testResource.getResDesc()).isEqualTo(UPDATED_RES_DESC);
        assertThat(testResource.getResSize()).isEqualTo(UPDATED_RES_SIZE);
        assertThat(testResource.getResType()).isEqualTo(UPDATED_RES_TYPE);
        assertThat(testResource.getDownCount()).isEqualTo(UPDATED_DOWN_COUNT);
        assertThat(testResource.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testResource.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
        assertThat(testResource.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);
    }

    @Test
    @Transactional
    public void updateNonExistingResource() throws Exception {
        int databaseSizeBeforeUpdate = resourceRepository.findAll().size();

        // Create the Resource
        ResourceDTO resourceDTO = resourceMapper.toDto(resource);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restResourceMockMvc.perform(put("/api/resources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(resourceDTO)))
            .andExpect(status().isCreated());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteResource() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);
        int databaseSizeBeforeDelete = resourceRepository.findAll().size();

        // Get the resource
        restResourceMockMvc.perform(delete("/api/resources/{id}", resource.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Resource.class);
        Resource resource1 = new Resource();
        resource1.setId(1L);
        Resource resource2 = new Resource();
        resource2.setId(resource1.getId());
        assertThat(resource1).isEqualTo(resource2);
        resource2.setId(2L);
        assertThat(resource1).isNotEqualTo(resource2);
        resource1.setId(null);
        assertThat(resource1).isNotEqualTo(resource2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ResourceDTO.class);
        ResourceDTO resourceDTO1 = new ResourceDTO();
        resourceDTO1.setId(1L);
        ResourceDTO resourceDTO2 = new ResourceDTO();
        assertThat(resourceDTO1).isNotEqualTo(resourceDTO2);
        resourceDTO2.setId(resourceDTO1.getId());
        assertThat(resourceDTO1).isEqualTo(resourceDTO2);
        resourceDTO2.setId(2L);
        assertThat(resourceDTO1).isNotEqualTo(resourceDTO2);
        resourceDTO1.setId(null);
        assertThat(resourceDTO1).isNotEqualTo(resourceDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(resourceMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(resourceMapper.fromId(null)).isNull();
    }
}
