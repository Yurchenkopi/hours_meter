package ru.yurch.hours.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yurch.hours.model.ReportSetting;
import ru.yurch.hours.repository.ReportSettingRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ReportSettingService {
    private final ReportSettingRepository reportSettingRepository;

    private static final Logger LOG = LoggerFactory.getLogger(ReportSettingService.class.getName());


    public Optional<ReportSetting> save(ReportSetting reportSetting) {
        Optional<ReportSetting> rsl = Optional.empty();
        try {
            rsl = Optional.of(reportSettingRepository.save(reportSetting));
        } catch (Exception e) {
            LOG.error("Error occurred while saving reportSetting:  " + e.getMessage());
        }
        return rsl;
    }

    public boolean update(ReportSetting reportSetting) {
        boolean rsl = false;
        Optional<ReportSetting> currentReportSetting = reportSettingRepository.findById(reportSetting.getId());
        LOG.info("Текущая настройка отчета: " + currentReportSetting);
        LOG.info("Сохраняемая настройка отчёта: " + reportSetting);
        if (currentReportSetting.isPresent()) {
            if (!currentReportSetting.get().equals(reportSetting)) {
                try {
                    reportSettingRepository.save(reportSetting);
                    rsl = true;
                } catch (Exception e) {
                    LOG.error("Произошла ошибка при обновлении записи в БД: " + e.getMessage());
                }
            }
        }
        return rsl;
    }
}
