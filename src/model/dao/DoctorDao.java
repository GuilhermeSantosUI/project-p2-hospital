package model.dao;

import java.util.List;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.entities.Doctor;

public interface DoctorDao {

	void insert(Doctor obj);

	List<Doctor> findAll();

	Doctor findBusyDoctor();

	void handleLogin(TextField crmDoctor, PasswordField passDoctor);

}
