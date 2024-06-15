/*
import domcast.finalprojbackend.bean.ComponentResourceBean;
import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.dao.ComponentResourceDao;
import domcast.finalprojbackend.dao.ProjectDao;
import domcast.finalprojbackend.dto.componentResourceDto.CRPreview;
import domcast.finalprojbackend.dto.componentResourceDto.DetailedCR;
import domcast.finalprojbackend.entity.ComponentResourceEntity;
import domcast.finalprojbackend.entity.ProjectEntity;
import domcast.finalprojbackend.enums.ComponentResourceEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateComponentResource_Success() {
        // Arrange
        DetailedCR detailedCR = new DetailedCR();
        detailedCR.setName("Test Name");
        detailedCR.setBrand("Test Brand");
        detailedCR.setType(ComponentResourceEnum.COMPONENT.getId());
        CRPreview crPreview = new CRPreview();
        crPreview.setName(detailedCR.getName());
        crPreview.setBrand(detailedCR.getBrand());

        ComponentResourceEntity mockEntity = new ComponentResourceEntity();
        // Set the properties of mockEntity as needed
        when(componentResourceDao.findCREntityByNameAndBrand(anyString(), anyString())).thenReturn(mockEntity);

        // Act
        CRPreview result = componentResourceBean.createComponentResource(detailedCR);

        // Assert
        assertNotNull(result);
        assertEquals(detailedCR.getName(), result.getName());
        assertEquals(detailedCR.getBrand(), result.getBrand());
    }

    */
/*@Test
    public void testCreateComponentResource_Failure() {
        // Arrange
        DetailedCR detailedCR = new DetailedCR();
        detailedCR.setName("Test Name");
        detailedCR.setBrand("Test Brand");
        detailedCR.setType(ComponentResourceEnum.COMPONENT.getId());
        when(dataValidator.isCRMandatoryDataValid(detailedCR)).thenReturn(false);

        // Act
        CRPreview result = componentResourceBean.createComponentResource(detailedCR);

        // Assert
        assertNull(result);
    }
*//*


    @Test
    public void testRegisterData_Success() {
        // Arrange
        DetailedCR detailedCR = new DetailedCR();
        detailedCR.setName("Test Name");
        detailedCR.setBrand("Test Brand");
        detailedCR.setType(ComponentResourceEnum.COMPONENT.getId());
        when(componentResourceDao.doesCRExistByNameAndBrand(anyString(), anyString())).thenReturn(false);
        when(componentResourceDao.findCREntityByNameAndBrand(anyString(), anyString())).thenReturn(new ComponentResourceEntity());

        // Act
        ComponentResourceEntity result = componentResourceBean.registerData(detailedCR);

        // Assert
        assertNotNull(result);
        assertEquals("Test Name", result.getName());
        assertEquals("Test Brand", result.getBrand());
    }

    @Test
    public void testRegisterData_Failure() {
        // Arrange
        DetailedCR detailedCR = new DetailedCR();
        detailedCR.setName("Test Name");
        detailedCR.setBrand("Test Brand");
        detailedCR.setType(ComponentResourceEnum.COMPONENT.getId());
        when(componentResourceDao.doesCRExistByNameAndBrand(anyString(), anyString())).thenReturn(true);

        // Act
        ComponentResourceEntity result = componentResourceBean.registerData(detailedCR);

        // Assert
        assertNull(result);
    }

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

    @Test
    public void testDetailedToPreviewCR_Success() {
        // Arrange
        DetailedCR detailedCR = new DetailedCR();
        detailedCR.setName("Test Name");
        detailedCR.setBrand("Test Brand");

        // Act
        CRPreview result = componentResourceBean.detailedToPreviewCR(detailedCR);

        // Assert
        assertNotNull(result);
        assertEquals("Test Name", result.getName());
        assertEquals("Test Brand", result.getBrand());
    }

    @Test
    public void testDetailedToPreviewCR_Failure() {
        // Arrange
        DetailedCR detailedCR = null;

        // Act
        CRPreview result = componentResourceBean.detailedToPreviewCR(detailedCR);

        // Assert
        assertNull(result);
    }
}*/
