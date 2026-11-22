package com.fqq.futurecommunity.community.service.impl;

import com.fqq.futurecommunity.common.constant.HttpStatus;
import com.fqq.futurecommunity.common.core.exception.CustomException;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import com.fqq.futurecommunity.community.domain.Community;
import com.fqq.futurecommunity.community.domain.vo.AnalysisTextRow;
import com.fqq.futurecommunity.community.domain.vo.CommunityRankingVo;
import com.fqq.futurecommunity.community.domain.vo.ComplaintWordCloudVo;
import com.fqq.futurecommunity.community.domain.vo.ParkingHourlyVo;
import com.fqq.futurecommunity.community.domain.vo.ParkingPeakVo;
import com.fqq.futurecommunity.community.domain.vo.ParkingPeriodRow;
import com.fqq.futurecommunity.community.domain.vo.ParkingTrendVo;
import com.fqq.futurecommunity.community.domain.vo.TfidfAnalysisVo;
import com.fqq.futurecommunity.community.domain.vo.TfidfKeywordVo;
import com.fqq.futurecommunity.community.domain.vo.WordCloudItemVo;
import com.fqq.futurecommunity.community.mapper.AnalysisMapper;
import com.fqq.futurecommunity.community.mapper.CommunityMapper;
import com.fqq.futurecommunity.community.service.AnalysisService;
import com.fqq.futurecommunity.system.domain.LoginUser;
import com.huaban.analysis.jieba.JiebaSegmenter;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    private static final int WORD_LIMIT = 80;
    private static final int DEFAULT_TFIDF_LIMIT = 30;
    private static final int MAX_TFIDF_LIMIT = 100;
    private static final int DEFAULT_RANKING_DAYS = 30;
    private static final int MIN_RANKING_DAYS = 1;
    private static final int MAX_RANKING_DAYS = 365;
    private static final int RANKING_SCALE = 4;
    // private static final double[] TOPSIS_WEIGHTS = {0.15D, 0.15D, 0.18D, 0.17D, 0.17D, 0.18D};
    private static final double[] TOPSIS_WEIGHTS = {0.15D, 0.15D, 0.18D, 0.18D, 0.18D, 0.16D};
    private static final Pattern TOKEN_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]+|[A-Za-z0-9]+");

    private final JiebaSegmenter jiebaSegmenter = new JiebaSegmenter();

    private Set<String> stopWords = Set.of();

    @Resource
    private AnalysisMapper analysisMapper;

    @Resource
    private CommunityMapper communityMapper;

    @PostConstruct
    public void initStopWords() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("cn_stop_words.txt")) {
            if (inputStream == null) {
                stopWords = Set.of();
                return;
            }
            stopWords = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .map(String::trim)
                    .filter(StringUtils::isNotBlank)
                    .filter(word -> !word.startsWith("#"))
                    .map(this::normalizeWord)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            stopWords = Set.of();
        }
    }

    @Override
    public ComplaintWordCloudVo getComplaintWordCloud(Long communityId, String beginTime, String endTime) {
        if (StringUtils.isBlank(beginTime)) {
            beginTime = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE);
        }
        if (StringUtils.isBlank(endTime)) {
            endTime = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        }

        LocalDate beginDate = parseDate(beginTime);
        LocalDate endDate = parseDate(endTime);
        if (beginDate.isAfter(endDate)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "开始时间不能晚于结束时间");
        }

        checkRequiredCommunityPermission(communityId);

        LocalDateTime begin = beginDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();
        List<AnalysisTextRow> rows = analysisMapper.selectComplaintSuggestTexts(communityId, begin, end);

        ComplaintWordCloudVo vo = new ComplaintWordCloudVo();
        vo.setCommunityId(communityId);
        vo.setBeginTime(beginDate.format(DateTimeFormatter.BASIC_ISO_DATE));
        vo.setEndTime(endDate.format(DateTimeFormatter.BASIC_ISO_DATE));
        vo.setComplaintWords(buildWordCloud(rows, "Complaint"));
        vo.setSuggestWords(buildWordCloud(rows, "Suggest"));
        return vo;
    }


    @Override
    public TfidfAnalysisVo getComplaintTfidf(Long communityId, String beginTime, String endTime, String type, Integer topN) {
        if (StringUtils.isBlank(beginTime)) {
            beginTime = LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE);
        }
        if (StringUtils.isBlank(endTime)) {
            endTime = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        }

        LocalDate beginDate = parseDate(beginTime);
        LocalDate endDate = parseDate(endTime);
        if (beginDate.isAfter(endDate)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "开始时间不能晚于结束时间");
        }

        checkRequiredCommunityPermission(communityId);

        LocalDateTime begin = beginDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();
        String normalizedType = normalizeComplaintSuggestType(type);
        List<AnalysisTextRow> rows = analysisMapper.selectComplaintSuggestTexts(communityId, begin, end);

        TfidfAnalysisVo vo = new TfidfAnalysisVo();
        vo.setCommunityId(communityId);
        vo.setType(normalizedType);
        vo.setBeginTime(beginDate.format(DateTimeFormatter.BASIC_ISO_DATE));
        vo.setEndTime(endDate.format(DateTimeFormatter.BASIC_ISO_DATE));
        vo.setKeywords(buildTfidfKeywords(rows, normalizedType, normalizeTfidfLimit(topN)));
        vo.setTotalDocumentCount(countTfidfDocuments(rows, normalizedType));
        return vo;
    }

    @Override
    public ParkingPeakVo getParkingPeak(Long communityId, String date) {
        if (StringUtils.isBlank(date)) {
            date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        }
        LocalDate targetDate = parseDate(date);

        checkCommunityPermission(communityId);

        Long parkingCount = analysisMapper.countParkingSpace(communityId);
        if (parkingCount == null) {
            parkingCount = 0L;
        }

        LocalDateTime begin = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay();
        List<ParkingPeriodRow> periods = analysisMapper.selectParkingPeriods(communityId, begin, end);
        List<ParkingHourlyVo> hourlyData = buildHourlyData(periods, targetDate, parkingCount);

        ParkingHourlyVo peak = hourlyData.stream()
                .max(Comparator.comparing(ParkingHourlyVo::getCount))
                .orElse(null);
        String peakHour = peak == null || peak.getCount() == null || peak.getCount() <= 0
                ? ""
                : peak.getHour() + "-" + nextHour(peak.getHour());
        Integer peakCount = peak == null ? 0 : peak.getCount();
        BigDecimal pressureRate = calculatePressureRate(peakCount, parkingCount);
        String pressureLevel = resolvePressureLevel(pressureRate, parkingCount);

        long fixedCarCount = periods.stream()
                .filter(row -> row != null && isFixedParkingRecord(row.getRecordType()))
                .count();
        long tempCarCount = periods.stream()
                .filter(row -> row != null && isTempParkingRecord(row.getRecordType()))
                .count();
        long typedRecordCount = fixedCarCount + tempCarCount;

        LocalDate beginDate = targetDate.minusDays(6);
        List<ParkingTrendVo> trendRows = buildPresenceTrend7Days(communityId, beginDate, targetDate, parkingCount);

        ParkingPeakVo vo = new ParkingPeakVo();
        vo.setCommunityId(communityId);
        vo.setDate(targetDate.format(DateTimeFormatter.BASIC_ISO_DATE));
        vo.setParkingCount(parkingCount);
        vo.setPeakHour(peakHour);
        vo.setPeakCount(peakCount);
        vo.setPressureRate(pressureRate);
        vo.setPressureLevel(pressureLevel);
        vo.setPressureText(resolvePressureText(pressureLevel, parkingCount));
        vo.setFixedCarCount(Math.toIntExact(fixedCarCount));
        vo.setTempCarCount(Math.toIntExact(tempCarCount));
        vo.setFixedCarRate(calculateRate(fixedCarCount, typedRecordCount));
        vo.setTempCarRate(calculateRate(tempCarCount, typedRecordCount));
        vo.setHourlyData(hourlyData);
        vo.setLast7Days(trendRows);
        return vo;
    }


    @Override
    public List<CommunityRankingVo> getCommunityRanking(Long communityId, Integer days) {
        int periodDays = normalizeRankingDays(days);
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime begin = end.minusDays(periodDays);

        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long deptId = null;
        if (loginUser == null || loginUser.getUser() == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "未登录");
        }
        if (!loginUser.getUser().isAdmin()) {
            deptId = loginUser.getUser().getDeptId();
        }

        List<CommunityRankingVo> rows = analysisMapper.selectRankingCommunityBases(communityId, deptId);
        for (CommunityRankingVo row : rows) {
            fillRankingIndicators(row, begin, end, periodDays);
        }
        fillTopsisScore(rows);
        rows.sort((a, b) -> b.getScorePercent().compareTo(a.getScorePercent()));
        for (int i = 0; i < rows.size(); i++) {
            CommunityRankingVo row = rows.get(i);
            row.setRank(i + 1);
            fillRankingLevel(row);
        }
        return rows;
    }


    @Override
    public List<Map<String, Object>> getCommunityRankingIndicators() {
        List<Map<String, Object>> indicators = new ArrayList<>();
        indicators.add(buildRankingIndicator("occupancyRate", "入住率", "已入住房屋数 / 房屋总数", TOPSIS_WEIGHTS[0], "room", "反映小区房屋资源使用情况"));
        indicators.add(buildRankingIndicator("paymentRate", "缴费率", "已缴账单数 / 有效账单数", TOPSIS_WEIGHTS[1], "community_fee_bill", "反映物业收费管理情况"));
        indicators.add(buildRankingIndicator("deviceHealth", "设备健康度", "非异常设备数 / 设备总数", TOPSIS_WEIGHTS[2], "community_device", "反映公共设备运行状态"));
        indicators.add(buildRankingIndicator("repairHealth", "报修健康度", "非待处理报修工单数 / 报修工单总数", TOPSIS_WEIGHTS[3], "community_repair_order", "反映维修响应和服务处理情况"));
        indicators.add(buildRankingIndicator("complaintHealth", "投诉健康度", "1 - 投诉建议数量 / max(评价天数 × 5, 1)", TOPSIS_WEIGHTS[4], "complaint_suggest", "反映业主反馈压力和服务问题集中程度"));
        indicators.add(buildRankingIndicator("parkingHealth", "停车健康度", "1 - 停车记录数量 / max(车位数量 × 评价天数, 1)", TOPSIS_WEIGHTS[5], "community_parking_space、community_parking_record", "反映停车资源使用压力"));
        return indicators;
    }

    private Map<String, Object> buildRankingIndicator(String field, String name, String formula, double weight, String dataSource, String usage) {
        Map<String, Object> indicator = new HashMap<>();
        indicator.put("field", field);
        indicator.put("name", name);
        indicator.put("attribute", "正向指标");
        indicator.put("formula", formula);
        indicator.put("calculation", formula);
        indicator.put("weight", BigDecimal.valueOf(weight).setScale(2, RoundingMode.HALF_UP));
        indicator.put("dataSource", dataSource);
        indicator.put("usage", usage);
        indicator.put("description", usage);
        return indicator;
    }


    private int normalizeRankingDays(Integer days) {
        if (days == null) {
            return DEFAULT_RANKING_DAYS;
        }
        return Math.min(MAX_RANKING_DAYS, Math.max(MIN_RANKING_DAYS, days));
    }

    private void fillRankingIndicators(CommunityRankingVo row, LocalDateTime begin, LocalDateTime end, int periodDays) {
        Long communityId = row.getCommunityId();

        int roomCount = defaultZero(analysisMapper.countRankingRoomByCommunityId(communityId));
        int occupiedRoomCount = defaultZero(analysisMapper.countRankingOccupiedRoomByCommunityId(communityId));
        int deviceTotal = defaultZero(analysisMapper.countRankingDeviceByCommunityId(communityId));
        int deviceError = defaultZero(analysisMapper.countRankingErrorDeviceByCommunityId(communityId));

        int validFeeBillCount = defaultZero(analysisMapper.countRankingValidFeeBillByCommunityId(communityId, begin, end));
        int paidFeeBillCount = defaultZero(analysisMapper.countRankingPaidFeeBillByCommunityId(communityId, begin, end));

        int repairCount = defaultZero(analysisMapper.countRankingRepairByCommunityId(communityId, begin, end));
        int repairPending = defaultZero(analysisMapper.countRankingPendingRepairByCommunityId(communityId, begin, end));

        int complaintCount = defaultZero(analysisMapper.countRankingComplaintByCommunityId(communityId, begin, end));

        int parkingSpaceCount = defaultZero(analysisMapper.countRankingParkingSpaceByCommunityId(communityId));
        int parkingRecordCount = defaultZero(analysisMapper.countRankingParkingRecordByCommunityId(communityId, begin, end));

        BigDecimal occupancyRate = calcRate(occupiedRoomCount, roomCount);
        BigDecimal paymentRate = validFeeBillCount == 0 ? BigDecimal.ONE : calcRate(paidFeeBillCount, validFeeBillCount);
        BigDecimal deviceHealth = calcHealth(deviceTotal - deviceError, deviceTotal);
        BigDecimal repairHealth = repairCount == 0 ? BigDecimal.ONE : calcHealth(repairCount - repairPending, repairCount);
        BigDecimal complaintHealth = calcPressureHealth(complaintCount, Math.max(periodDays * 5, 1));
        BigDecimal parkingHealth = parkingSpaceCount == 0
                ? BigDecimal.ONE
                : calcPressureHealth(parkingRecordCount, Math.max(parkingSpaceCount * periodDays, 1));

        row.setOccupancyRate(occupancyRate);
        row.setPaymentRate(paymentRate);
        row.setDeviceHealth(deviceHealth);
        row.setRepairHealth(repairHealth);
        row.setComplaintHealth(complaintHealth);
        row.setParkingHealth(parkingHealth);
        row.setMainRisks(buildRankingRisks(row));
    }

    private void fillTopsisScore(List<CommunityRankingVo> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }

        double[][] matrix = new double[rows.size()][TOPSIS_WEIGHTS.length];
        for (int i = 0; i < rows.size(); i++) {
            CommunityRankingVo row = rows.get(i);
            matrix[i][0] = toDouble(row.getOccupancyRate());
            matrix[i][1] = toDouble(row.getPaymentRate());
            matrix[i][2] = toDouble(row.getDeviceHealth());
            matrix[i][3] = toDouble(row.getRepairHealth());
            matrix[i][4] = toDouble(row.getComplaintHealth());
            matrix[i][5] = toDouble(row.getParkingHealth());
        }

        double[] denominators = new double[TOPSIS_WEIGHTS.length];
        for (int j = 0; j < TOPSIS_WEIGHTS.length; j++) {
            double sum = 0D;
            for (double[] row : matrix) {
                sum += row[j] * row[j];
            }
            denominators[j] = Math.sqrt(sum);
        }

        double[][] weighted = new double[rows.size()][TOPSIS_WEIGHTS.length];
        double[] best = new double[TOPSIS_WEIGHTS.length];
        double[] worst = new double[TOPSIS_WEIGHTS.length];
        for (int j = 0; j < TOPSIS_WEIGHTS.length; j++) {
            best[j] = Double.NEGATIVE_INFINITY;
            worst[j] = Double.POSITIVE_INFINITY;
        }

        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < TOPSIS_WEIGHTS.length; j++) {
                weighted[i][j] = denominators[j] == 0D ? 0D : matrix[i][j] / denominators[j] * TOPSIS_WEIGHTS[j];
                best[j] = Math.max(best[j], weighted[i][j]);
                worst[j] = Math.min(worst[j], weighted[i][j]);
            }
        }

        for (int i = 0; i < rows.size(); i++) {
            double bestDistance = 0D;
            double worstDistance = 0D;
            for (int j = 0; j < TOPSIS_WEIGHTS.length; j++) {
                bestDistance += Math.pow(weighted[i][j] - best[j], 2D);
                worstDistance += Math.pow(weighted[i][j] - worst[j], 2D);
            }
            bestDistance = Math.sqrt(bestDistance);
            worstDistance = Math.sqrt(worstDistance);

            double closeness;
            if (bestDistance + worstDistance == 0D) {
                closeness = weightedAverage(matrix[i]);
            } else {
                closeness = worstDistance / (bestDistance + worstDistance);
            }
            rows.get(i).setScorePercent(BigDecimal.valueOf(closeness * 100D).setScale(2, RoundingMode.HALF_UP));
        }
    }

    private double weightedAverage(double[] values) {
        double result = 0D;
        for (int i = 0; i < TOPSIS_WEIGHTS.length; i++) {
            result += values[i] * TOPSIS_WEIGHTS[i];
        }
        return result;
    }

    private void fillRankingLevel(CommunityRankingVo row) {
        BigDecimal score = row.getScorePercent() == null ? BigDecimal.ZERO : row.getScorePercent();
        if (score.compareTo(new BigDecimal("80")) >= 0) {
            row.setLevel("good");
            row.setLevelName("运行良好");
        } else if (score.compareTo(new BigDecimal("60")) >= 0) {
            row.setLevel("warning");
            row.setLevelName("轻度预警");
        } else {
            row.setLevel("danger");
            row.setLevelName("重点关注");
        }
    }

    private List<String> buildRankingRisks(CommunityRankingVo row) {
        List<String> risks = new ArrayList<>();
        if (lessThan(row.getOccupancyRate(), "0.80")) {
            risks.add("入住率偏低");
        }
        if (lessThan(row.getPaymentRate(), "0.80")) {
            risks.add("缴费率偏低");
        }
        if (lessThan(row.getDeviceHealth(), "0.90")) {
            risks.add("设备异常较多");
        }
        if (lessThan(row.getRepairHealth(), "0.80")) {
            risks.add("报修处理压力较高");
        }
        if (lessThan(row.getComplaintHealth(), "0.80")) {
            risks.add("投诉压力较高");
        }
        if (lessThan(row.getParkingHealth(), "0.80")) {
            risks.add("停车压力较高");
        }
        return risks;
    }

    private boolean lessThan(BigDecimal value, String threshold) {
        return value != null && value.compareTo(new BigDecimal(threshold)) < 0;
    }

    private BigDecimal calcRate(Integer numerator, Integer denominator) {
        if (denominator == null || denominator == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(defaultZero(numerator))
                .divide(BigDecimal.valueOf(denominator), RANKING_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal calcHealth(Integer normalCount, Integer totalCount) {
        if (totalCount == null || totalCount == 0) {
            return BigDecimal.ONE;
        }
        BigDecimal value = BigDecimal.valueOf(Math.max(defaultZero(normalCount), 0))
                .divide(BigDecimal.valueOf(totalCount), RANKING_SCALE, RoundingMode.HALF_UP);
        return clampRate(value);
    }

    private BigDecimal calcPressureHealth(Integer pressureCount, Integer maxPressureCount) {
        if (maxPressureCount == null || maxPressureCount <= 0) {
            return BigDecimal.ONE;
        }
        BigDecimal pressureRate = BigDecimal.valueOf(defaultZero(pressureCount))
                .divide(BigDecimal.valueOf(maxPressureCount), RANKING_SCALE, RoundingMode.HALF_UP);
        return clampRate(BigDecimal.ONE.subtract(pressureRate));
    }

    private BigDecimal clampRate(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        if (value.compareTo(BigDecimal.ONE) > 0) {
            return BigDecimal.ONE;
        }
        return value;
    }

    private double toDouble(BigDecimal value) {
        if (value == null) {
            return 0D;
        }
        return value.doubleValue();
    }

    private Integer defaultZero(Integer value) {
        return value == null ? 0 : value;
    }

    private void checkRequiredCommunityPermission(Long communityId) {
        if (communityId == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "参数错误，请选择小区");
        }

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "未登录");
        }

        Community community = communityMapper.selectById(communityId);
        if (community == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "小区信息不存在");
        }
        if (loginUser.getUser().isAdmin()) {
            return;
        }
        if (!Objects.equals(community.getDeptId(), loginUser.getUser().getDeptId())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "无权查看该小区数据");
        }
    }

    private void checkCommunityPermission(Long communityId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "未登录");
        }

        if (loginUser.getUser().isAdmin()) {
            return;
        }

        if (communityId == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "参数错误，请检查筛选条件");
        }

        Community community = communityMapper.selectById(communityId);
        if (community == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "小区信息不存在");
        }
        if (!Objects.equals(community.getDeptId(), loginUser.getUser().getDeptId())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "无权查看该小区数据");
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DateTimeFormatter.BASIC_ISO_DATE);
        } catch (DateTimeParseException e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "日期格式错误，请使用 yyyyMMdd");
        }
    }

    private List<String> splitWords(String text) {
        List<String> words = new ArrayList<>();
        if (StringUtils.isBlank(text)) {
            return words;
        }

        Matcher matcher = TOKEN_PATTERN.matcher(text);
        while (matcher.find()) {
            String token = matcher.group();
            if (StringUtils.isBlank(token)) {
                continue;
            }
            if (containsChinese(token)) {
                words.addAll(jiebaSegmenter.sentenceProcess(token));
            } else {
                words.add(token.toLowerCase(Locale.ROOT));
            }
        }
        return words;
    }

    private boolean containsChinese(String value) {
        return value != null && value.matches(".*[\\u4e00-\\u9fa5].*");
    }

    private String normalizeWord(String word) {
        if (StringUtils.isBlank(word)) {
            return "";
        }
        return word.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^\\u4e00-\\u9fa5a-z0-9]", "");
    }

    private boolean isValidWord(String word) {
        if (StringUtils.isBlank(word)) {
            return false;
        }
        if (word.length() < 2) {
            return false;
        }
        if (word.matches("\\d+")) {
            return false;
        }
        return !stopWords.contains(word);
    }

    private String normalizeComplaintSuggestType(String type) {
        if (StringUtils.isBlank(type)) {
            return "";
        }
        String normalized = type.trim();
        if ("Complaint".equalsIgnoreCase(normalized) || "投诉".equals(normalized)) {
            return "Complaint";
        }
        if ("Suggest".equalsIgnoreCase(normalized) || "建议".equals(normalized)) {
            return "Suggest";
        }
        return normalized;
    }


    private int normalizeTfidfLimit(Integer topN) {
        if (topN == null || topN <= 0) {
            return DEFAULT_TFIDF_LIMIT;
        }
        return Math.min(MAX_TFIDF_LIMIT, topN);
    }

    private int countTfidfDocuments(List<AnalysisTextRow> rows, String type) {
        int count = 0;
        for (AnalysisTextRow row : rows) {
            if (row == null || !matchComplaintSuggestType(row.getComplaintSuggestType(), type) || StringUtils.isBlank(row.getContent())) {
                continue;
            }
            if (!extractValidWords(row.getContent()).isEmpty()) {
                count++;
            }
        }
        return count;
    }

    private List<TfidfKeywordVo> buildTfidfKeywords(List<AnalysisTextRow> rows, String type, int topN) {
        Map<String, Integer> tfMap = new HashMap<>();
        Map<String, Integer> dfMap = new HashMap<>();
        int documentCount = 0;

        for (AnalysisTextRow row : rows) {
            if (row == null || !matchComplaintSuggestType(row.getComplaintSuggestType(), type) || StringUtils.isBlank(row.getContent())) {
                continue;
            }

            List<String> words = extractValidWords(row.getContent());
            if (words.isEmpty()) {
                continue;
            }

            documentCount++;
            Set<String> documentWords = new HashSet<>();
            for (String word : words) {
                tfMap.merge(word, 1, Integer::sum);
                documentWords.add(word);
            }
            for (String word : documentWords) {
                dfMap.merge(word, 1, Integer::sum);
            }
        }

        if (documentCount == 0 || tfMap.isEmpty()) {
            return List.of();
        }

        List<TfidfKeywordVo> keywords = new ArrayList<>();
        BigDecimal totalTfidf = BigDecimal.ZERO;
        for (Map.Entry<String, Integer> entry : tfMap.entrySet()) {
            String word = entry.getKey();
            int tf = entry.getValue();
            int df = dfMap.getOrDefault(word, 0);
            double idfValue = Math.log((documentCount + 1D) / (df + 1D)) + 1D;
            double tfidfValue = tf * idfValue;

            TfidfKeywordVo keyword = new TfidfKeywordVo();
            keyword.setWord(word);
            keyword.setTf(tf);
            keyword.setDf(df);
            keyword.setIdf(toScale4(idfValue));
            keyword.setTfidf(toScale4(tfidfValue));
            keyword.setWeight(BigDecimal.ZERO);
            keywords.add(keyword);
            totalTfidf = totalTfidf.add(keyword.getTfidf());
        }

        if (totalTfidf.compareTo(BigDecimal.ZERO) > 0) {
            for (TfidfKeywordVo keyword : keywords) {
                keyword.setWeight(keyword.getTfidf().divide(totalTfidf, 4, RoundingMode.HALF_UP));
            }
        }

        return keywords.stream()
                .sorted((a, b) -> {
                    int compare = b.getTfidf().compareTo(a.getTfidf());
                    if (compare != 0) {
                        return compare;
                    }
                    compare = b.getTf().compareTo(a.getTf());
                    if (compare != 0) {
                        return compare;
                    }
                    return a.getWord().compareTo(b.getWord());
                })
                .limit(topN)
                .collect(Collectors.toList());
    }

    private BigDecimal toScale4(double value) {
        return BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP);
    }

    private List<String> extractValidWords(String content) {
        List<String> result = new ArrayList<>();
        for (String rawWord : splitWords(content)) {
            String word = normalizeWord(rawWord);
            if (isValidWord(word)) {
                result.add(word);
            }
        }
        return result;
    }

    private boolean matchComplaintSuggestType(String sourceType, String targetType) {
        if (StringUtils.isBlank(targetType)) {
            return true;
        }
        return targetType.equalsIgnoreCase(normalizeComplaintSuggestType(sourceType));
    }

    private List<WordCloudItemVo> buildWordCloud(List<AnalysisTextRow> rows, String type) {
        Map<String, Integer> counter = new HashMap<>();
        for (AnalysisTextRow row : rows) {
            if (row == null || !type.equalsIgnoreCase(normalizeComplaintSuggestType(row.getComplaintSuggestType()))) {
                continue;
            }
            for (String rawWord : splitWords(row.getContent())) {
                String word = normalizeWord(rawWord);
                if (!isValidWord(word)) {
                    continue;
                }
                counter.merge(word, 1, Integer::sum);
            }
        }
        return counter.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(WORD_LIMIT)
                .map(e -> new WordCloudItemVo(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private List<ParkingHourlyVo> buildHourlyData(List<ParkingPeriodRow> periods, LocalDate targetDate, Long parkingCount) {
        List<ParkingHourlyVo> result = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            LocalDateTime slotStart = targetDate.atTime(hour, 0);
            LocalDateTime slotEnd = slotStart.plusHours(1);
            int count = 0;
            for (ParkingPeriodRow row : periods) {
                if (row == null || row.getInTime() == null) {
                    continue;
                }
                if (row.getInTime().isBefore(slotEnd)
                        && (row.getOutTime() == null || row.getOutTime().isAfter(slotStart))) {
                    count++;
                }
            }

            BigDecimal pressureRate = calculatePressureRate(count, parkingCount);
            ParkingHourlyVo vo = new ParkingHourlyVo();
            vo.setHour(String.format("%02d:00", hour));
            vo.setCount(count);
            vo.setPressureRate(pressureRate);
            vo.setPressureLevel(resolvePressureLevel(pressureRate, parkingCount));
            result.add(vo);
        }
        return result;
    }


    private List<ParkingTrendVo> buildPresenceTrend7Days(Long communityId, LocalDate beginDate, LocalDate endDate, Long parkingCount) {
        List<ParkingTrendVo> result = new ArrayList<>();
        for (LocalDate cursor = beginDate; !cursor.isAfter(endDate); cursor = cursor.plusDays(1)) {
            LocalDateTime dayBegin = cursor.atStartOfDay();
            LocalDateTime dayEnd = cursor.plusDays(1).atStartOfDay();
            List<ParkingPeriodRow> periods = analysisMapper.selectParkingPeriods(communityId, dayBegin, dayEnd);
            List<ParkingHourlyVo> hourlyData = buildHourlyData(periods, cursor, parkingCount);
            int peakCount = hourlyData.stream()
                    .map(ParkingHourlyVo::getCount)
                    .filter(Objects::nonNull)
                    .max(Integer::compareTo)
                    .orElse(0);

            ParkingTrendVo vo = new ParkingTrendVo();
            vo.setDate(cursor.format(DateTimeFormatter.BASIC_ISO_DATE));
            vo.setCount(peakCount);
            vo.setHourlyData(hourlyData);
            result.add(vo);
        }
        return result;
    }

    private boolean isFixedParkingRecord(String recordType) {
        if (StringUtils.isBlank(recordType)) {
            return false;
        }
        String normalized = recordType.trim().toLowerCase(Locale.ROOT);
        return normalized.contains("固定") || "fixed".equals(normalized) || "fixed_car".equals(normalized);
    }

    private boolean isTempParkingRecord(String recordType) {
        if (StringUtils.isBlank(recordType)) {
            return false;
        }
        String normalized = recordType.trim().toLowerCase(Locale.ROOT);
        return normalized.contains("临停") || normalized.contains("临时") || "temp".equals(normalized)
                || "temporary".equals(normalized) || "temporary_car".equals(normalized);
    }

    private BigDecimal calculateRate(long count, long total) {
        if (total <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(count).divide(BigDecimal.valueOf(total), 3, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePressureRate(Integer count, Long parkingCount) {
        if (parkingCount == null || parkingCount <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(count == null ? 0 : count)
                .divide(BigDecimal.valueOf(parkingCount), 3, RoundingMode.HALF_UP);
    }

    private String resolvePressureLevel(BigDecimal pressureRate, Long parkingCount) {
        if (parkingCount == null || parkingCount <= 0) {
            return "unknown";
        }
        if (pressureRate.compareTo(new BigDecimal("0.50")) < 0) {
            return "normal";
        }
        if (pressureRate.compareTo(new BigDecimal("0.80")) <= 0) {
            return "warning";
        }
        return "danger";
    }

    private String nextHour(String hour) {
        int value = Integer.parseInt(hour.substring(0, 2));
        return value == 23 ? "24:00" : String.format("%02d:00", value + 1);
    }

    private String resolvePressureText(String level, Long parkingCount) {
        if (parkingCount == null || parkingCount <= 0 || "unknown".equals(level)) {
            return "暂无车位数据，无法判断停车压力";
        }
        return switch (level) {
            case "warning" -> "停车压力较高";
            case "danger" -> "停车压力高峰";
            default -> "停车压力正常";
        };
    }

    private List<ParkingTrendVo> fillTrend7Days(List<ParkingTrendVo> rows, LocalDate beginDate, LocalDate endDate) {
        Map<String, Integer> source = new HashMap<>();
        for (ParkingTrendVo row : rows) {
            if (row != null && StringUtils.isNotBlank(row.getDate())) {
                source.put(row.getDate(), row.getCount() == null ? 0 : row.getCount());
            }
        }

        List<ParkingTrendVo> result = new ArrayList<>();
        for (LocalDate cursor = beginDate; !cursor.isAfter(endDate); cursor = cursor.plusDays(1)) {
            String date = cursor.format(DateTimeFormatter.BASIC_ISO_DATE);
            ParkingTrendVo vo = new ParkingTrendVo();
            vo.setDate(date);
            vo.setCount(source.getOrDefault(date, 0));
            result.add(vo);
        }
        return result;
    }
}
