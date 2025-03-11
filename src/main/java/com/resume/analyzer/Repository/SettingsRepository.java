package com.resume.analyzer.Repository;


import com.resume.analyzer.Model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Integer> {

    Settings findById(int id);
}