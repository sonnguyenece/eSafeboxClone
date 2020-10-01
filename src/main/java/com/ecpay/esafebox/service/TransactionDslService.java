package com.ecpay.esafebox.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ecpay.entities.ecbox.Box;
import com.ecpay.entities.ecbox.Transaction;
import com.ecpay.entities.ecbox.UserBox;
import com.ecpay.entities.ecbox.enumeration.TransactionDataType;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.TransactionDto;
import com.ecpay.esafebox.dto.UserBoxDto;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.BoxFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.EdongEsafeboxFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.TransactionFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.UserBoxFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.mapper.TransactionMapper;
import com.ecpay.esafebox.mapper.UserBoxMapper;
import com.ecpay.esafebox.repository.BoxDslRepository;
import com.ecpay.esafebox.repository.TransactionDslRepository;
import com.ecpay.esafebox.repository.UserBoxDslRepository;
import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.EsafeboxUtils;
import com.ecpay.esafebox.utils.TimeUtils;
import com.google.common.collect.Maps;

@Service
@Transactional
public class TransactionDslService {
	private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.SERVICE);

	@Autowired
	UserBoxDslRepository userBoxDslRepository;

	@Autowired
	BoxDslRepository boxDslRepository;

	@Autowired
	UserBoxMapper userBoxMapper;
	
    @Autowired
    private TransactionDslRepository transactionDslRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    public PagedData<TransactionDto> getListTransaction(Long logId, Map<String, Object> data, Pageable pageable)
            throws ESafeboxException {
        logger.info("[{}] Get list transaction of {} with paging {}", logId, data, pageable);
        try {
            Long sender = EsafeboxUtils
                    .getFieldValueAsLong(TransactionFieldName.SENDER.getEsafeboxFieldName().getFieldName(), data);
            Long receiver = EsafeboxUtils
                    .getFieldValueAsLong(TransactionFieldName.RECEIVER.getEsafeboxFieldName().getFieldName(), data);
            String t =  EsafeboxUtils.getFieldValueAsString(
                    TransactionFieldName.TRANSACTION_TYPE.getEsafeboxFieldName().getFieldName(), data);
            TransactionDataType transactionType = TransactionDataType.valueOf(t);
            String fDate = EsafeboxUtils
                    .getFieldValueAsString(TransactionFieldName.FROM_DATE.getEsafeboxFieldName().getFieldName(), data);
            String tDate = EsafeboxUtils
                    .getFieldValueAsString(TransactionFieldName.TO_DATE.getEsafeboxFieldName().getFieldName(), data);
            Timestamp fromDate = Timestamp.valueOf(TimeUtils.convertString2LocalDate(fDate).atStartOfDay());
            Timestamp toDate = Timestamp.valueOf(TimeUtils.convertString2LocalDate(tDate).atTime(LocalTime.MAX));
            Page<Transaction> pageTransaction = transactionDslRepository.getListBoxTransaction(sender, receiver,
                    transactionType, fromDate, toDate, pageable);
            return PagedData.<TransactionDto>builder().totalElements(pageTransaction.getTotalElements())
                    .totalPages(pageTransaction.getTotalPages()).pageNumber(pageable.getPageNumber() + 1)
                    .pageSize(pageable.getPageSize())
                    .data(transactionMapper.toTransactionDtos(pageTransaction.getContent())).build();
        } catch (Exception e) {
            logger.error("[{}] Exception: {}", logId, e.getMessage(), e);
            throw new ESafeboxException("0001", e.getMessage());
        }

    }

	public PagedData<UserBoxDto> getListUserBox(Long logId, Map<String, Object> data, Pageable pageable)
			throws ESafeboxException {
		logger.info("[{}] Get list userbox of {} with paging {}", logId, data, pageable);
		try {
			Long userId = EsafeboxUtils
					.getFieldValueAsLong(UserBoxFieldName.USER_ID.getEsafeboxFieldName().getFieldName(), data);
			Long boxTypeId = EsafeboxUtils
					.getFieldValueAsLong(BoxFieldName.BOX_TYPE.getEsafeboxFieldName().getFieldName(), data);
			if (Objects.isNull(boxTypeId) || StringUtils.isEmpty(boxTypeId)) {
				boxTypeId = Long.valueOf(0l);
			}
			Long boxSerial = EsafeboxUtils
					.getFieldValueAsLong(BoxFieldName.BOX_SERIAL.getEsafeboxFieldName().getFieldName(), data);
			String fDate = EsafeboxUtils
					.getFieldValueAsString(BoxFieldName.FROM_DATE.getEsafeboxFieldName().getFieldName(), data);
			String tDate = EsafeboxUtils
					.getFieldValueAsString(BoxFieldName.TO_DATE.getEsafeboxFieldName().getFieldName(), data);
			LocalDateTime fromDate = TimeUtils.convertString2LocalDate(fDate).atStartOfDay();
			LocalDateTime toDate = TimeUtils.convertString2LocalDate(tDate).atTime(LocalTime.MAX);
			Page<UserBox> pageBox = userBoxDslRepository.getListUserBox(userId, boxTypeId, boxSerial, fromDate, toDate,
					pageable);
			return PagedData.<UserBoxDto>builder().totalElements(pageBox.getTotalElements())
					.totalPages(pageBox.getTotalPages()).pageNumber(pageable.getPageNumber() + 1)
					.pageSize(pageable.getPageSize()).data(userBoxMapper.toUserBoxDtos(pageBox.getContent())).build();
		} catch (Exception e) {
			logger.error("[{}] Exception: {}", logId, e.getMessage(), e);
			throw new ESafeboxException("0001", e.getMessage());
		}
	}

	public UserBoxDto activeUserBox(Long logId, Map<String, Object> data) throws ESafeboxException {
		logger.info("[{}] Activate userBox {}", logId, data);
		Long userId = EsafeboxUtils.getFieldValueAsLong(UserBoxFieldName.USER_ID.getEsafeboxFieldName().getFieldName(),
				data);
		String terminalId = EsafeboxUtils
				.getFieldValueAsString(UserBoxFieldName.TERMINAL_ID.getEsafeboxFieldName().getFieldName(), data);
		String terminalInfo = EsafeboxUtils
				.getFieldValueAsString(UserBoxFieldName.TERMINAL_INFO.getEsafeboxFieldName().getFieldName(), data);

		// check UC007 throw S108
		UserBox ub;
		List<Box> boxes = userBoxDslRepository.findBoxByUserIdAndTerminalId(userId, terminalId);
		if (boxes.size() == 1) {
			//userBox is existed
			ub = userBoxDslRepository.findByUnique(userId, boxes.get(0).getId(), terminalId).get();
			return userBoxMapper.toUserBoxDto(ub);
		} else {
			//get randomly box
			Optional<Box> ob = boxDslRepository.findByBoxTypeCode();
			if (!ob.isPresent()) {
				//box is not found
				logger.info("[{}] Validating for activate userBox FAIL - Reason: BoxId does not exist --> END.");
				throw new ESafeboxException(EsafeboxErrorCode.BOX_DOES_NOT_EXISTS.getErrorCode(),
						EsafeboxErrorCode.BOX_DOES_NOT_EXISTS.getDescription());
			} else {
				//box exists, update status
				Box b =ob.get();
				b.setStatus("Y");
				b.setActived(LocalDateTime.now());
				boxDslRepository.saveAndFlush(b);
//				boxDslRepository.updateStatusById(ob.get().getId());
				String uuid=UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
				
				//create userBox
				ub = UserBox.builder().userId(ob.get().getId()).box(ob.get()).boxId(ob.get().getId())
						.boxKey(Hex.encodeHexString(uuid.getBytes()))
						.terminalId(terminalId).terminalInfo(terminalInfo).build();
				
				return userBoxMapper.toUserBoxDto(userBoxDslRepository.saveAndFlush(ub));
			}
		}
	}
	
	public Map<String, Object> edongEsafeboxInitTransaction(Long logId, Map<String, Object> dataMap) throws ESafeboxException {
		Map<String, Object> output = Maps.newHashMap();
		try {
			//UC001:
			Long receiveBoxSerial = EsafeboxUtils.getFieldValueAsLong(EdongEsafeboxFieldName.RECEIVER_SERIAL.getEsafeboxFieldName().getFieldName(), dataMap);
			if (Objects.isNull(receiveBoxSerial)) {
				throw new ESafeboxException(EsafeboxErrorCode.PARAM_IS_MISSED.getErrorCode(),
						EsafeboxErrorCode.PARAM_IS_MISSED.getDescription(Arrays.asList(EdongEsafeboxFieldName.RECEIVER_SERIAL.getEsafeboxFieldName().getFieldName())));
			}
			//UC002: check receive box exists by serial
			List<UserBox> userBoxList = userBoxDslRepository.findByListBoxSerial(Arrays.asList(receiveBoxSerial));
			if(Objects.isNull(userBoxList) || userBoxList.isEmpty()) {
				throw new ESafeboxException(EsafeboxErrorCode.BOX_SERIAL_NOT_EXISTS.getErrorCode(),
						EsafeboxErrorCode.BOX_SERIAL_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(receiveBoxSerial))));
			}
			//UC13: Verify keyBoxClient
			String keyBoxClient = EsafeboxUtils.getFieldValueAsString(EdongEsafeboxFieldName.KEYBOX_CLIENT.getEsafeboxFieldName().getFieldName(), dataMap);
			UserBox userBox = userBoxList.get(0);
			String boxKp = userBox.getBoxKp();
			String boxKey = userBox.getBoxKey();
			String alg = userBox.getAlgorithm();
			EsafeboxUtils.verifyKeyBoxClient(boxKp, alg, boxKey, keyBoxClient);
			output.put("aliasBox", userBox.getAlias());
			output.put("kpBox", Base64.getEncoder().encodeToString(Hex.decodeHex(boxKp)));
		} catch (Exception e) {
			logger.error("[{}][edongEsafeboxInitTransaction] Exception: {}", logId, e.getMessage(), e);
			throw new ESafeboxException("0001", e.getMessage());
		}
		return output;
	}
	
	public Map<String, Object> esafeboEdongInitTransaction(Long logId, Map<String, Object> dataMap) throws ESafeboxException {
		Map<String, Object> output = Maps.newHashMap();
		try {
			//UC001:
			Long receiveBoxSerial = EsafeboxUtils.getFieldValueAsLong(EdongEsafeboxFieldName.RECEIVER_SERIAL.getEsafeboxFieldName().getFieldName(), dataMap);
			if (Objects.isNull(receiveBoxSerial)) {
				throw new ESafeboxException(EsafeboxErrorCode.PARAM_IS_MISSED.getErrorCode(),
						EsafeboxErrorCode.PARAM_IS_MISSED.getDescription(Arrays.asList(EdongEsafeboxFieldName.RECEIVER_SERIAL.getEsafeboxFieldName().getFieldName())));
			}
			//UC002: check receive box exists by serial
			List<UserBox> userBoxList = userBoxDslRepository.findByListBoxSerial(Arrays.asList(receiveBoxSerial));
			if(Objects.isNull(userBoxList) || userBoxList.isEmpty()) {
				throw new ESafeboxException(EsafeboxErrorCode.BOX_SERIAL_NOT_EXISTS.getErrorCode(),
						EsafeboxErrorCode.BOX_SERIAL_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(receiveBoxSerial))));
			}
			//UC13: Verify keyBoxClient
			String keyBoxClient = EsafeboxUtils.getFieldValueAsString(EdongEsafeboxFieldName.KEYBOX_CLIENT.getEsafeboxFieldName().getFieldName(), dataMap);
			UserBox userBox = userBoxList.get(0);
			String boxKp = userBox.getBoxKp();//Hexadecimal
			String boxKey = userBox.getBoxKey();
			String alg = "";
			if (!EsafeboxUtils.verifyKeyBoxClient(boxKp, alg, boxKey, keyBoxClient)) {
				throw new ESafeboxException(EsafeboxErrorCode.INVALID_KEY_BOX_CLIENT.getErrorCode(),
						EsafeboxErrorCode.INVALID_KEY_BOX_CLIENT.getDescription());
			}
			output.put("aliasBox", userBox.getAlias());
			output.put("kpBox", Base64.getEncoder().encodeToString(Hex.decodeHex(boxKp)));
		} catch (Exception e) {
			logger.error("[{}][esafeboEdongInitTransaction] Exception: {}", logId, e.getMessage(), e);
			throw new ESafeboxException("0001", e.getMessage());
		}
		return output;
	}
}
