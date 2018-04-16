package com.sandman.download.web.rest;

import com.sandman.download.SandmanApp;

import com.sandman.download.domain.ResourceRecord;
import com.sandman.download.repository.ResourceRecordRepository;
import com.sandman.download.service.ResourceRecordService;
import com.sandman.download.service.dto.ResourceRecordDTO;
import com.sandman.download.service.mapper.ResourceRecordMapper;
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
 * Test class for the ResourceRecordResource REST controller.
 *
 * @see ResourceRecordResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SandmanApp.class)
public class ResourceRecordResourceIntTest {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final Long DEFAULT_RES_ID = 1L;
    private static final Long UPDATED_RES_ID = 2L;

    private static final Integer DEFAULT_ORI_GOLD = 1;
    private static final Integer UPDATED_ORI_GOLD = 2;

    private static final Integer DEFAULT_RES_GOLD = 1;
    private static final Integer UPDATED_RES_GOLD = 2;

    private static final Integer DEFAULT_CUR_GOLD = 1;
    private static final Integer UPDATED_CUR_GOLD = 2;

    private static final String DEFAULT_RES_NAME = "AAAAAAAAAA";
    private static final String UPDATED_RES_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_OP_DESC = "AAAAAAAAAA";
    private static final String UPDATED_OP_DESC = "BBBBBBBBBB";

    private static final Long DEFAULT_RECORD_TIME = 1L;
    private static final Long UPDATED_RECORD_TIME = 2L;

    @Autowired
    private ResourceRecordRepository resourceRecordRepository;

    @Autowired
    private ResourceRecordMapper resourceRecordMapper;

    @Autowired
    private ResourceRecordService resourceRecordService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restResourceRecordMockMvc;

    private ResourceRecord resourceRecord;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ResourceRecordResource resourceRecordResource = new ResourceRecordResource(resourceRecordService);
        this.restResourceRecordMockMvc = MockMvcBuilders.standaloneSetup(resourceRecordResource)
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
    public static ResourceRecord createEntity(EntityManager em) {
        ResourceRecord resourceRecord = new ResourceRecord()
            .userId(DEFAULT_USER_ID)
            .resId(DEFAULT_RES_ID)
            .oriGold(DEFAULT_ORI_GOLD)
            .resGold(DEFAULT_RES_GOLD)
            .curGold(DEFAULT_CUR_GOLD)
            .resName(DEFAULT_RES_NAME)
            .opDesc(DEFAULT_OP_DESC)
            .recordTime(DEFAULT_RECORD_TIME);
        return resourceRecord;
    }

    @Before
    public void initTest() {
        resourceRecord = createEntity(em);
    }

    @Test
    @Transactional
    public void createResourceRecord() throws Exception {
        int databaseSizeBeforeCreate = resourceRecordRepository.findAll().size();

        // Create the ResourceRecord
        ResourceRecordDTO resourceRecordDTO = resourceRecordMapper.toDto(resourceRecord);
        restResourceRecordMockMvc.perform(post("/api/resource-records")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(resourceRecordDTO)))
            .andExpect(status().isCreated());

        // Validate the ResourceRecord in the database
        List<ResourceRecord> resourceRecordList = resourceRecordRepository.findAll();
        assertThat(resourceRecordList).hasSize(databaseSizeBeforeCreate + 1);
        ResourceRecord testResourceRecord = resourceRecordList.get(resourceRecordList.size() - 1);
        assertThat(testResourceRecord.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testResourceRecord.getResId()).isEqualTo(DEFAULT_RES_ID);
        assertThat(testResourceRecord.getOriGold()).isEqualTo(DEFAULT_ORI_GOLD);
        assertThat(testResourceRecord.getResGold()).isEqualTo(DEFAULT_RES_GOLD);
        assertThat(testResourceRecord.getCurGold()).isEqualTo(DEFAULT_CUR_GOLD);
        assertThat(testResourceRecord.getResName()).isEqualTo(DEFAULT_RES_NAME);
        assertThat(testResourceRecord.getOpDesc()).isEqualTo(DEFAULT_OP_DESC);
        assertThat(testResourceRecord.getRecordTime()).isEqualTo(DEFAULT_RECORD_TIME);
    }

    @Test
    @Transactional
    public void createResourceRecordWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = resourceRecordRepository.findAll().size();

        // Create the ResourceRecord with an existing ID
        resourceRecord.setId(1L);
        ResourceRecordDTO resourceRecordDTO = resourceRecordMapper.toDto(resourceRecord);

        // An entity with an existing ID cannot be created, so this API call must fail
        restResourceRecordMockMvc.perform(post("/api/resource-records")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(resourceRecordDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ResourceRecord in the database
        List<ResourceRecord> resourceRecordList = resourceRecordRepository.findAll();
        assertThat(resourceRecordList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllResourceRecords() throws Exception {
        // Initialize the database
        resourceRecordRepository.saveAndFlush(resourceRecord);

        // Get all the resourceRecordList
        restResourceRecordMockMvc.perform(get("/api/resource-records?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resourceRecord.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].resId").value(hasItem(DEFAULT_RES_ID.intValue())))
            .andExpect(jsonPath("$.[*].oriGold").value(hasItem(DEFAULT_ORI_GOLD)))
            .andExpect(jsonPath("$.[*].resGold").value(hasItem(DEFAULT_RES_GOLD)))
            .andExpect(jsonPath("$.[*].curGold").value(hasItem(DEFAULT_CUR_GOLD)))
            .andExpect(jsonPath("$.[*].resName").value(hasItem(DEFAULT_RES_NAME.toString())))
            .andExpect(jsonPath("$.[*].opDesc").value(hasItem(DEFAULT_OP_DESC.toString())))
            .andExpect(jsonPath("$.[*].recordTime").value(hasItem(DEFAULT_RECORD_TIME.intValue())));
    }

    @Test
    @Transactional
    public void getResourceRecord() throws Exception {
        // Initialize the database
        resourceRecordRepository.saveAndFlush(resourceRecord);

        // Get the resourceRecord
        restResourceRecordMockMvc.perform(get("/api/resource-records/{id}", resourceRecord.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(resourceRecord.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.resId").value(DEFAULT_RES_ID.intValue()))
            .andExpect(jsonPath("$.oriGold").value(DEFAULT_ORI_GOLD))
            .andExpect(jsonPath("$.resGold").value(DEFAULT_RES_GOLD))
            .andExpect(jsonPath("$.curGold").value(DEFAULT_CUR_GOLD))
            .andExpect(jsonPath("$.resName").value(DEFAULT_RES_NAME.toString()))
            .andExpect(jsonPath("$.opDesc").value(DEFAULT_OP_DESC.toString()))
            .andExpect(jsonPath("$.recordTime").value(DEFAULT_RECORD_TIME.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingResourceRecord() throws Exception {
        // Get the resourceRecord
        restResourceRecordMockMvc.perform(get("/api/resource-records/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateResourceRecord() throws Exception {
        // Initialize the database
        resourceRecordRepository.saveAndFlush(resourceRecord);
        int databaseSizeBeforeUpdate = resourceRecordRepository.findAll().size();

        // Update the resourceRecord
        ResourceRecord updatedResourceRecord = resourceRecordRepository.findOne(resourceRecord.getId());
        // Disconnect from session so that the updates on updatedResourceRecord are not directly saved in db
        em.detach(updatedResourceRecord);
        updatedResourceRecord
            .userId(UPDATED_USER_ID)
            .resId(UPDATED_RES_ID)
            .oriGold(UPDATED_ORI_GOLD)
            .resGold(UPDATED_RES_GOLD)
            .curGold(UPDATED_CUR_GOLD)
            .resName(UPDATED_RES_NAME)
            .opDesc(UPDATED_OP_DESC)
            .recordTime(UPDATED_RECORD_TIME);
        ResourceRecordDTO resourceRecordDTO = resourceRecordMapper.toDto(updatedResourceRecord);

        restResourceRecordMockMvc.perform(put("/api/resource-records")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(resourceRecordDTO)))
            .andExpect(status().isOk());

        // Validate the ResourceRecord in the database
        List<ResourceRecord> resourceRecordList = resourceRecordRepository.findAll();
        assertThat(resourceRecordList).hasSize(databaseSizeBeforeUpdate);
        ResourceRecord testResourceRecord = resourceRecordList.get(resourceRecordList.size() - 1);
        assertThat(testResourceRecord.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testResourceRecord.getResId()).isEqualTo(UPDATED_RES_ID);
        assertThat(testResourceRecord.getOriGold()).isEqualTo(UPDATED_ORI_GOLD);
        assertThat(testResourceRecord.getResGold()).isEqualTo(UPDATED_RES_GOLD);
        assertThat(testResourceRecord.getCurGold()).isEqualTo(UPDATED_CUR_GOLD);
        assertThat(testResourceRecord.getResName()).isEqualTo(UPDATED_RES_NAME);
        assertThat(testResourceRecord.getOpDesc()).isEqualTo(UPDATED_OP_DESC);
        assertThat(testResourceRecord.getRecordTime()).isEqualTo(UPDATED_RECORD_TIME);
    }

    @Test
    @Transactional
    public void updateNonExistingResourceRecord() throws Exception {
        int databaseSizeBeforeUpdate = resourceRecordRepository.findAll().size();

        // Create the ResourceRecord
        ResourceRecordDTO resourceRecordDTO = resourceRecordMapper.toDto(resourceRecord);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restResourceRecordMockMvc.perform(put("/api/resource-records")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(resourceRecordDTO)))
            .andExpect(status().isCreated());

        // Validate the ResourceRecord in the database
        List<ResourceRecord> resourceRecordList = resourceRecordRepository.findAll();
        assertThat(resourceRecordList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteResourceRecord() throws Exception {
        // Initialize the database
        resourceRecordRepository.saveAndFlush(resourceRecord);
        int databaseSizeBeforeDelete = resourceRecordRepository.findAll().size();

        // Get the resourceRecord
        restResourceRecordMockMvc.perform(delete("/api/resource-records/{id}", resourceRecord.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ResourceRecord> resourceRecordList = resourceRecordRepository.findAll();
        assertThat(resourceRecordList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ResourceRecord.class);
        ResourceRecord resourceRecord1 = new ResourceRecord();
        resourceRecord1.setId(1L);
        ResourceRecord resourceRecord2 = new ResourceRecord();
        resourceRecord2.setId(resourceRecord1.getId());
        assertThat(resourceRecord1).isEqualTo(resourceRecord2);
        resourceRecord2.setId(2L);
        assertThat(resourceRecord1).isNotEqualTo(resourceRecord2);
        resourceRecord1.setId(null);
        assertThat(resourceRecord1).isNotEqualTo(resourceRecord2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ResourceRecordDTO.class);
        ResourceRecordDTO resourceRecordDTO1 = new ResourceRecordDTO();
        resourceRecordDTO1.setId(1L);
        ResourceRecordDTO resourceRecordDTO2 = new ResourceRecordDTO();
        assertThat(resourceRecordDTO1).isNotEqualTo(resourceRecordDTO2);
        resourceRecordDTO2.setId(resourceRecordDTO1.getId());
        assertThat(resourceRecordDTO1).isEqualTo(resourceRecordDTO2);
        resourceRecordDTO2.setId(2L);
        assertThat(resourceRecordDTO1).isNotEqualTo(resourceRecordDTO2);
        resourceRecordDTO1.setId(null);
        assertThat(resourceRecordDTO1).isNotEqualTo(resourceRecordDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(resourceRecordMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(resourceRecordMapper.fromId(null)).isNull();
    }
}
