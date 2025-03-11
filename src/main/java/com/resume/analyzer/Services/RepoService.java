package com.resume.analyzer.Services;

import com.resume.analyzer.Model.Settings;
import com.resume.analyzer.Repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepoService {

    @Autowired
    private SettingsRepository settingsRepository;

    public Settings getSettings(){
        return settingsRepository.findById(1);
    }

    public void save(Settings settings){
        settings.setId(1);
        settingsRepository.save(settings);
    }
}
