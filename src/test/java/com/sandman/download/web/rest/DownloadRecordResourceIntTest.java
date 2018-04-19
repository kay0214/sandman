package com.sandman.download.web.rest;

import com.sandman.download.SandmanApp;

import com.sandman.download.domain.DownloadRecord;
import com.sandman.download.repository.DownloadRecordRepository;
import com.sandman.download.service.DownloadRecordService;
import com.sandman.download.service.dto.DownloadRecordDTO;
import com.sandman.download.service.mapper.DownloadRecordMapper;
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
 * Test class for the DownloadRecordResource REST controller.
 *
 * @see DownloadRecordResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SandmanApp.class)
public class DownloadRecordResourceIntTest {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final Long DEFAULT_RECORD_TIME = 1L;
    private static final Long UPDATED_RECORD_TIME = 2L;

    @Autowired
    private DownloadRecordRepository downloadRecordRepository;

    @Autowired
    private DownloadRecordMapper downloadRecordMapper;

    @Autowired
    private DownloadRecordService downloadRecordService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restDownloadRecordMockMvc;

    private DownloadRecord downloadRecord;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DownloadRecordResource downloadRecordResource = new DownloadRecordResource(downloadRecordService);
        this.restDownloadRecordMockMvc = MockMvcBuilders.standaloneSetup(downloadRecordResource)
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
    public static DownloadRecord createEntity(EntityManager em) {
        DownloadRecord downloadRecord = new DownloadRecord()
            .userId(DEFAULT_USER_ID)
            .recordTime(DEFAULT_RECORD_TIME);
        return downloadRecord;
    }

    @Before
    public void initTest() {
        downloadRecord = createEntity(em);
    }

    @Test
    @Transactional
    public void createDownloadRecord() throws Exception {
        int databaseSizeBeforeCreate = downloadRecordRepository.findAll().size();

        // Create the DownloadRecord
        DownloadRecordDTO downloadRecordDTO = downloadRecordMapper.toDto(downloadRecord);
        restDownloadRecordMockMvc.perform(post("/api/download-records")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(downloadRecordDTO)))
            .andExpect(status().isCreated());

        // Validate the DownloadRecord in the database
        List<DownloadRecord> downloadRecordList = downloadRecordRepository.findAll();
        assertThat(downloadRecordList).hasSize(databaseSizeBeforeCreate + 1);
        DownloadRecord testDownloadRecord = downloadRecordList.get(downloadRecordList.size() - 1);
        assertThat(testDownloadRecord.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testDownloadRecord.getRecordTime()).isEqualTo(DEFAULT_RECORD_TIME);
    }

    @Test
    @Transactional
    public void createDownloadRecordWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = downloadRecordRepository.findAll().size();

        // Create the DownloadRecord with an existing ID
        downloadRecord.setId(1L);
        DownloadRecordDTO downloadRecordDTO = downloadRecordMapper.toDto(downloadRecord);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDownloadRecordMockMvc.perform(post("/api/download-records")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(downloadRecordDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DownloadRecord in the database
        List<DownloadRecord> downloadRecordList = downloadRecordRepository.findAll();
        assertThat(downloadRecordList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllDownloadRecords() throws Exception {
        // Initialize the database
        downloadRecordRepository.saveAndFlush(downloadRecord);

        // Get all the downloadRecordList
        restDownloadRecordMockMvc.perform(get("/api/download-records?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(downloadRecord.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].recordTime").value(hasItem(DEFAULT_RECORD_TIME.intValue())));
    }

    @Test
    @Transactional
    public void getDownloadRecord() throws Exception {
        // Initialize the database
        downloadRecordRepository.saveAndFlush(downloadRecord);

        // Get the downloadRecord
        restDownloadRecordMockMvc.perform(get("/api/download-records/{id}", downloadRecord.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(downloadRecord.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.recordTime").value(DEFAULT_RECORD_TIME.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingDownloadRecord() throws Exception {
        // Get the downloadRecord
        restDownloadRecordMockMvc.perform(get("/api/download-records/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDownloadRecord() throws Exception {
        // Initialize the database
        downloadRecordRepository.saveAndFlush(downloadRecord);
        int databaseSizeBeforeUpdate = downloadRecordRepository.findAll().size();

        // Update the downloadRecord
        DownloadRecord updatedDownloadRecord = downloadRecordRepository.findOne(downloadRecord.getId());
        // Disconnect from session so that the updates on updatedDownloadRecord are not directly saved in db
        em.detach(updatedDownloadRecord);
        updatedDownloadRecord
            .userId(UPDATED_USER_ID)
            .recordTime(UPDATED_RECORD_TIME);
        DownloadRecordDTO downloadRecordDTO = downloadRecordMapper.toDto(updatedDownloadRecord);

        restDownloadRecordMockMvc.perform(put("/api/download-records")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(downloadRecordDTO)))
            .andExpect(status().isOk());

        // Validate the DownloadRecord in the database
        List<DownloadRecord> downloadRecordList = downloadRecordRepository.findAll();
        assertThat(downloadRecordList).hasSize(databaseSizeBeforeUpdate);
        DownloadRecord testDownloadRecord = downloadRecordList.get(downloadRecordList.size() - 1);
        assertThat(testDownloadRecord.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testDownloadRecord.getRecordTime()).isEqualTo(UPDATED_RECORD_TIME);
    }

    @Test
    @Transactional
    public void updateNonExistingDownloadRecord() throws Exception {
        int databaseSizeBeforeUpdate = downloadRecordRepository.findAll().size();

        // Create the DownloadRecord
        DownloadRecordDTO downloadRecordDTO = downloadRecordMapper.toDto(downloadRecord);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restDownloadRecordMockMvc.perform(put("/api/download-records")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(downloadRecordDTO)))
            .andExpect(status().isCreated());

        // Validate the DownloadRecord in the database
        List<DownloadRecord> downloadRecordList = downloadRecordRepository.findAll();
        assertThat(downloadRecordList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteDownloadRecord() throws Exception {
        // Initialize the database
        downloadRecordRepository.saveAndFlush(downloadRecord);
        int databaseSizeBeforeDelete = downloadRecordRepository.findAll().size();

        // Get the downloadRecord
        restDownloadRecordMockMvc.perform(delete("/api/download-records/{id}", downloadRecord.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<DownloadRecord> downloadRecordList = downloadRecordRepository.findAll();
        assertThat(downloadRecordList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DownloadRecord.class);
        DownloadRecord downloadRecord1 = new DownloadRecord();
        downloadRecord1.setId(1L);
        DownloadRecord downloadRecord2 = new DownloadRecord();
        downloadRecord2.setId(downloadRecord1.getId());
        assertThat(downloadRecord1).isEqualTo(downloadRecord2);
        downloadRecord2.setId(2L);
        assertThat(downloadRecord1).isNotEqualTo(downloadRecord2);
        downloadRecord1.setId(null);
        assertThat(downloadRecord1).isNotEqualTo(downloadRecord2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DownloadRecordDTO.class);
        DownloadRecordDTO downloadRecordDTO1 = new DownloadRecordDTO();
        downloadRecordDTO1.setId(1L);
        DownloadRecordDTO downloadRecordDTO2 = new DownloadRecordDTO();
        assertThat(downloadRecordDTO1).isNotEqualTo(downloadRecordDTO2);
        downloadRecordDTO2.setId(downloadRecordDTO1.getId());
        assertThat(downloadRecordDTO1).isEqualTo(downloadRecordDTO2);
        downloadRecordDTO2.setId(2L);
        assertThat(downloadRecordDTO1).isNotEqualTo(downloadRecordDTO2);
        downloadRecordDTO1.setId(null);
        assertThat(downloadRecordDTO1).isNotEqualTo(downloadRecordDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(downloadRecordMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(downloadRecordMapper.fromId(null)).isNull();
    }
}
