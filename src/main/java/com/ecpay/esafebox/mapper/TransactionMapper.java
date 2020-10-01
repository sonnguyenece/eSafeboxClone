package com.ecpay.esafebox.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

import com.ecpay.entities.ecbox.Transaction;
import com.ecpay.esafebox.dto.TransactionDto;

@Component
@Mapper(componentModel = "spring")
@MapperConfig(implementationPackage = "com.ecpay.esafebox.mapper")
public interface TransactionMapper {
	@Mappings({ @Mapping(target = "transactionId", source = "transaction.id"),
			@Mapping(target = "sender", source = "transaction.sender"),
			@Mapping(target = "receiver", source = "transaction.receiver"),
			@Mapping(target = "transactionTime", source = "transaction.transactionTime"),
			@Mapping(target = "transactionType", source = "transaction.transactionDataType")
//			@Mapping(target = "transactionType", expression = "java(transaction.getTransactionDataType() == null ? null : transaction.getTransactionDataType())")
	})
	TransactionDto toTransactionDto(Transaction transaction);

	List<TransactionDto> toTransactionDtos(List<Transaction> boxs);
}
