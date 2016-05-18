package container;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import bean.Fox;

public class WinterTest {
	String message;
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void testCreateTeacher() {
		message = "When CreateTeacher is triggered with all data provided, "
				+ "then it should be created and added to the list";
		
		Winter winter = new Winter("bean");
		
//		assertEquals(message, 1, department.getTeachers().size());
	}
	
//	@Test(expected = ObjectExistException.class)
//	public void testCreateTeacherThatExists() throws ObjectExistException, InvalidArgumentException {
//		message = "When CreateTeacher is triggered with passport of existing teacher, "
//				+ "then it should throw ObjectExistException";
//		
//		department.createTeacher("Rick", "Rest", "taxpayerId", "DET982");
//		department.createTeacher("Rick", "Rest", "taxpayerId", "DET982");
//	}
	
}
