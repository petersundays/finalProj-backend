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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
        detailedCR.setSupplierContact(123456789);
        Integer projectId = 1; // Ensure this is greater than 0
        Integer quantity = 1; // Ensure this is greater than 0

        ComponentResourceEntity mockEntity = new ComponentResourceEntity();
        mockEntity.setName(detailedCR.getName());
        mockEntity.setBrand(detailedCR.getBrand());
        when(componentResourceDao.findCREntityByNameAndBrand(anyString(), anyString())).thenReturn(mockEntity);

        // Act
        CRPreview result = componentResourceBean.createComponentResource(detailedCR, projectId, quantity);

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
        when(dataValidator.isCRMandatoryDataValid(detailedCR, null, null)).thenReturn(false);

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
        Integer projectId = 1;
        Integer quantity = 1;
        when(componentResourceDao.doesCRExistByNameAndBrand(anyString(), anyString())).thenReturn(false);
        when(componentResourceDao.findCREntityByNameAndBrand(anyString(), anyString())).thenReturn(new ComponentResourceEntity());

        // Act
        ComponentResourceEntity result = componentResourceBean.registerData(detailedCR, projectId, quantity);

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
        Integer projectId = 1;
        Integer quantity = 1;
        when(componentResourceDao.doesCRExistByNameAndBrand(anyString(), anyString())).thenReturn(true);

        // Act
        ComponentResourceEntity result = componentResourceBean.registerData(detailedCR, projectId, quantity);

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
}
