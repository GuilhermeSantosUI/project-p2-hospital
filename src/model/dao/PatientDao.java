package model.dao;

import java.util.List;

import model.entities.Patient;

public interface PatientDao {
	
	void insert(Patient obj);
		
	List<Patient> findAll();
	
	void update(Patient obj);

	void deleteById(Integer id);

}
