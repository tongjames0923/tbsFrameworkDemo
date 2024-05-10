package sec.secpart;

import org.apache.ibatis.annotations.Mapper;
import tbs.framework.sql.interfaces.mappers.IFrameworkMapper;

@Mapper
public interface MessageMapper extends IFrameworkMapper<MessageEntity, Long> {
}
