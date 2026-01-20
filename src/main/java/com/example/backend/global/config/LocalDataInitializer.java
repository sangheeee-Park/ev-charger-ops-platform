package com.example.backend.global.config;

import com.example.backend.analysis.entity.ChargerAnalysis;
import com.example.backend.analysis.entity.MultimodalAnalysis;
import com.example.backend.analysis.repository.ChargerAnalysisRepository;
import com.example.backend.analysis.repository.MultimodalAnalysisRepository;
import com.example.backend.chargingstation.entity.Agency;
import com.example.backend.chargingstation.entity.Charger;
import com.example.backend.chargingstation.entity.ChargerLog;
import com.example.backend.chargingstation.entity.ChargingStation;
import com.example.backend.chargingstation.entity.ImageLog;
import com.example.backend.chargingstation.entity.RegionCode;
import com.example.backend.chargingstation.entity.RegionDetailCode;
import com.example.backend.chargingstation.entity.SensorLog;
import com.example.backend.chargingstation.repository.AgencyRepository;
import com.example.backend.chargingstation.repository.ChargerLogRepository;
import com.example.backend.chargingstation.repository.ChargerRepository;
import com.example.backend.chargingstation.repository.ChargingStationRepository;
import com.example.backend.chargingstation.repository.ImageLogRepository;
import com.example.backend.chargingstation.repository.RegionCodeRepository;
import com.example.backend.chargingstation.repository.RegionDetailCodeRepository;
import com.example.backend.chargingstation.repository.SensorLogRepository;
import com.example.backend.notification.entity.ExternalNotification;
import com.example.backend.notification.repository.ExternalNotificationRepository;
import com.example.backend.qna.entity.OfficialDocument;
import com.example.backend.qna.repository.OfficialDocumentRepository;
import com.example.backend.report.entity.Report;
import com.example.backend.report.repository.ReportRepository;
import com.example.backend.request.entity.Request;
import com.example.backend.request.entity.RequestStatus;
import com.example.backend.request.entity.RequestType;
import com.example.backend.request.repository.RequestRepository;
import com.example.backend.requestoutbound.entity.RequestOutbound;
import com.example.backend.requestoutbound.repository.RequestOutboundRepository;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalDataInitializer implements CommandLineRunner {

    private final RegionCodeRepository regionCodeRepository;
    private final RegionDetailCodeRepository regionDetailCodeRepository;
    private final AgencyRepository agencyRepository;
    private final ChargingStationRepository chargingStationRepository;
    private final ChargerRepository chargerRepository;
    private final ChargerLogRepository chargerLogRepository;
    private final SensorLogRepository sensorLogRepository;
    private final ImageLogRepository imageLogRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final RequestOutboundRepository requestOutboundRepository;
    private final OfficialDocumentRepository officialDocumentRepository;
    private final ChargerAnalysisRepository chargerAnalysisRepository;
    private final MultimodalAnalysisRepository multimodalAnalysisRepository;
    private final ReportRepository reportRepository;
    private final ExternalNotificationRepository externalNotificationRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (regionCodeRepository.count() > 0 || chargingStationRepository.count() > 0) {
            return;
        }

        seedChargingStationData();
        seedUserData();
        seedRequestData();
        seedDocumentsAndAnalysis();
        seedReportsAndNotifications();
    }

    private void seedChargingStationData() {
        RegionCode seoul = withFields(new RegionCode(),
                "zcode", "11",
                "zcodeDescription", "서울");
        RegionCode busan = withFields(new RegionCode(),
                "zcode", "26",
                "zcodeDescription", "부산");
        regionCodeRepository.saveAll(List.of(seoul, busan));

        RegionDetailCode seoulJunggu = withFields(new RegionDetailCode(),
                "zscode", "11000",
                "zscodeDescription", "중구");
        RegionDetailCode busanJunggu = withFields(new RegionDetailCode(),
                "zscode", "26000",
                "zscodeDescription", "중구");
        regionDetailCodeRepository.saveAll(List.of(seoulJunggu, busanJunggu));

        Agency kepco = withFields(new Agency(),
                "busiId", "A1",
                "busidDescription", "한국전력");
        Agency privateAgency = withFields(new Agency(),
                "busiId", "B2",
                "busidDescription", "민간운영사");
        agencyRepository.saveAll(List.of(kepco, privateAgency));

        ChargingStation seoulStation = withFields(new ChargingStation(),
                "statId", "ST000001",
                "zcode", "11",
                "zscode", "11000",
                "busiId", "A1",
                "statNm", "서울역 급속충전소",
                "addr", "서울특별시 중구 세종대로 1",
                "lat", 37.555,
                "lng", 126.972,
                "busiCall", "02-1234-5678",
                "note", "24시간 운영",
                "year", 2022);
        ChargingStation busanStation = withFields(new ChargingStation(),
                "statId", "ST000002",
                "zcode", "26",
                "zscode", "26000",
                "busiId", "B2",
                "statNm", "부산역 완속충전소",
                "addr", "부산광역시 중구 중앙대로 5",
                "lat", 35.116,
                "lng", 129.040,
                "busiCall", "051-987-6543",
                "note", "주차장 내 설치",
                "year", 2021);
        chargingStationRepository.saveAll(List.of(seoulStation, busanStation));

        Charger seoulCharger1 = withFields(new Charger(),
                "chgerId", "01",
                "statId", "ST000001",
                "chgerType", "DC",
                "output", "100",
                "method", "카드");
        Charger seoulCharger2 = withFields(new Charger(),
                "chgerId", "02",
                "statId", "ST000001",
                "chgerType", "DC",
                "output", "50",
                "method", "앱");
        Charger busanCharger = withFields(new Charger(),
                "chgerId", "01",
                "statId", "ST000002",
                "chgerType", "AC",
                "output", "7",
                "method", "카드");
        chargerRepository.saveAll(List.of(seoulCharger1, seoulCharger2, busanCharger));

        LocalDateTime baseTime = LocalDateTime.of(2025, 1, 10, 10, 0, 0);
        ChargerLog chargerLog1 = withFields(new ChargerLog(),
                "chgerTime", baseTime,
                "chgerId", "01",
                "statId", "ST000001",
                "lastTsdt", baseTime.minusHours(1),
                "lastTedt", baseTime.minusMinutes(20),
                "statUpdDt", baseTime.plusMinutes(5),
                "stat", 2);
        ChargerLog chargerLog2 = withFields(new ChargerLog(),
                "chgerTime", baseTime.plusHours(1),
                "chgerId", "02",
                "statId", "ST000001",
                "lastTsdt", baseTime.plusMinutes(10),
                "lastTedt", baseTime.plusMinutes(50),
                "statUpdDt", baseTime.plusHours(1).plusMinutes(3),
                "stat", 1);
        chargerLogRepository.saveAll(List.of(chargerLog1, chargerLog2));

        SensorLog sensorLog1 = withFields(new SensorLog(),
                "sensorTime", baseTime,
                "chgerId", "01",
                "statId", "ST000001",
                "totalChargingKwh", 12.5,
                "totalChargingMin", 35,
                "currentSoc", 65,
                "currentEnergyMeterValue", 45.3,
                "chargingv", 380.0,
                "charginga", 125.0,
                "outPower", 47.5,
                "chargingGunTemperature1", 42,
                "chargingGunTemperature2", 41,
                "types", 1);
        SensorLog sensorLog2 = withFields(new SensorLog(),
                "sensorTime", baseTime.plusHours(1),
                "chgerId", "02",
                "statId", "ST000001",
                "totalChargingKwh", 8.2,
                "totalChargingMin", 28,
                "currentSoc", 54,
                "currentEnergyMeterValue", 30.1,
                "chargingv", 360.0,
                "charginga", 110.0,
                "outPower", 39.6,
                "chargingGunTemperature1", 40,
                "chargingGunTemperature2", 39,
                "types", 2);
        sensorLogRepository.saveAll(List.of(sensorLog1, sensorLog2));

        ImageLog imageLog1 = withFields(new ImageLog(),
                "imgId", 1001L,
                "imgTime", baseTime,
                "imgPath", "/images/st000001_1001.jpg",
                "statId", "ST000001");
        ImageLog imageLog2 = withFields(new ImageLog(),
                "imgId", 1002L,
                "imgTime", baseTime.plusHours(1),
                "imgPath", "/images/st000001_1002.jpg",
                "statId", "ST000001");
        imageLogRepository.saveAll(List.of(imageLog1, imageLog2));
    }

    private void seedUserData() {
        User user1 = User.builder()
                .employeeNum("EMP-001")
                .password("{noop}pass1234")
                .username("홍길동")
                .department("운영팀")
                .build();
        User user2 = User.builder()
                .employeeNum("EMP-002")
                .password("{noop}pass5678")
                .username("김영희")
                .department("관리팀")
                .build();
        userRepository.saveAll(List.of(user1, user2));
    }

    private void seedRequestData() {
        Request request1 = Request.builder()
                .chgerId("01")
                .statId("ST000001")
                .title("충전 케이블 점검 요청")
                .content("케이블 마모가 있어 교체가 필요합니다.")
                .reqType(RequestType.REPAIR)
                .reqDt(LocalDateTime.of(2025, 1, 10, 12, 0))
                .status(RequestStatus.PENDING)
                .build();
        Request request2 = Request.builder()
                .chgerId("01")
                .statId("ST000002")
                .title("요금 문의")
                .content("완속 충전 요금 체계를 알려주세요.")
                .reqType(RequestType.INQUIRY)
                .reqDt(LocalDateTime.of(2025, 1, 10, 12, 30))
                .status(RequestStatus.IN_PROGRESS)
                .build();
        requestRepository.saveAll(List.of(request1, request2));
        requestRepository.flush();

        RequestOutbound outbound1 = RequestOutbound.builder()
                .answer("케이블 교체 일정 확인 후 회신드리겠습니다.")
                .answerDt(LocalDateTime.of(2025, 1, 10, 13, 0))
                .reqId(request1.getReqId())
                .build();
        RequestOutbound outbound2 = RequestOutbound.builder()
                .answer("완속 충전 요금은 1kWh당 250원입니다.")
                .answerDt(LocalDateTime.of(2025, 1, 10, 13, 10))
                .reqId(request2.getReqId())
                .build();
        requestOutboundRepository.saveAll(List.of(outbound1, outbound2));
    }

    private void seedDocumentsAndAnalysis() {
        OfficialDocument doc1 = new OfficialDocument(501, "충전소 안전 점검 매뉴얼", "한국전력",
                LocalDateTime.of(2024, 12, 1, 9, 0), "/docs/safety_manual.pdf");
        OfficialDocument doc2 = new OfficialDocument(502, "충전기 유지보수 지침", "환경부",
                LocalDateTime.of(2024, 11, 15, 9, 0), "/docs/maintenance_guideline.pdf");
        officialDocumentRepository.saveAll(List.of(doc1, doc2));

        ChargerAnalysis analysis1 = ChargerAnalysis.builder()
                .unconfMin(5)
                .build();
        ChargerAnalysis analysis2 = ChargerAnalysis.builder()
                .unconfMin(12)
                .build();
        chargerAnalysisRepository.saveAll(List.of(analysis1, analysis2));

        MultimodalAnalysis multimodal1 = MultimodalAnalysis.builder()
                .fireYn(false)
                .fireDetails("열화상 이상 없음")
                .brokeYn(false)
                .brokeDetails("외관 파손 없음")
                .cleanYn(true)
                .cleanDetails("정상 청결 상태")
                .imgsensoranalTime(LocalDateTime.of(2025, 1, 10, 10, 5))
                .imgId(1001L)
                .imgTime(LocalDateTime.of(2025, 1, 10, 10, 0))
                .sensorTime(LocalDateTime.of(2025, 1, 10, 10, 0))
                .chgerId2("01")
                .statId2("ST000001")
                .build();
        MultimodalAnalysis multimodal2 = MultimodalAnalysis.builder()
                .fireYn(false)
                .fireDetails("연기 감지 없음")
                .brokeYn(true)
                .brokeDetails("충전 커넥터 손상 감지")
                .cleanYn(false)
                .cleanDetails("먼지 축적 확인")
                .imgsensoranalTime(LocalDateTime.of(2025, 1, 10, 11, 5))
                .imgId(1002L)
                .imgTime(LocalDateTime.of(2025, 1, 10, 11, 0))
                .sensorTime(LocalDateTime.of(2025, 1, 10, 11, 0))
                .chgerId2("02")
                .statId2("ST000001")
                .build();
        multimodalAnalysisRepository.saveAll(List.of(multimodal1, multimodal2));
    }

    private void seedReportsAndNotifications() {
        Report report1 = Report.builder()
                .reportTitle("주간 충전기 운영 리포트")
                .createdTime(LocalDateTime.of(2025, 1, 10, 14, 0))
                .filePath("/reports/weekly_report.pdf")
                .reportType("WEEKLY")
                .prompt("주간 운영 현황 요약")
                .dataStartTime(LocalDateTime.of(2025, 1, 3, 0, 0))
                .dataEndTime(LocalDateTime.of(2025, 1, 9, 23, 59, 59))
                .build();
        Report report2 = Report.builder()
                .reportTitle("장애 분석 리포트")
                .createdTime(LocalDateTime.of(2025, 1, 10, 15, 0))
                .filePath("/reports/failure_report.pdf")
                .reportType("INCIDENT")
                .prompt("최근 장애 분석")
                .dataStartTime(LocalDateTime.of(2025, 1, 8, 0, 0))
                .dataEndTime(LocalDateTime.of(2025, 1, 10, 12, 0))
                .build();
        reportRepository.saveAll(List.of(report1, report2));

        ExternalNotification notification1 = ExternalNotification.builder()
                .message("충전기 온도 경고가 감지되었습니다.")
                .testMail("ops@example.com")
                .chgerTime(LocalDateTime.of(2025, 1, 10, 10, 7))
                .chgerId("01")
                .statId("ST000001")
                .build();
        ExternalNotification notification2 = ExternalNotification.builder()
                .message("충전 중단 알림이 발생했습니다.")
                .testMail("support@example.com")
                .chgerTime(LocalDateTime.of(2025, 1, 10, 11, 8))
                .chgerId("02")
                .statId("ST000001")
                .build();
        externalNotificationRepository.saveAll(List.of(notification1, notification2));
    }

    private <T> T withFields(T target, Object... fieldsAndValues) {
        if (fieldsAndValues.length % 2 != 0) {
            throw new IllegalArgumentException("Fields and values must be paired.");
        }
        for (int i = 0; i < fieldsAndValues.length; i += 2) {
            String fieldName = (String) fieldsAndValues[i];
            Object value = fieldsAndValues[i + 1];
            Field field = ReflectionUtils.findField(target.getClass(), fieldName);
            if (field == null) {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            }
            field.setAccessible(true);
            ReflectionUtils.setField(field, target, value);
        }
        return target;
    }
}
