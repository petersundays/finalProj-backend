import domcast.finalprojbackend.bean.ComponentResourceBean;
import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.dao.ComponentResourceDao;
import domcast.finalprojbackend.dao.M2MComponentProjectDao;
import domcast.finalprojbackend.dao.ProjectDao;
import domcast.finalprojbackend.dto.componentResourceDto.CRPreview;
import domcast.finalprojbackend.dto.componentResourceDto.DetailedCR;
import domcast.finalprojbackend.entity.ComponentResourceEntity;
import domcast.finalprojbackend.entity.M2MComponentProject;
import domcast.finalprojbackend.entity.ProjectEntity;
import domcast.finalprojbackend.enums.ComponentResourceEnum;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ComponentResourceBeanTest {

    @InjectMocks
    private ComponentResourceBean componentResourceBean;

    @Mock
    private DataValidator dataValidator;

    @Mock
    private ComponentResourceDao componentResourceDao;

    @Mock
    private ProjectDao projectDao;

    @Mock
    private M2MComponentProjectDao m2MComponentProjectDao;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateComponentResource_Success() {
        // Arrange
        DetailedCR detailedCR = new DetailedCR();
        detailedCR.setName("Test Name");
        detailedCR.setDescription("Test Description");
        detailedCR.setBrand("Test Brand");
        detailedCR.setPartNumber(555555L);
        detailedCR.setType(ComponentResourceEnum.COMPONENT.getId());
        detailedCR.setSupplier("Test Supplier");
        detailedCR.setSupplierContact("123456789");
        Integer projectId = 1; // Ensure this is greater than 0

        ComponentResourceEntity mockEntity = new ComponentResourceEntity();
        mockEntity.setName(detailedCR.getName());
        mockEntity.setBrand(detailedCR.getBrand());
        when(componentResourceDao.findCREntityByNameAndBrand(anyString(), anyString())).thenReturn(mockEntity);

        // Act
        CRPreview result = componentResourceBean.createComponentResource(detailedCR, projectId);

        // Assert
        assertNotNull(result);
        assertEquals(detailedCR.getName(), result.getName());
        assertEquals(detailedCR.getBrand(), result.getBrand());
    }

    /**
     * Test the createComponentResource method of ComponentResourceBean
     * Failure case
     */
    @Test
    public void testCreateComponentResource_Failure() {
        // Arrange
        DetailedCR detailedCR = new DetailedCR();
        detailedCR.setName("Test Name");
        detailedCR.setBrand("Test Brand");
        detailedCR.setType(ComponentResourceEnum.COMPONENT.getId());
        detailedCR.setQuantity(1);
        when(dataValidator.isCRMandatoryDataValid(detailedCR, null)).thenReturn(false);

        // Act
        CRPreview result = componentResourceBean.createComponentResource(detailedCR);

        // Assert
        assertNotNull(result);
    }

    /**
     * Test the registerData method of ComponentResourceBean
     * Success case
     */
    @Test
    public void testRegisterData_Success() {
        // Arrange
        DetailedCR detailedCR = new DetailedCR();
        detailedCR.setName("Test Name");
        detailedCR.setBrand("Test Brand");
        detailedCR.setType(ComponentResourceEnum.COMPONENT.getId());
        detailedCR.setQuantity(1);
        Integer projectId = 1;
        when(componentResourceDao.doesCRExistByNameAndBrand(anyString(), anyString())).thenReturn(false);
        when(componentResourceDao.findCREntityByNameAndBrand(anyString(), anyString())).thenReturn(new ComponentResourceEntity());

        // Act
        ComponentResourceEntity result = componentResourceBean.registerData(detailedCR, projectId);

        // Assert
        assertNotNull(result);
        assertEquals("Test Name", result.getName());
        assertEquals("Test Brand", result.getBrand());
    }

    /**
     * Test the registerData method of ComponentResourceBean
     * Failure case
     */
    @Test
    public void testRegisterData_Failure() {
        // Arrange
        DetailedCR detailedCR = new DetailedCR();
        detailedCR.setName("Test Name");
        detailedCR.setBrand("Test Brand");
        detailedCR.setType(ComponentResourceEnum.COMPONENT.getId());
        detailedCR.setQuantity(1);
        Integer projectId = 1;
        when(componentResourceDao.doesCRExistByNameAndBrand(anyString(), anyString())).thenReturn(true);

        // Act
        ComponentResourceEntity result = componentResourceBean.registerData(detailedCR, projectId);

        // Assert
        assertNull(result);
    }

    /**
     * Test the addCRToProject method of ComponentResourceBean
     * Success case
     */
    @Test
    public void testAddCRToProject_Success() {
        // Arrange
        int projectId = 1;
        ComponentResourceEntity componentResource = mock(ComponentResourceEntity.class);
        int quantity = 1;
        when(dataValidator.isIdValid(projectId)).thenReturn(true);
        when(projectDao.findProjectById(projectId)).thenReturn(new ProjectEntity());

        // Act
        componentResourceBean.addCRToProject(projectId, componentResource, quantity);

        // Assert
        verify(projectDao, times(1)).findProjectById(projectId);
        verify(projectDao, times(1)).merge(any(ProjectEntity.class));
        verify(componentResourceDao, times(1)).merge(any(ComponentResourceEntity.class));
    }

    /**
     * Test the addCRToProject method of ComponentResourceBean
     * Failure case
     */
    @Test
    public void testAddCRToProject_Failure() {
        // Arrange
        int projectId = 1;
        ComponentResourceEntity componentResource = new ComponentResourceEntity();
        int quantity = 1;
        when(dataValidator.isIdValid(projectId)).thenReturn(false);

        // Act
        componentResourceBean.addCRToProject(projectId, componentResource, quantity);

        // Assert
        verify(projectDao, times(0)).findProjectById(projectId);
        verify(projectDao, times(0)).merge(any(ProjectEntity.class));
        verify(componentResourceDao, times(0)).merge(any(ComponentResourceEntity.class));
    }

    /**
     * Test the updateCR method of ComponentResourceBean
     * Success case
     */
    @Test
    public void testEntityToPreviewCR_Success() {
        // Arrange
        ComponentResourceEntity entityCR = new ComponentResourceEntity();
        entityCR.setName("Test Name");
        entityCR.setBrand("Test Brand");
        entityCR.setType(ComponentResourceEnum.COMPONENT);

        // Act
        CRPreview result = componentResourceBean.entityToPreviewCR(entityCR);

        // Assert
        assertNotNull(result);
        assertEquals("Test Name", result.getName());
        assertEquals("Test Brand", result.getBrand());
    }

    /**
     * Test the updateCR method of ComponentResourceBean
     * Failure case
     */
    @Test
    public void testEntityToPreviewCR_Failure() {
        // Arrange
        ComponentResourceEntity entityCR = null;

        // Act
        CRPreview result = componentResourceBean.entityToPreviewCR(entityCR);

        // Assert
        assertNull(result);
    }

    /**
     * Test the updateCR method of ComponentResourceBean
     * Success case
     */
    @Test
    public void testEditComponentResource_Success() {
        // Arrange
        DetailedCR detailedCR = new DetailedCR();
        detailedCR.setName("Test Name");
        detailedCR.setBrand("Test Brand");
        detailedCR.setType(ComponentResourceEnum.COMPONENT.getId());
        when(componentResourceDao.findCREntityById(anyInt())).thenReturn(new ComponentResourceEntity());

        // Act
        DetailedCR result = componentResourceBean.editComponentResource(detailedCR, 1, 1);

        // Assert
        assertNotNull(result);
    }

    /**
     * Test the updateCR method of ComponentResourceBean
     * Failure case
     */
    @Test
    public void testEditComponentResource_Failure() {
        // Arrange
        DetailedCR detailedCR = new DetailedCR();
        detailedCR.setName("Test Name");
        detailedCR.setBrand("Test Brand");
        detailedCR.setType(ComponentResourceEnum.COMPONENT.getId());
        when(componentResourceDao.findCREntityById(anyInt())).thenReturn(null);

        // Act
        DetailedCR result = componentResourceBean.editComponentResource(detailedCR, 1, 1);

        // Assert
        assertNull(result);
    }

    /**
     * Test the updateData method of ComponentResourceBean
     * Success case
     */
    @Test
    public void testUpdateData_Success() {
        // Arrange
        DetailedCR detailedCR = new DetailedCR();
        detailedCR.setName("Test Name");
        detailedCR.setBrand("Test Brand");
        detailedCR.setType(ComponentResourceEnum.COMPONENT.getId());
        ComponentResourceEntity componentResourceEntity = new ComponentResourceEntity();

        // Act
        ComponentResourceEntity result = componentResourceBean.updateData(detailedCR, componentResourceEntity, 1);

        // Assert
        assertNotNull(result);
    }

    /**
     * Test the updateData method of ComponentResourceBean
     * Failure case
     */
    @Test
    public void testUpdateData_Failure() {
        // Arrange
        DetailedCR detailedCR = new DetailedCR();
        detailedCR.setName("Test Name");
        detailedCR.setBrand("Test Brand");
        detailedCR.setType(ComponentResourceEnum.COMPONENT.getId());
        ComponentResourceEntity componentResourceEntity = null;

        // Act
        ComponentResourceEntity result = componentResourceBean.updateData(detailedCR, componentResourceEntity, 1);

        // Assert
        assertNull(result);
    }

    /**
     * Test the inactivateRelation method of ComponentResourceBean
     * Success case
     */
    @Test
    public void testInactivateRelation_Success() {
        // Arrange
        when(dataValidator.isIdValid(anyInt())).thenReturn(true);
        when(m2MComponentProjectDao.findM2MComponentProjectByComponentIdAndProjectId(anyInt(), anyInt())).thenReturn(new M2MComponentProject());

        // Act
        boolean result = componentResourceBean.inactivateRelation(1, 1);

        // Assert
        assertTrue(result);
    }

    /**
     * Test the inactivateRelation method of ComponentResourceBean
     * Failure case
     */
    @Test
    public void testInactivateRelation_Failure() {
        // Arrange
        when(dataValidator.isIdValid(anyInt())).thenReturn(false);

        // Act
        boolean result = componentResourceBean.inactivateRelation(1, 1);

        // Assert
        assertFalse(result);
    }

    /**
     * Test the deleteComponentResource method of ComponentResourceBean
     * Success case
     */
    @Test
    public void testGetComponentResourcesByProjectId_Success() {
        // Arrange
        when(dataValidator.isIdValid(anyInt())).thenReturn(true);
        when(m2MComponentProjectDao.findComponentResourceIdsByProjectId(anyInt())).thenReturn(new HashSet<>(Arrays.asList(1, 2, 3)));
        when(componentResourceDao.findCREntityById(anyInt())).thenReturn(new ComponentResourceEntity());

        // Act
        Set<CRPreview> result = componentResourceBean.getComponentResourcesByProjectId(1);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    /**
     * Test the deleteComponentResource method of ComponentResourceBean
     * Failure case
     */
    @Test
    public void testGetComponentResourcesByProjectId_Failure() {
        // Arrange
        when(dataValidator.isIdValid(anyInt())).thenReturn(false);

        // Act
        Set<CRPreview> result = componentResourceBean.getComponentResourcesByProjectId(1);

        // Assert
        assertNull(result);
    }

    /**
     * Test the deleteComponentResource method of ComponentResourceBean
     * Success case
     */
    @Test
    public void testGetComponentResourcesByCriteria_Success() {
        // Arrange
        when(dataValidator.isPageNumberValid(anyInt())).thenReturn(true);
        when(dataValidator.isPageSizeValid(anyInt())).thenReturn(true);
        when(componentResourceDao.getComponentResourcesByCriteria(anyString(), anyString(), anyLong(), anyString(), anyString(), anyBoolean(), anyInt(), anyInt())).thenReturn(new ArrayList<>(Arrays.asList(new ComponentResourceEntity(), new ComponentResourceEntity(), new ComponentResourceEntity())));

        // Act
        List<CRPreview> result = componentResourceBean.getComponentResourcesByCriteria("Test Name", "Test Brand", 555555L, "Test Supplier", "name", true, 1, 10);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    /**
     * Test the deleteComponentResource method of ComponentResourceBean
     * Failure case
     */
    @Test
    public void testGetComponentResourcesByCriteria_Failure() {
        // Arrange
        when(dataValidator.isPageNumberValid(anyInt())).thenReturn(false);

        // Act
        List<CRPreview> result = componentResourceBean.getComponentResourcesByCriteria("Test Name", "Test Brand", 555555L, "Test Supplier", "name", true, 1, 10);

        // Assert
        assertTrue(result.isEmpty());
    }

    /**
     * Test the deleteComponentResource method of ComponentResourceBean
     * Success case
     */
    @Test
    public void testFindCREntityByNameAndBrand_Success() {
        // Arrange
        Set<DetailedCR> detailedCRs = new HashSet<>();
        DetailedCR detailedCR = new DetailedCR();
        detailedCR.setName("Test Name");
        detailedCR.setBrand("Test Brand");
        detailedCRs.add(detailedCR);

        ComponentResourceEntity componentResourceEntity = new ComponentResourceEntity();
        componentResourceEntity.setId(1);
        when(componentResourceDao.findCREntityByNameAndBrand(detailedCR.getName(), detailedCR.getBrand())).thenReturn(componentResourceEntity);

        // Act
        Set<Integer> result = componentResourceBean.findCREntityByNameAndBrand(detailedCRs);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains(componentResourceEntity.getId()));
    }

    /**
     * Test the deleteComponentResource method of ComponentResourceBean
     * Failure case
     */
    @Test
    public void testFindCREntityByNameAndBrand_Failure() {
        // Arrange
        Set<DetailedCR> detailedCRs = null;

        // Act
        Set<Integer> result = componentResourceBean.findCREntityByNameAndBrand(detailedCRs);

        // Assert
        assertNull(result);
    }

    /**
     * Test the deleteComponentResource method of ComponentResourceBean
     * Success case
     */
    @Test
    public void testFindEntityAndSetQuantity_Success() {
        // Arrange
        Set<DetailedCR> cRDtos = new HashSet<>();
        DetailedCR detailedCR = new DetailedCR();
        detailedCR.setName("Test Name");
        detailedCR.setBrand("Test Brand");
        detailedCR.setQuantity(5);
        cRDtos.add(detailedCR);

        ComponentResourceEntity componentResourceEntity = new ComponentResourceEntity();
        componentResourceEntity.setId(1);
        when(componentResourceDao.findCREntityByNameAndBrand(detailedCR.getName(), detailedCR.getBrand())).thenReturn(componentResourceEntity);

        // Act
        Map<Integer, Integer> result = componentResourceBean.findEntityAndSetQuantity(cRDtos);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey(componentResourceEntity.getId()));
        assertEquals(detailedCR.getQuantity(), result.get(componentResourceEntity.getId()));
    }

    /**
     * Test the deleteComponentResource method of ComponentResourceBean
     * Failure case
     */
    @Test
    public void testFindEntityAndSetQuantity_Failure() {
        // Arrange
        Set<DetailedCR> cRDtos = null;

        // Act
        Map<Integer, Integer> result = componentResourceBean.findEntityAndSetQuantity(cRDtos);

        // Assert
        assertNull(result);
    }

    /**
     * Test the deleteComponentResource method of ComponentResourceBean
     * Success case
     */
    @Test
    public void testRelationInProjectCreation_Success() {
        // Arrange
        Map<Integer, Integer> componentResources = new HashMap<>();
        componentResources.put(1, 5);

        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setId(1);

        ComponentResourceEntity componentResourceEntity = new ComponentResourceEntity();
        componentResourceEntity.setId(1);
        when(componentResourceDao.findCREntityById(1)).thenReturn(componentResourceEntity);
        when(dataValidator.isIdValid(1)).thenReturn(true);

        // Act
        Set<M2MComponentProject> result = componentResourceBean.relationInProjectCreation(componentResources, projectEntity);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Test the deleteComponentResource method of ComponentResourceBean
     * Failure case
     */
    @Test
    public void testRelationInProjectCreation_Failure() {
        // Arrange
        Map<Integer, Integer> componentResources = new HashMap<>();
        componentResources.put(1, 5);

        ProjectEntity projectEntity = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            componentResourceBean.relationInProjectCreation(componentResources, projectEntity);
        });
    }

    /**
     * Test the deleteComponentResource method of ComponentResourceBean
     * Success case
     */
    @Test
    public void testComponentProjectToCRPreview_Success() {
        // Arrange
        Set<M2MComponentProject> m2MComponentProjects = new HashSet<>();
        M2MComponentProject m2MComponentProject = new M2MComponentProject();
        ComponentResourceEntity componentResourceEntity = new ComponentResourceEntity();
        componentResourceEntity.setId(1);
        m2MComponentProject.setComponentResource(componentResourceEntity);
        m2MComponentProjects.add(m2MComponentProject);

        // Act
        Set<CRPreview> result = componentResourceBean.componentProjectToCRPreview(m2MComponentProjects);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Test the deleteComponentResource method of ComponentResourceBean
     * Failure case
     */
    @Test
    public void testComponentProjectToCRPreview_Failure() {
        // Arrange
        Set<M2MComponentProject> m2MComponentProjects = null;

        // Act
        Set<CRPreview> result = componentResourceBean.componentProjectToCRPreview(m2MComponentProjects);

        // Assert
        assertNull(result);
    }

    /**
     * Test the deleteComponentResource method of ComponentResourceBean
     * Success case
     */
    @Test
    public void testExtractCRDtos_Success() throws IOException {
        // Arrange
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        Map<String, List<InputPart>> formDataMap = new HashMap<>();
        List<InputPart> inputParts = new ArrayList<>();
        InputPart inputPart = mock(InputPart.class);
        inputParts.add(inputPart);
        formDataMap.put("components", inputParts);
        when(input.getFormDataMap()).thenReturn(formDataMap);
        when(inputPart.getBodyAsString()).thenReturn("[{\"name\":\"Test Name\",\"brand\":\"Test Brand\"}]");

        // Act
        Set<DetailedCR> result = componentResourceBean.extractCRDtos(input);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Test the deleteComponentResource method of ComponentResourceBean
     * Failure case
     */
    @Test
    public void testExtractCRDtos_Failure() throws IOException {
        // Arrange
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        Map<String, List<InputPart>> formDataMap = new HashMap<>();
        when(input.getFormDataMap()).thenReturn(formDataMap);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            componentResourceBean.extractCRDtos(input);
        });
    }
}
