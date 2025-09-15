package ru.yurch.hours.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yurch.hours.model.ReportSetting;

@Repository
public interface ReportSettingRepository extends CrudRepository<ReportSetting, Integer> {

    }
