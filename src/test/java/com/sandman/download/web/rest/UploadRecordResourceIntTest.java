package com.sandman.download.web.rest;

import com.sandman.download.SandmanApp;

import com.sandman.download.domain.UploadRecord;
import com.sandman.download.repository.UploadRecordRepository;
import com.sandman.download.service.UploadRecordService;
import com.sandman.download.service.dto.UploadRecordDTO;
import com.sandman.download.service.mapper.UploadRecordMapper;
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
 * Test class for the UploadRecordResource REST controller.
 *
 * @see UploadRecordResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SandmanApp.class)
public class UploadRecordResourceIntTest {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final Long DEFAULT_RES_ID = 1L;
    private static final Long UPDATED_RES_ID = 2L;

    private static final Long DEFAULT_RECORD_TIME = 1L;
    private static final Long UPDATED_RECORD_TIME = 2L;

    @Autowired
    private UploadRecordRepository uploadRecordRepository;

    @Autowired
    private UploadRecordMapper uploadRecordMapper;

    @Autowired
    private UploadRecordService uploadRecordService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restUploadRecordMockMvc;

    private UploadRecord uploadRecord;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final UploadRecordResource uploadRecordResource = new UploadRecordResource(uploadRecordService);
        this.restUploadRecordMockMvc = MockMvcBuilders.standaloneSetup(uploadRecordResource)
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
    public static UploadRecord createEntity(EntityManager em) {
        UploadRecord uploadRecord = new UploadRecord()
            .userId(DEFAULT_USER_ID)
            .resId(DEFAULT_RES_ID)
            .recordTime(DEFAULT_RECORD_TIME);
        return uploadRecord;
    }

    @Before
    public void initTest() {
        uploadRecord = createEntity(em);
    }

    @Test
    @Transactional
    public void createUploadRecord() throws Exception {
        int databaseSizeBeforeCreate = uploadRecordRepository.findAll().size();

        // Create the UploadRecord
        UploadRecordDTO uploadRecordDTO = uploadRecordMapper.toDto(uploadRecord);
        restUploadRecordMockMvc.perform(post("/api/upload-records")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(uploadRecordDTO)))
            .andExpect(status().isCreated());

        // Validate the UploadRecord in the database
        List<UploadRecord> uploadRecordList = uploadRecordRepository.findAll();
        assertThat(uploadRecordList).hasSize(databaseSizeBeforeCreate + 1);
        UploadRecord testUploadRecord = uploadRecordList.get(uploadRecordList.size() - 1);
        assertThat(testUploadRecord.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testUploadRecord.getResId()).isEqualTo(DEFAULT_RES_ID);
        assertThat(testUploadRecord.getRecordTime()).isEqualTo(DEFAULT_RECORD_TIME);
    }

    @Test
    @Transactional
    public void createUploadRecordWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = uploadRecordRepository.findAll().size();

        // Create the UploadRecord with an existing ID
        uploadRecord.setId(1L);
        UploadRecordDTO uploadRecordDTO = uploadRecordMapper.toDto(uploadRecord);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUploadRecordMockMvc.perform(post("/api/upload-records")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(uploadRecordDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UploadRecord in the database
        List<UploadRecord> uploadRecordList = uploadRecordRepository.findAll();
        assertThat(uploadRecordList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllUploadRecords() throws Exception {
        // Initialize the database
        uploadRecordRepository.saveAndFlush(uploadRecord);

        // Get all the uploadRecordList
        restUploadRecordMockMvc.perform(get("/api/upload-records?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(uploadRecord.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].resId").value(hasItem(DEFAULT_RES_ID.intValue())))
            .andExpect(jsonPath("$.[*].recordTime").value(hasItem(DEFAULT_RECORD_TIME.intValue())));
    }

    @Test
    @Transactional
    public void getUploadRecord() throws Exception {
        // Initialize the database
        uploadRecordRepository.saveAndFlush(uploadRecord);

        // Get the uploadRecord
        restUploadRecordMockMvc.perform(get("/api/upload-records/{id}", uploadRecord.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(uploadRecord.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.resId").value(DEFAULT_RES_ID.intValue()))
            .andExpect(jsonPath("$.recordTime").value(DEFAULT_RECORD_TIME.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingUploadRecord() throws Exception {
        // Get the uploadRecord
        restUploadRecordMockMvc.perform(get("/api/upload-records/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUploadRecord() throws Exception {
        // Initialize the database
        uploadRecordRepository.saveAndFlush(uploadRecord);
        int databaseSizeBeforeUpdate = uploadRecordRepository.findAll().size();

        // Update the uploadRecord
        UploadRecord updatedUploadRecord = uploadRecordRepository.findOne(uploadRecord.getId());
        // Disconnect from session so that the updates on updatedUploadRecord are not directly saved in db
        em.detach(updatedUploadRecord);
        updatedUploadRecord
            .userId(UPDATED_USER_ID)
            .resId(UPDATED_RES_ID)
            .recordTime(UPDATED_RECORD_TIME);
        UploadRecordDTO uploadRecordDTO = uploadRecordMapper.toDto(updatedUploadRecord);

        restUploadRecordMockMvc.perform(put("/api/upload-records")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(uploadRecordDTO)))
            .andExpect(status().isOk());

        // Validate the UploadRecord in the database
        List<UploadRecord> uploadRecordList = uploadRecordRepository.findAll();
        assertThat(uploadRecordList).hasSize(databaseSizeBeforeUpdate);
        UploadRecord testUploadRecord = uploadRecordList.get(uploadRecordList.size() - 1);
        assertThat(testUploadRecord.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testUploadRecord.getResId()).isEqualTo(UPDATED_RES_ID);
        assertThat(testUploadRecord.getRecordTime()).isEqualTo(UPDATED_RECORD_TIME);
    }

    @Test
    @Transactional
    public void updateNonExistingUploadRecord() throws Exception {
        int databaseSizeBeforeUpdate = uploadRecordRepository.findAll().size();

        // Create the UploadRecord
        UploadRecordDTO uploadRecordDTO = uploadRecordMapper.toDto(uploadRecord);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restUploadRecordMockMvc.perform(put("/api/upload-records")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(uploadRecordDTO)))
            .andExpect(status().isCreated());

        // Validate the UploadRecord in the database
        List<UploadRecord> uploadRecordList = uploadRecordRepository.findAll();
        assertThat(uploadRecordList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteUploadRecord() throws Exception {
        // Initialize the database
        uploadRecordRepository.saveAndFlush(uploadRecord);
        int databaseSizeBeforeDelete = uploadRecordRepository.findAll().size();

        // Get the uploadRecord
        restUploadRecordMockMvc.perform(delete("/api/upload-records/{id}", uploadRecord.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<UploadRecord> uploadRecordList = uploadRecordRepository.findAll();
        assertThat(uploadRecordList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UploadRecord.class);
        UploadRecord uploadRecord1 = new UploadRecord();
        uploadRecord1.setId(1L);
        UploadRecord uploadRecord2 = new UploadRecord();
        uploadRecord2.setId(uploadRecord1.getId());
        assertThat(uploadRecord1).isEqualTo(uploadRecord2);
        uploadRecord2.setId(2L);
        assertThat(uploadRecord1).isNotEqualTo(uploadRecord2);
        uploadRecord1.setId(null);
        assertThat(uploadRecord1).isNotEqualTo(uploadRecord2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UploadRecordDTO.class);
        UploadRecordDTO uploadRecordDTO1 = new UploadRecordDTO();
        uploadRecordDTO1.setId(1L);
        UploadRecordDTO uploadRecordDTO2 = new UploadRecordDTO();
        assertThat(uploadRecordDTO1).isNotEqualTo(uploadRecordDTO2);
        uploadRecordDTO2.setId(uploadRecordDTO1.getId());
        assertThat(uploadRecordDTO1).isEqualTo(uploadRecordDTO2);
        uploadRecordDTO2.setId(2L);
        assertThat(uploadRecordDTO1).isNotEqualTo(uploadRecordDTO2);
        uploadRecordDTO1.setId(null);
        assertThat(uploadRecordDTO1).isNotEqualTo(uploadRecordDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(uploadRecordMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(uploadRecordMapper.fromId(null)).isNull();
    }
}
