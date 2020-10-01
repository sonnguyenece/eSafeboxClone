package com.ecpay.esafebox.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ecpay.entities.ecbox.Box;
import com.ecpay.entities.ecbox.Manufacture;
import com.ecpay.esafebox.dto.BoxDto;
import com.ecpay.esafebox.dto.ManufactedBoxDto;
import com.ecpay.esafebox.dto.ManufactureDto;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.enumeration.fieldname.ManufactureFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.ManufactureListFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.PaginationSortingFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.mapper.ManufactedBoxMapper;
import com.ecpay.esafebox.mapper.ManufactureMapper;
import com.ecpay.esafebox.repository.BoxDslRepository;
import com.ecpay.esafebox.repository.ManufactureDslRepository;
import com.ecpay.esafebox.search.ManufactureSearchCriteria;
import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.EsafeboxUtils;
import com.querydsl.core.types.Predicate;

@Service
@Transactional
public class ManufactureDslService {
	private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.SERVICE);

	@Autowired
	private ManufactureDslRepository manufactureDslRepository;

	@Autowired
	private BoxDslRepository boxDslRepository;

	@Autowired
	private ManufactureMapper manufactureMapper;
//	@Autowired
//	private BoxMapper boxMapper;
	
	@Autowired
	private ManufactedBoxMapper manufactedBoxMapper;

	public ManufactureDto getManufacturebyId(Long id) throws ESafeboxException {
		Optional<Manufacture> manufacture = manufactureDslRepository.findById(id);
		// check null
		ManufactureDto dto = null;
		if (manufacture.isPresent()) {
			dto = manufactureMapper.toManufactureDto(manufacture.get());
//			dto.setBoxList(boxMapper.toBoxDtos(new ArrayList<Box>(manufacture.get().getListBox())));
		}

		return dto;
	}

	@SuppressWarnings("unchecked")
	public PagedData<ManufactedBoxDto> getManufactedBox(Long logId, Map<String, Object> data, Pageable pageable)
			throws ESafeboxException {
		logger.info("[{}] Get list userbox of {} with paging {}", logId, data);
		try {
			Long manufactureId = EsafeboxUtils.getFieldValueAsLong(
					ManufactureFieldName.MANUFACTURE_ID.getEsafeboxFieldName().getFieldName(), data);
			
			String directionValue = EsafeboxUtils.getFieldValueAsString(
					PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName().getFieldName(), data);
			String defaultDirectionValue = PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName().getDefaultValue()
					.toString();
			directionValue = StringUtils.isEmpty(directionValue) ? defaultDirectionValue : directionValue;
			Object sortsObject = data.get(ManufactureListFieldName.SORTS.getEsafeboxFieldName().getFieldName());
			List<String> sorts = (sortsObject != null && sortsObject instanceof List) ? (List<String>) sortsObject
					: Arrays.asList(ManufactureFieldName.MANUFACTURE_ID.getEsafeboxFieldName().getEntityFieldName());

			
			Page<Box> boxPage = boxDslRepository.findByManufactureId(manufactureId, pageable);
			
			PagedData<ManufactedBoxDto> boxes= PagedData.<ManufactedBoxDto>builder().pageSize(boxPage.getSize()).pageNumber(boxPage.getNumber() + 1)
					.totalPages(boxPage.getTotalPages()).totalElements(boxPage.getTotalElements()).sorts(sorts)
					.order(directionValue).data(manufactedBoxMapper.toBoxDtos(boxPage.getContent())).build();
			

			return boxes;
		} catch (Exception e) {
			logger.error("[{}] Exception: {}", logId, e.getMessage(), e);
			throw new ESafeboxException("0001", e.getMessage());
		}
	}

	public Manufacture getUniqueManufacture(String code) {
		return manufactureDslRepository.findOneByCode(code);
	}

	public Manufacture createOrUpdateManufacture(Manufacture entity) {
		return manufactureDslRepository.save(entity);
	}

	@SuppressWarnings("unchecked")
	public PagedData<ManufactureDto> searchManufacture(Long logId, Map<String, Object> data, Pageable pageable)
			throws ESafeboxException {
		logger.info("[{}] Get list userbox of {} with paging {}", logId, data, pageable);
		try {

			Predicate predicate = ManufactureSearchCriteria.builder().data(data).build().buildPredicate();
			Page<Manufacture> manufacturePage = manufactureDslRepository.findAll(predicate, pageable);

			String directionValue = EsafeboxUtils.getFieldValueAsString(
					PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName().getFieldName(), data);
			String defaultDirectionValue = PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName().getDefaultValue()
					.toString();
			directionValue = StringUtils.isEmpty(directionValue) ? defaultDirectionValue : directionValue;
			Object sortsObject = data.get(ManufactureListFieldName.SORTS.getEsafeboxFieldName().getFieldName());
			List<String> sorts = (sortsObject != null && sortsObject instanceof List) ? (List<String>) sortsObject
					: Arrays.asList(
							ManufactureListFieldName.MANUFACTURE_CODE.getEsafeboxFieldName().getEntityFieldName());

			return PagedData.<ManufactureDto>builder().pageSize(manufacturePage.getSize())
					.pageNumber(manufacturePage.getNumber() + 1).totalPages(manufacturePage.getTotalPages())
					.totalElements(manufacturePage.getTotalElements()).sorts(sorts).order(directionValue)
//				.data(manuEntitysToDtos(manufacturePage.getContent(), boxStatus))
					.data(manufactureMapper.toManufactureDtos(manufacturePage.getContent())).build();
		} catch (Exception e) {
			logger.error("[{}] Exception: {}", logId, e.getMessage(), e);
			throw new ESafeboxException("0001", e.getMessage());
		}
	}

//	public List<ManufactureDto> manuEntitysToDtos(List<Manufacture> manufactures, String boxStatus) {
//	    if ( manufactures == null ) {
//	    	return null;
//	    }
//	    List<ManufactureDto> list = new ArrayList<ManufactureDto>();
//	    for ( Manufacture manufacture : manufactures) {
//	    	ManufactureDto mfd = new ManufactureDto();
//	    	mfd.setManufactureCode(manufacture.getCode());
//	    	mfd.setBoxtypeId(manufacture.getBoxtypeId());
//	    	mfd.setFactureStatus(manufacture.getStatus());
//	    	
//	    	mfd.setCreatedDate(DATE_FORMATTER.format(manufacture.getCreated()));
//	    	
//	    	mfd.setQuantityBox(manufacture.getQuantity());
//	    	
//	    	List<Box> boxList = boxDslRepository.findByManufactureIdAndStatus(manufacture.getId(), boxStatus);
//	    	List<BoxDto> boxDtoList = boxEntitysToDtos(boxList);
//	    			
//	    	mfd.setBoxList(boxDtoList);
//	    	
//	    	if(boxList != null) {
//	    		if(boxList.size() > 0) {
//		    		list.add(mfd);
//		    	}
//	    	}
//	    	
//	    }
//	    return list;
//	}

	public List<BoxDto> boxEntitysToDtos(List<Box> boxs) {
		if (boxs == null) {
			return null;
		}

		List<BoxDto> list = new ArrayList<BoxDto>();
		for (Box box : boxs) {
			BoxDto bd = new BoxDto();
			bd.setBoxId(box.getId());
			bd.setBoxSerial(box.getSerial());
			bd.setBoxStatus(box.getStatus());

			list.add(bd);
		}

		return list;
	}
}
